# 🏭 Production Optimizer: Inteligência Industrial & Otimização de Lucro

Este projeto é uma solução Full-Stack de alto nível desenvolvida para resolver um dos maiores desafios da gestão industrial: a **alocação otimizada de recursos**. O sistema não apenas gerencia o estoque, mas utiliza um motor de **Programação Linear** para decidir matematicamente o que produzir para maximizar o retorno financeiro.

<img width="1902" height="908" alt="image" src="https://github.com/user-attachments/assets/73ee023f-3691-4290-a01c-2f53bbd77e1b" />
<img width="1900" height="904" alt="image" src="https://github.com/user-attachments/assets/27fb7c85-ab82-44fa-b16b-6a24260546d8" />
<img width="1889" height="895" alt="image" src="https://github.com/user-attachments/assets/3b281184-8ab1-429b-8b96-1c0734583a1e" />

## 🧠 O Diferencial de P&D: Motor de Otimização

Diferente de abordagens baseadas em regras simples (como ordenar por preço), este projeto utiliza o **Google OR-Tools** com o solver **SCIP**.

* **Problema Resolvido:** Otimização Combinatória (Variação do Problema da Mochila Multidimensional).
* **Inteligência:** O algoritmo analisa simultaneamente múltiplos produtos que competem pelos mesmos insumos limitados, resolvendo conflitos para encontrar o cenário de **lucro global máximo**, e não apenas local.

---

## 🚀 Tecnologias e Arquitetura

O projeto foi construído focando em **escalabilidade** e **integridade de domínio**.

### 🔙 Back-end (Robustez e Segurança)

* **Java 21 & Quarkus:** Utilização de recursos modernos da linguagem.
* **Encapsulamento e OO:** Domínio refatorado para garantir a integridade dos dados, utilizando campos privados e métodos de acesso controlados.
* **Google OR-Tools:** Implementação de pesquisa operacional para tomada de decisão.
* **Persistência:** Hibernate com Panache e PostgreSQL para gestão relacional.

### 💻 Front-end (Experiência e Agilidade)

* **Vue.js 3 & Vite:** Interface reativa e performática.
* **CSS Puro:** Design customizado sem dependências pesadas, focado em clareza de dados industriais.

---

## 🧪 Estratégia de Qualidade (Testes)

A cobertura de testes foi desenhada para validar desde o contrato da API até a precisão matemática do algoritmo.

* **Testes de Lógica (JUnit 5):** Validam cenários de conflito de insumos, garantindo que o solver escolha sempre a opção de maior valor total (ROI).
* **Testes de Integração (REST Assured):** Validam o ciclo completo de vida das entidades (CRUD) e a comunicação HTTP.
* **Testes E2E (Cypress):** Simulam a jornada do usuário final na interface, garantindo que o fluxo de otimização seja intuitivo e funcional.

---

## 🛠️ Execução Local

### 1. Back-end

```bash
# Clone o repositório
git clone https://github.com/carlosjeferson/production-optimizer.git
cd production-optimizer/backend

# Configure o application.properties com seu banco PostgreSQL local
mvn compile quarkus:dev

```

### 2. Front-end

```bash
cd ../frontend
npm install
npm run dev

```

---

## 📊 Exemplos de Otimização

| Cenário | Estoque | Produto A (R$ 100) | Produto B (R$ 60) | Resultado do Solver |
| --- | --- | --- | --- | --- |
| **Simples** | 10 Madeiras | Gasta 10 | Gasta 5 | **2x Produto B (Total R$ 120)** |

*O sistema percebe que fabricar duas unidades de menor valor unitário gera um lucro total maior do que uma unidade de valor alto, otimizando o uso da matéria-prima.*