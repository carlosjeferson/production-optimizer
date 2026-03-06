# 🏭 Gerenciamento e Otimização de Produção Industrial

Este projeto é uma solução Full-Stack desenvolvida para otimizar a linha de produção de uma fábrica. O sistema gerencia o estoque de matérias-primas e a composição de produtos, possuindo um motor de otimização que analisa o inventário atual e sugere o plano de produção ideal para obter o **maior lucro possível**, resolvendo conflitos de insumos de forma inteligente.

## 🚀 Tecnologias Utilizadas

O projeto foi construído seguindo boas práticas de Clean Code, com separação clara de responsabilidades e alta cobertura de testes.

### 🔙 Back-end
* **Linguagem:** Java 21
* **Framework:** Quarkus (com RESTEasy Jackson e Arc)
* **Banco de Dados:** PostgreSQL (com Quarkus Hibernate ORM Panache)
* **Otimização Matemática:** Google OR-Tools (Programação Linear)
* **Documentação da API:** OpenAPI / Swagger
* **Testes:** JUnit 5 e REST Assured
* **Arquitetura:** Padrões DTO, Model, Exception Handler Global, Resource (Controllers) e Service.

### 💻 Front-end
* **Framework:** Vue.js 3
* **Build Tool:** Vite
* **Requisições HTTP:** Axios
* **Estilização:** CSS Puro (sem frameworks externos para maior controle)
* **Testes E2E:** Cypress (com mockagem de dados)

---

## ✨ Funcionalidades

* **Gestão de Matérias-Primas:** CRUD completo (Cadastro, Edição, Listagem e Remoção) de insumos e suas quantidades em estoque.
* **Gestão de Produtos:** CRUD de produtos, definindo valor de venda e a "receita" (quais matérias-primas e quantidades são necessárias para fabricar 1 unidade).
* **Cálculo de Produção (Motor de Otimização):** O coração da aplicação. Uma funcionalidade que processa o estoque atual e a composição dos produtos utilizando o **Google OR-Tools** para calcular a combinação exata de produtos a serem fabricados, garantindo o maior retorno financeiro possível sem ultrapassar o estoque disponível.
* **Tratamento de Erros:** Exceções globais tratadas no back-end, retornando mensagens claras para a interface.

---

## 🛠️ Como executar o projeto localmente

### Pré-requisitos
Certifique-se de ter instalado em sua máquina:
* **Java 21+** e **Maven**
* **Node.js** (versão 22+ recomendada) e **NPM** 10+
* **PostgreSQL** rodando localmente (ou via Docker)

### 1. Rodando o Back-end (Quarkus)

1. Clone o repositório:

```bash
git clone [https://github.com/carlosjeferson/production-optimizer.git](https://github.com/carlosjeferson/production-optimizer)
```

2. Acesse a pasta do back-end (ajuste o nome da pasta conforme seu projeto):
```bash
cd backend
```


3. Configure as credenciais do banco de dados no arquivo `src/main/resources/application.properties`:
```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/nome_do_banco
quarkus.datasource.username=seu_usuario
quarkus.datasource.password=sua_senha
quarkus.hibernate-orm.database.generation=update
```


4. Inicie o servidor em modo de desenvolvimento:
```bash
mvn compile quarkus:dev
```


*A API estará rodando em `http://localhost:8080` (A documentação Swagger geralmente fica em `http://localhost:8080/q/swagger-ui`).*

### 2. Rodando o Front-end (Vue.js)

1. Abra um novo terminal e acesse a pasta do front-end:
```bash
cd frontend
```


2. Instale as dependências:
```bash
npm install
```


3. Inicie o servidor de desenvolvimento:
```bash
npm run dev
```


*A aplicação estará disponível em `http://localhost:5173`.*

---

## 🧪 Como rodar os Testes

A qualidade do software foi garantida através de testes em ambas as camadas.

* **Back-end (Testes Unitários e de Integração):**
Na pasta do back-end, execute:
```bash
mvn test
```


*Isto executará os testes de CRUD, validações e a lógica matemática do Cálculo de Produção usando JUnit 5 e REST Assured.*
* **Front-end (Testes E2E com Cypress):**
Na pasta do front-end, execute:
```bash
npx cypress open
```


*Isto abrirá a interface do Cypress. Selecione "E2E Testing" para rodar os cenários de `engenharia_produtos.cy.js` e `insumos.cy.js`. Os testes utilizam mockagem de dados para garantir isolamento e rapidez.*

---

Desenvolvido com dedicação para o desafio técnico de P&D.
