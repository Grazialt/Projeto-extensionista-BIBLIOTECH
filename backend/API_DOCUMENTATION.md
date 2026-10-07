# Documentação da API Backend

## Base URL

- `http://localhost:8080` (ou outra porta configurada em `application.properties`)

## Endpoints de Livros

### Listar livros
- `GET /api/livros`
- Query opcional: `?q=termo`

Resposta:
```json
[
  {
    "id": 1,
    "titulo": "Nome do Livro",
    "isbn": "9781234567890",
    "anoPublicacao": 2024,
    "quantidade": 5,
    "editora": "Editora XYZ",
    "edicao": "3ª",
    "paginas": 320,
    "autorId": 2,
    "autorNome": "Autor Exemplo",
    "categoriaId": 1,
    "categoriaNome": "Categoria Exemplo"
  }
]
```

### Obter livro por ID
- `GET /api/livros/{id}`

### Criar livro
- `POST /api/livros`
- Body JSON:
```json
{
  "titulo": "Livro Novo",
  "isbn": "9781234567890",
  "anoPublicacao": 2026,
  "quantidade": 10,
  "editora": "Editora ABC",
  "edicao": "1ª",
  "paginas": 250,
  "autorId": 1,
  "categoriaId": 1
}
```

### Atualizar livro
- `PUT /api/livros/{id}`
- Body JSON igual ao de criação

### Excluir livro
- `DELETE /api/livros/{id}`

## Endpoints de Usuários

### Listar usuários
- `GET /api/usuarios`
- Query opcional: `?q=termo`

Resposta:
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "matricula": "2024001",
    "endereco": "Rua A, 123",
    "email": "joao@example.com",
    "telefone": "(11) 99999-9999",
    "tipo": "usuario"
  }
]
```

### Criar usuário
- `POST /api/usuarios`
- Body JSON:
```json
{
  "nome": "Maria Souza",
  "matricula": "2024002",
  "endereco": "Av. B, 456",
  "email": "maria@example.com",
  "telefone": "(11) 98888-8888",
  "tipo": "usuario"
}
```

### Atualizar usuário
- `PUT /api/usuarios/{id}`
- Body JSON igual ao de criação

### Excluir usuário
- `DELETE /api/usuarios/{id}`

### Obter usuário por ID
- `GET /api/usuario/{id}`

### Obter usuário por email
- `GET /api/usuario/email/{email}`

### Login
- `POST /api/login`
- Body JSON:
```json
{
  "email": "usuario@example.com",
  "senha": "senha123"
}
```

### Registro
- `POST /api/register`
- Body JSON:
```json
{
  "nome": "Maria Souza",
  "matricula": "2024002",
  "endereco": "Av. B, 456",
  "email": "maria@example.com",
  "senha": "senha123",
  "telefone": "(11) 98888-8888"
}
```

## Endpoints de Empréstimos

### Listar empréstimos
- `GET /api/emprestimos`
- Query opcional: `?status=ativo|devolvido|atrasado`

### Criar empréstimo
- `POST /api/emprestimos`
- Body JSON:
```json
{
  "livroId": 1,
  "usuarioId": 1,
  "dataDevolucao": "2026-06-03"
}
```

### Obter empréstimo por ID
- `GET /api/emprestimos/{id}`

### Listar empréstimos de um usuário
- `GET /api/emprestimos/usuario/{usuarioId}`

### Devolver empréstimo
- `PUT /api/emprestimos/{id}/devolver`

## Endpoints de Dashboard / Relatórios

### Resumo do dashboard
- `GET /api/dashboard`

Resposta:
```json
{
  "totalLivros": 10,
  "usuariosCadastrados": 5,
  "emprestimosAtivos": 2,
  "emprestimosAtrasados": 1,
  "livrosDevolvidos": 8
}
```

## Observações

- O backend usa PostgreSQL conforme `application.properties`.
- Para rodar localmente, execute no diretório `backend`:
  - `mvn spring-boot:run`
- A API está preparada para suportar a interface das telas de livro, usuário, empréstimo e dashboard.
