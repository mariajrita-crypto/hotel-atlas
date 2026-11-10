package com.hotel;

import com.hotel.service.ClienteService;
import com.hotel.service.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Locale;

public class LoginController {
    @FXML private ComboBox<String> cbPerfil;
    @FXML private TextField txtIdentificador;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblErro;
    @FXML private TextField txtCpfCadastro;
    @FXML private TextField txtTelefoneCadastro;
    @FXML private PasswordField txtSenhaCadastro;
    @FXML private PasswordField txtConfirmarSenha;
    @FXML private Label lblCadastroStatus;

    @FXML public void initialize() {
        cbPerfil.getItems().setAll("Cliente", "Funcionário");
        cbPerfil.setValue("Cliente");
        atualizarIdentificador();
        cbPerfil.setOnAction(e -> atualizarIdentificador());
        lblErro.setText("");
        lblCadastroStatus.setText("Seus dados ficam salvos neste computador.");
    }

    private void atualizarIdentificador() {
        boolean funcionario = "Funcionário".equals(cbPerfil.getValue());
        txtIdentificador.setPromptText(funcionario ? "E-mail do funcionário" : "Seu CPF (somente números)");
    }

    @FXML private void entrar() {
        String identificador = txtIdentificador.getText() == null ? "" : txtIdentificador.getText().trim();
        String senha = txtSenha.getText() == null ? "" : txtSenha.getText();
        boolean employee = "Funcionário".equals(cbPerfil.getValue());
        if (identificador.isBlank() || senha.isBlank()) {
            lblErro.setText("Preencha seu CPF/e-mail e sua senha.");
            return;
        }
        try {
            boolean valid;
            if (employee) {
                String email = identificador.toLowerCase(Locale.ROOT);
                valid = email.equals("funcionario@atlas.com") && senha.equals("123456");
                identificador = email;
            } else {
                valid = ClienteService.autenticar(identificador, senha);
                identificador = ClienteService.somenteNumeros(identificador);
            }
            if (!valid) {
                lblErro.setText("Dados incorretos. Confira o acesso e tente novamente.");
                txtSenha.clear();
                txtSenha.requestFocus();
                return;
            }
            Session.login(employee ? "Funcionário Atlas" : "Cliente", identificador, employee ? "FUNCIONARIO" : "CLIENTE");
            abrirTela(employee ? "/com/example/hotel_atlas/main-view.fxml" : "/com/example/hotel_atlas/cliente-view.fxml");
        } catch (Exception e) {
            lblErro.setText("Não foi possível ler os cadastros locais. Verifique as permissões do computador.");
            e.printStackTrace();
        }
    }

    @FXML private void cadastrar() {
        String cpf = txtCpfCadastro.getText();
        String telefone = txtTelefoneCadastro.getText();
        String senha = txtSenhaCadastro.getText();
        String confirmar = txtConfirmarSenha.getText();
        if (senha == null || !senha.equals(confirmar)) {
            lblCadastroStatus.setText("As senhas não coincidem.");
            return;
        }
        try {
            ClienteService.cadastrar(cpf, telefone, senha);
            lblCadastroStatus.setText("Conta criada! Agora entre usando seu CPF e senha.");
            txtIdentificador.setText(ClienteService.somenteNumeros(cpf));
            txtSenha.setText(senha);
            txtCpfCadastro.clear();
            txtTelefoneCadastro.clear();
            txtSenhaCadastro.clear();
            txtConfirmarSenha.clear();
        } catch (IllegalArgumentException e) {
            lblCadastroStatus.setText(e.getMessage());
        } catch (Exception e) {
            lblCadastroStatus.setText("Não foi possível salvar o cadastro neste computador.");
            e.printStackTrace();
        }
    }

    private void abrirTela(String caminho) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminho));
            Scene scene = new Scene(loader.load(), 1280, 768);
            Stage stage = (Stage) txtIdentificador.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hotel Atlas - " + (Session.isEmployee() ? "Área do Funcionário" : "Área do Cliente"));
            stage.centerOnScreen();
        } catch (Exception e) {
            lblErro.setText("Não foi possível abrir sua área. Verifique os arquivos FXML.");
            e.printStackTrace();
        }
    }
}
