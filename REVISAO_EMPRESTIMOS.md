# Revisão de empréstimos — BiblioTech

Esta cópia foi criada a partir de `BiblioTech_atualizado.zip`. O original não foi modificado.

## Mudanças
- `EmprestimoService`: `@Transactional` na criação e na devolução; operações de estoque passam a ocorrer na mesma transação do registro e das notificações.
- `LivroRepository`: consulta com bloqueio pessimista (`PESSIMISTIC_WRITE`) para serializar atualizações do estoque.
- `EmprestimoRepository`: consulta com bloqueio pessimista na devolução, evitando que duas requisições devolvam o mesmo empréstimo ao mesmo tempo.
- A criação trata também quantidade nula como indisponibilidade.

## Antes de usar
1. Faça backup do banco e mantenha o projeto original intacto.
2. Configure `DB_PASSWORD` e `JWT_SECRET` no terminal, sem publicar os valores.
3. Execute `mvn clean test` e `mvn spring-boot:run` na pasta `backend`.
4. Teste cadastro de empréstimo, devolução, tentativas duplicadas e estoque com apenas uma unidade.
5. Confirme que as notificações continuam funcionando.

## Limites
- Não foi possível executar Maven nesta sessão: o comando `mvn` não está instalado neste ambiente.
- Ainda é necessário revisar autorização de rotas e outras operações que alterem a quantidade de livros.
- O ZIP de entrega exclui `target`, backup do banco e logs locais; guarde o backup separadamente.
