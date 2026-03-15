# 🛒 Products API

API REST para gerenciamento de produtos, desenvolvida com Spring Boot.
Permite criar, listar, atualizar e remover produtos, com suporte a
paginação, validação de dados e tratamento global de exceções.

---

## 📌 Funcionalidades

- ✅ CRUD completo de produtos
- 📄 Listagem com paginação
- 🔎 Busca por ID
- ✏️ Atualização de dados
- ❌ Remoção de produtos
- ⚠️ Tratamento global de erros
- 🧾 Respostas padronizadas

---

## 🛠️ Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Jakarta Validation

---

## ▶️ Como executar o projeto

### 1. Clone o repositório

git clone https://github.com/seu-usuario/products-api.git

### 2. Acesse a pasta do projeto

cd products-api

### 3. Configure o banco de dados

Edite o arquivo `application.properties`:

spring.datasource.url=jdbc:postgresql://localhost:5432/products_db
spring.datasource.username=postgres
spring.datasource.password=123456

spring.jpa.hibernate.ddl-auto=update

### 4. Execute a aplicação

./mvnw spring-boot:run

A API estará disponível em:

http://localhost:8080

---

## 🌐 Endpoints da API

### 📌 Criar produto

POST /products

Body:

{
"name": "Notebook",
"price": 3500.00
}

---

### 📌 Listar produtos (com paginação)

GET /products?page=0&size=10

Resposta:

{
"content": [
{
"id": "uuid",
"name": "Notebook",
"price": 3500.00
}
],
"page": 0,
"size": 10,
"totalElements": 1,
"totalPages": 1
}

---

### 📌 Buscar produto por ID

GET /products/{id}

---

### 📌 Atualizar produto

PUT /products/{id}

Body:

{
"name": "Notebook Gamer",
"price": 4200.00
}

---

### 📌 Remover produto

DELETE /products/{id}

---

## ⚠️ Tratamento de erros

Exemplo de erro 404 — produto não encontrado:

{
"timestamp": "2026-03-10T14:30:00",
"status": 404,
"error": "Not Found",
"message": "Produto não encontrado",
"path": "/products/123"
}

---

## 🧠 Arquitetura

O projeto segue boas práticas de separação em camadas:

- Controller → recebe requisições HTTP
- Service → regras de negócio
- Repository → acesso ao banco
- DTOs → transferência de dados
- Exception Handler → tratamento global de erros

---

## 👤 Autor

**Simael Silva**  
Estudante de Sistemas de Informação  
Futuro Desenvolvedor Full Stack 🚀