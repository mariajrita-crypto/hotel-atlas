# Atlas Hotel — funcionalidades atualizadas

## O que está disponível no projeto

### Área do hóspede
- Busca de quartos por período, capacidade e categoria.
- Consulta e cancelamento de reservas futuras.
- Central de serviços com Room Service, lavanderia, transfer, solicitações especiais e chat com a recepção.
- Acompanhamento das solicitações e das respostas da recepção.
- Check-in e check-out digitais vinculados às reservas do CPF autenticado.
- Preparação de e-mail com os dados da hospedagem para solicitar a nota fiscal.

### Painel administrativo
- Cadastro, edição e exclusão de quartos.
- Campos de número, nome, descrição, categoria, capacidade, diária, qualidade e comodidades.
- Upload/substituição de fotos dos quartos e visualização do caminho da foto na tabela administrativa.
- Validação no serviço de dados para impedir números de quarto duplicados, inclusive em alterações.
- Bloqueio da exclusão de quartos com reservas/hospedagens ativas.
- Atendimento das solicitações, atualização de status e resposta ao hóspede.

### Persistência
Os dados são mantidos localmente em `~/.hotel-atlas/`:
- `quartos.json`
- `reservas.json`
- `pagamentos.json`
- `solicitacoes.json`
- `fotos-quartos/`

O diretório é criado automaticamente. Faça backup dessa pasta antes de reinstalar ou trocar de computador.

## Limites de integração que exigem configuração externa
- O fluxo de nota fiscal abre o aplicativo de e-mail padrão com um pedido e o resumo da hospedagem. Ele não emite uma NFS-e nem envia o e-mail sozinho. Para isso, é necessário configurar o provedor SMTP e integrar o sistema fiscal/ERP do hotel, com credenciais e dados fiscais oficiais.
- O chat atual é uma caixa de mensagens persistida por solicitações, com respostas da recepção; não é um chat em tempo real entre dispositivos.
- O check-in/check-out registra a mudança de status no sistema local; validação documental, assinatura, fechaduras e integrações de PMS exigem serviços externos.

## Executar
Requisitos: JDK 21 e Maven.
Na pasta `HOTEL_ATLAS`, execute:

```bash
mvn clean javafx:run
```

A configuração do Maven foi ajustada para usar somente `release=21`, evitando conflito entre `release` e `source/target`.
