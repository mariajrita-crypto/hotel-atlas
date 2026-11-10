package com.hotel.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public final class AdminService {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Path.of(System.getProperty("user.home", "."))
            .toAbsolutePath().normalize().resolve(".hotel-atlas").resolve("administradores.json");
    private static final int ITERATIONS = 210_000;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private AdminService() {}

    public static synchronized boolean cadastroInicialDisponivel() {
        return carregar().isEmpty();
    }

    public static synchronized void cadastrarPrimeiroAdministrador(
            String nome, String email, String senha, String confirmar) {
        if (!cadastroInicialDisponivel()) {
            throw new IllegalStateException("O cadastro inicial de administrador já foi realizado.");
        }
        if (nome == null || nome.trim().length() < 2) {
            throw new IllegalArgumentException("Informe o nome do administrador.");
        }
        String normalizedEmail = normalizarEmail(email);
        if (!normalizedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (senha == null || senha.length() < 10) {
            throw new IllegalArgumentException("A senha do administrador deve ter pelo menos 10 caracteres.");
        }
        if (!senha.equals(confirmar)) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }

        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        AdminAccount account = new AdminAccount();
        account.name = nome.trim();
        account.email = normalizedEmail;
        account.salt = Base64.getEncoder().encodeToString(salt);
        account.passwordHash = hash(senha.toCharArray(), salt);
        account.iterations = ITERATIONS;
        List<AdminAccount> accounts = new ArrayList<>();
        accounts.add(account);
        salvar(accounts);
    }

    public static synchronized boolean autenticar(String email, String senha) {
        if (email == null || senha == null) return false;
        String normalizedEmail = normalizarEmail(email);
        for (AdminAccount account : carregar()) {
            if (account.email.equals(normalizedEmail)) {
                try {
                    byte[] salt = Base64.getDecoder().decode(account.salt);
                    byte[] expected = Base64.getDecoder().decode(account.passwordHash);
                    byte[] actual = Base64.getDecoder().decode(hash(senha.toCharArray(), salt, account.iterations));
                    return MessageDigest.isEqual(expected, actual);
                } catch (RuntimeException ex) {
                    return false;
                }
            }
        }
        return false;
    }

    public static synchronized String nomeDoAdministrador(String email) {
        String normalizedEmail = normalizarEmail(email);
        return carregar().stream().filter(a -> a.email.equals(normalizedEmail))
                .map(a -> a.name).findFirst().orElse("Administrador Atlas");
    }

    private static String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String hash(char[] password, byte[] salt) {
        return hash(password, salt, ITERATIONS);
    }

    private static String hash(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
        try {
            byte[] key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(key);
        } catch (Exception ex) {
            throw new IllegalStateException("Não foi possível proteger a senha.", ex);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(password, '\0');
        }
    }

    private static List<AdminAccount> carregar() {
        try {
            if (!Files.exists(FILE)) return new ArrayList<>();
            List<AdminAccount> accounts = GSON.fromJson(
                    Files.readString(FILE, StandardCharsets.UTF_8),
                    new TypeToken<List<AdminAccount>>(){}.getType());
            return accounts == null ? new ArrayList<>() : accounts;
        } catch (Exception ex) {
            throw new IllegalStateException("Não foi possível ler o cadastro de administradores.", ex);
        }
    }

    private static void salvar(List<AdminAccount> accounts) {
        try {
            Files.createDirectories(FILE.getParent());
            Path temp = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temp, GSON.toJson(accounts), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Não foi possível salvar o administrador neste computador.", ex);
        }
    }

    private static final class AdminAccount {
        String name;
        String email;
        String salt;
        String passwordHash;
        int iterations;
    }
}
