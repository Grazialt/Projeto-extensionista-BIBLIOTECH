# Notificações do Bibliotech Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar notificações internas persistentes para empréstimos e reservas com sino no dashboard.

**Architecture:** Entidade + repository + service + controller REST no backend; serviços de domínio disparam notificações somente após operações bem-sucedidas. O dashboard consome a API com o ID do usuário autenticado já salvo no localStorage.

**Tech Stack:** Java 17, Spring Boot 3.2.5, Spring Data JPA, PostgreSQL, HTML/CSS/JavaScript.

**Spec:** `docs/superpowers/specs/2026-08-26-notificacoes-design.md`

## Global Constraints
- Manter a organização atual `com.bibliotech.{Entidades,Repositories,Services,Controllers,DTOs}`.
- Não adicionar autenticação nova nem dependências de runtime desnecessárias.
- Não serializar a entidade `Usuario` nas respostas de notificação.

---

### Task 1: Modelo e serviço de notificações
**Files:** criar `Notificacao.java`, `NotificacaoDTO.java`, `NotificacaoRepository.java`, `NotificacaoService.java`; criar teste `NotificacaoServiceTest.java`.
**Interfaces:** produz `criar`, `listarPorUsuario`, `contarNaoLidas`, `marcarComoLida`, `marcarTodasComoLidas`.
- [ ] Escrever testes falhando para criação, listagem e leitura.
- [ ] Executar os testes e confirmar falha pela ausência da funcionalidade.
- [ ] Implementar o mínimo necessário.
- [ ] Executar os testes e confirmar sucesso.

### Task 2: API REST
**Files:** criar `NotificacaoController.java`; criar `NotificacaoControllerTest.java` se o contexto permitir.
**Interfaces:** expõe os quatro endpoints definidos no spec.
- [ ] Escrever teste de contrato para os endpoints principais.
- [ ] Confirmar falha.
- [ ] Implementar controller.
- [ ] Confirmar sucesso.

### Task 3: Integração de empréstimos e reservas
**Files:** modificar `EmprestimoService.java` e `ReservaService.java`; testes correspondentes.
**Interfaces:** consome `NotificacaoService.criar(Usuario,String,String,String)`.
- [ ] Escrever testes que exijam notificação após operação bem-sucedida.
- [ ] Confirmar falha.
- [ ] Injetar serviço e criar eventos após persistência.
- [ ] Confirmar sucesso.

### Task 4: Sino no dashboard
**Files:** modificar `dashboard.html`.
**Interfaces:** consome `/api/notificacoes/...`.
- [ ] Adicionar markup/CSS do sino, badge e dropdown.
- [ ] Adicionar funções JS de carregar, renderizar e marcar leitura.
- [ ] Atualizar notificações após operações e em intervalo moderado.
- [ ] Verificar referências DOM/JS e comportamento sem notificações.

### Task 5: Verificação final e empacotamento
**Files:** projeto completo.
- [ ] Executar `mvn test`.
- [ ] Executar `mvn package`.
- [ ] Revisar `git diff` para escopo e credenciais não adicionadas.
- [ ] Gerar ZIP completo atualizado para entrega.
