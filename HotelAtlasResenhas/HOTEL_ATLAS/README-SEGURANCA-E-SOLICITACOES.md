# Correções de solicitações e acesso administrativo

## Solicitações / Chat
- A tela agora usa `SolicitacoesController`, separado do controlador principal do dashboard.
- Colunas são configuradas explicitamente e os pedidos existentes são carregados ao abrir a tela.
- Botão Atualizar recarrega os pedidos persistidos.
- Botão Atender permite atualizar o status e enviar resposta para o hóspede.
- As respostas são salvas em `~/.hotel-atlas/solicitacoes.json` e aparecem na área do hóspede.

## Administrador
- Removidos da tela inicial os dados e a senha de demonstração do funcionário.
- O login tem a opção Administrador.
- A aba Cadastrar ADM permite criar o primeiro administrador uma única vez.
- Depois do primeiro cadastro, a aba fica bloqueada para evitar auto-cadastro aberto de novos administradores.
- A senha não é armazenada em texto puro: usa PBKDF2-HMAC-SHA256 com salt aleatório e 210.000 iterações.
- Cadastro armazenado em `~/.hotel-atlas/administradores.json`.
- Para entrar: selecione Administrador e use o e-mail e a senha definidos no cadastro.

## Testes
- Todos os FXML foram validados como XML bem formado.
- A compilação e o teste de execução JavaFX precisam ser feitos num ambiente com JDK 21, Maven e dependências JavaFX/Gson.
