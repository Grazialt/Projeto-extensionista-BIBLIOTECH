# Projeto-extensionista-BIBLIOTECH
O Bibliotech é um sistema desenvolvido com o objetivo de modernizar e facilitar a gestão de bibliotecas, oferecendo uma solução prática, intuitiva e eficiente para o controle de acervo, usuários e empréstimos. 


## Controle de livros lidos

A quantidade de livros lidos é calculada automaticamente pelo histórico de empréstimos com status `devolvido`. Cada devolução concluída contabiliza uma leitura para o aluno. O total aparece na lista de alunos, no dashboard e nos relatórios, sem necessidade de manter um contador manual no banco de dados.

## Notificações V3 - toast e som

- Novas notificações aparecem também como aviso flutuante no Dashboard.
- Um som curto é tocado uma vez quando chega um novo lote de notificações.
- O último ID visto é salvo por usuário para não repetir toast/som ao atualizar a página.
- O Dashboard verifica novas notificações a cada 10 segundos.
- Empréstimo e devolução forçam uma atualização imediata das notificações após sucesso.
- Navegadores exigem uma interação do usuário (clique/tecla) antes de permitir áudio; o Dashboard habilita o som na primeira interação.
