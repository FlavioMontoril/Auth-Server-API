# Auth Server API

Uma API de gerenciamento de usuários e permissões (roles) desenvolvida com Spring Boot 3.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL** (Banco de dados)
- **Flyway** (Migração de banco de dados)
- **Lombok** (Produtividade)
- **Validation** (Validação de dados)
- **Auth0 Java JWT** (Suporte a tokens JWT)

## 🐳 Containerização

A aplicação é totalmente containerizada, utilizando as melhores práticas para isolamento e eficiência:

### Dockerfile Multistage
O projeto utiliza um **Dockerfile Multistage** para otimizar o tamanho da imagem final e aumentar a segurança:
- **Estágio de Build**: Utiliza uma imagem com Maven e JDK 21 para compilar o projeto e gerar o artefato (`.jar`).
- **Estágio Final**: Utiliza uma imagem leve (JRE Alpine), copia apenas o arquivo executável e configura um usuário não-root (`spring`) para execução segura do container.

### Docker Compose
O **Docker Compose** é utilizado para orquestrar os múltiplos serviços que compõem a aplicação:
- **API**: A aplicação Spring Boot, configurada para aguardar a saúde do banco de dados antes de iniciar.
- **Banco de Dados**: Um container PostgreSQL 16 com volume persistente para garantir que os dados não sejam perdidos ao reiniciar os serviços.

## ☁️ Cloud & Deployment (Azure)

A aplicação está hospedada na **Microsoft Azure**, utilizando o App Service e o Azure Database for PostgreSQL.

- **Perfil Ativo**: Na Azure, deve-se configurar a variável de ambiente `SPRING_PROFILES_ACTIVE=azure`.
- **Configurações**: As credenciais de produção devem ser cadastradas no portal da Azure (Application Settings) utilizando os nomes:
  - `DB_URL_AZURE`
  - `DB_USERNAME_AZURE`
  - `DB_PASSWORD_AZURE`
  - `JWT_SECRET`
  - `API_SECURITY_TOKEN_ISSUER`

## 📋 Pré-requisitos

- JDK 21 ou superior
- Docker e Docker Compose
- Maven 3.x (opcional se usar Docker)

## 🔧 Configuração

### Desenvolvimento Local
A aplicação está configurada para carregar automaticamente as variáveis de ambiente a partir de um arquivo `.env` localizado na raiz do projeto (via `spring.config.import`).

1. Certifique-se de ter um banco PostgreSQL rodando localmente (ou use o banco do Docker Compose).
2. Configure seu arquivo `.env` com as credenciais locais (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `API_SECURITY_TOKEN_ISSUER`).
3. Execute a aplicação normalmente (o Spring usará o perfil padrão):
   ```bash
   mvn spring-boot:run
   ```

### Docker Compose (Recomendado)
1. Clone o repositório.
2. Configure as variáveis de ambiente no arquivo `.env` (baseado no `.env.example`).
3. Execute a aplicação via Docker Compose:
   ```bash
   docker-compose up --build
   ```

## 🛡️ Segurança

A aplicação utiliza **Spring Security**. Atualmente, a configuração em `SecurityConfig.java` está definida como `.permitAll()` para todas as rotas, facilitando o desenvolvimento inicial. O sistema está preparado para implementar autenticação via **JWT**.
