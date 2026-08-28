# Notificações do Bibliotech — Design

## Objetivo
Adicionar notificações internas persistentes, vinculadas aos usuários, exibidas por um sino no dashboard e geradas automaticamente pelos fluxos de empréstimos e reservas.

## Arquitetura
A entidade `Notificacao` ficará em `Entidades`, persistida por `NotificacaoRepository`. `NotificacaoService` centralizará criação, consulta e marcação de leitura, e `NotificacaoController` exporá endpoints REST sob `/api/notificacoes`. `EmprestimoService` e `ReservaService` dispararão eventos após operações bem-sucedidas. O dashboard usará o usuário do `localStorage` para consultar o feed e o contador.

## Dados
Cada notificação terá: id, usuário, título, mensagem, tipo, lida e data de criação. O backend retornará DTOs para evitar serialização da entidade `Usuario` e exposição de senha.

## Eventos
- Empréstimo criado: confirmação com livro e data prevista.
- Empréstimo devolvido: confirmação da devolução.
- Reserva criada: confirmação da reserva.
- Reserva cancelada: confirmação do cancelamento.

## API
- `GET /api/notificacoes/usuario/{usuarioId}`: lista em ordem decrescente.
- `GET /api/notificacoes/usuario/{usuarioId}/nao-lidas/count`: quantidade não lida.
- `PUT /api/notificacoes/{id}/lida?usuarioId={usuarioId}`: marca uma como lida, respeitando o dono.
- `PUT /api/notificacoes/usuario/{usuarioId}/lidas`: marca todas como lidas.

## Frontend
O cabeçalho receberá um botão com sino, badge para não lidas e dropdown com lista. Ao abrir, o usuário pode marcar uma notificação como lida; haverá ação “Marcar todas como lidas”. O feed será recarregado após empréstimos/devoluções e periodicamente enquanto o dashboard estiver aberto.

## Erros e segurança de dados
IDs inexistentes retornam 404 quando aplicável. A marcação individual exige o `usuarioId` para impedir que um usuário marque a notificação de outro. DTOs evitam enviar o objeto `Usuario` completo.

## Testes
Cobrir criação de notificação não lida, listagem por usuário, marcação individual pertencente ao usuário e marcação em massa. Ao final, executar `mvn test` e `mvn package`.
