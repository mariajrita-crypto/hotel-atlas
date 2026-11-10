# Revisão de interface e correções — Atlas Hotel

## Alterações
- Aplicação e trocas de tela reaplicam o modo de tela cheia.
- Área administrativa ganhou uma seta de retorno com histórico de navegação.
- Área do hóspede ganhou uma seta para retornar à tela de acesso.
- Telas internas são acomodadas em uma área expansível para aproveitar a resolução disponível.
- A tabela financeira não apaga mais os pagamentos carregados ao abrir a tela.
- A atualização de tabelas administrativas recarrega hóspedes, pagamentos e solicitações.
- Indicadores do dashboard distinguem quartos ocupados, reservados, disponíveis e em manutenção/bloqueados.
- Acesso reflexivo do Gson ao pacote de persistência foi declarado no `module-info.java`.
- Cadastro e edição de quartos validam os campos essenciais e valores de diária.

## Observações de validação
- Todos os arquivos FXML foram analisados e estão com XML bem formado.
- A compilação/execução JavaFX não pôde ser concluída neste ambiente porque o Maven e as dependências locais não estão disponíveis. Execute `mvn clean javafx:run` em um ambiente com JDK 21 e Maven.
