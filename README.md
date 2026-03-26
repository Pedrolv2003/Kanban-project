# Mini Kanban

Uma aplicação completa de gerenciamento de tarefas utilizando o método Kanban, desenvolvida com **Vue.js** no frontend e **Spring Boot** no backend.

![Kanban Demo](https://img.shields.io/badge/Vue.js-3.x-4FC08D?logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-007396?logo=java)
![License](https://img.shields.io/badge/License-MIT-green)

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [Credenciais de Acesso](#credenciais-de-acesso)
- [Guia de Uso](#guia-de-uso)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Endpoints da API](#endpoints-da-api)
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## Sobre o Projeto

O **Mini Kanban** é uma ferramenta de gestão de tarefas visual que permite organizar, priorizar e acompanhar o progresso de atividades de forma intuitiva. Desenvolvido como trabalho acadêmico, o projeto implementa um quadro Kanban completo com suporte a colunas dinâmicas, arrastar e soltar, filtros avançados e um calendário integrado para visualização temporal das tarefas.

O sistema conta com autenticação JWT, garantindo segurança e isolamento entre usuários, além de um administrador padrão para demonstração.

---

## Funcionalidades

### Gerenciamento de Tarefas
- Criar, editar e eliminar tarefas
- Definir título, descrição, responsável e datas (início/fim)
- Arrastar e soltar tarefas entre colunas
- Seleção dupla: primeiro clique expande detalhes no card, segundo clique abre página completa
- Tarefas atrasadas são destacadas visualmente em vermelho
- Responsável pela última modificação é registrado automaticamente

### Colunas Dinâmicas
- Colunas padrão: **To Do**, **In Progress**, **Done**
- Criar novas colunas com título, cor e ícone personalizados
- Eliminar colunas (tarefas são movidas para "To Do")
- Barra de rolagem horizontal automática quando há muitas colunas
- Cores das colunas sincronizadas com o calendário

### Filtros Avançados
- Filtrar tarefas por **ID**, **título**, **responsável** e **data**
- Lista de responsáveis com busca integrada
- Filtros independentes por coluna
- Botão para limpar todos os filtros

### Calendário Integrado
- Página separada com visualização mensal
- Tarefas exibidas nos dias correspondentes ao período
- Cálculo dinâmico de espaço: mostra apenas tarefas que cabem no dia
- "+X" para indicar tarefas extras
- Clique em um dia para ver todas as tarefas do período
- Filtros integrados (ID, título, responsável)

### Autenticação e Usuários
- Login e registro de novos usuários
- Autenticação JWT (JSON Web Token)
- Administrador padrão criado automaticamente na primeira execução
- Menu de configurações com email do usuário, criar nova conta e sair
- Criador e último modificador registrados em cada tarefa

### Design e Experiência
- Interface moderna com gradientes e animações suaves
- Cores dinâmicas baseadas nas colunas
- Totalmente responsivo (desktop, tablet, mobile)
- Modal de confirmação para ações importantes (eliminar tarefa/coluna)
- Toast notifications para feedback de ações

---

## Tecnologias Utilizadas

### Backend
| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.x | Framework de desenvolvimento |
| Spring Security | 6.x | Autenticação e autorização |
| JWT | 0.11.5 | Tokens de autenticação |
| Spring Data JPA | - | Persistência de dados |
| H2 Database | - | Banco de dados em memória |
| Maven | - | Gerenciador de dependências |

### Frontend
| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Vue.js | 3.x | Framework progressivo |
| Vite | 5.x | Build tool e dev server |
| Font Awesome | 6.x | Ícones vetoriais |
| CSS3 | - | Estilização com Grid e Flexbox |

---

## Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- **Node.js** (versão 16 ou superior) - [Download](https://nodejs.org/)
- **Java JDK 17** - [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Maven** (opcional, o projeto inclui wrapper) - [Download](https://maven.apache.org/download.cgi)
- **Git** (para clonar o repositório) - [Download](https://git-scm.com/)

---

## Instalação e Execução

### 1. Clonar o Repositório
- git clone https://github.com/J-White17/kanban-project.git
- cd kanban-project

### 2. Backend (Spring Boot)
Navegar até a pasta do backend
- cd backend/api

Executar o Spring Boot (usando o wrapper do Maven)
- ./mvnw spring-boot:run   # Linux/Mac
- mvnw.cmd spring-boot:run # Windows

O backend será iniciado em: http://localhost:8080

### 3. Frontend (Vue.js)
Abrir um novo terminal, navegar até a pasta do frontend
- cd frontend

Instalar as dependências
- npm install

Executar o servidor de desenvolvimento
- npm run dev

O frontend será iniciado em: http://localhost:5173

### 4. Acessar a Aplicação
Abra o navegador e acesse: http://localhost:5173

## Credenciais de Acesso
O sistema já possui um usuário administrador criado automaticamente na primeira execução:

| Campo | Valor |
|-------|-------|
| **Email** | `admin@kanban.com` |
| **Senha** | `admin123` |

> 💡 **Dica:** Você pode criar novas contas através da opção **"Nova Conta"** no menu de configurações (ícone de engrenagem).

## Guia de Uso

### Página Principal (Kanban)

Criar Tarefa
- Clique em "+ Nova tarefa" em qualquer coluna
- Preencha título, descrição, responsável, data de início e data de fim
- Clique em "Criar Tarefa"

Mover Tarefa
- Arraste a tarefa com o mouse e solte na coluna desejada

Visualizar Detalhes
- Clique uma vez: a tarefa expande mostrando mais informações
- Clique novamente: abre página completa com todos os detalhes

Editar/Eliminar Tarefa
- Na página de detalhes, use os botões "Editar Tarefa" ou "Eliminar Tarefa"

Criar Nova Coluna

- Clique no botão "+ Nova Coluna" no cabeçalho
- Escolha título, cor e ícone
- A nova coluna será adicionada ao final

Filtrar Coluna

- Clique no ícone de funil (🔍) no cabeçalho da coluna
- Preencha os campos desejados (ID, título, responsável, data)
- Campos em branco são ignorados automaticamente
- Clique em "Aplicar Filtro"

### Calendário
Acessar Calendário
- Clique no botão "Calendário" no cabeçalho
- Navegar entre Meses
- Use os botões "<" e ">" para mudar de mês

Visualizar Tarefas
- Clique em qualquer dia para ver todas as tarefas daquele período
- As tarefas aparecem na coluna direita

Filtrar no Calendário
- Use os filtros (ID, título, responsável) para refinar a busca
- Os filtros afetam a exibição das tarefas no calendário

### Configurações
- Clique no ícone de engrenagem (⚙️) no cabeçalho
- Visualize seu email
- Crie uma nova conta
- Faça logout

## 📁 Estrutura do Projeto

```
kanban-project/
├── backend/
│   └── api/
│       ├── src/main/java/com/kanban/
│       │   ├── controller/          # Endpoints REST
│       │   ├── model/               # Entidades JPA
│       │   ├── repository/          # Interfaces de acesso a dados
│       │   ├── service/             # Lógica de negócio
│       │   ├── security/            # Configurações de segurança
│       │   ├── config/              # Configurações
│       │   └── dto/                 # Objetos de transferência
│       └── pom.xml                  # Dependências Maven
│
└── frontend/
    └── src/
        ├── components/              # Componentes Vue
        │   ├── CalendarPage.vue
        │   ├── KanbanColumn.vue
        │   ├── TaskCard.vue
        │   ├── TaskDetailModal.vue
        │   ├── TaskFormModal.vue
        │   ├── FilterModal.vue
        │   ├── NewColumnModal.vue
        │   ├── LoginForm.vue
        │   ├── RegisterForm.vue
        │   ├── SettingsMenu.vue
        │   └── Modal.vue
        ├── App.vue                  # Componente principal
        └── main.js                  # Ponto de entrada
```

### Endpoints da API


Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Realiza login e retorna token JWT |
| POST | `/api/auth/register` | Registra novo usuário |

Tarefas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/tasks` | Lista todas as tarefas |
| GET | `/api/tasks/{id}` | Busca tarefa por ID |
| POST | `/api/tasks` | Cria nova tarefa |
| PUT | `/api/tasks/{id}` | Atualiza tarefa |
| DELETE | `/api/tasks/{id}` | Remove tarefa |

Colunas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/columns` | Lista todas as colunas |
| POST | `/api/columns` | Cria nova coluna |
| DELETE | `/api/columns/{status}` | Remove coluna |
