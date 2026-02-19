# ecossistema-wn-backend

**WN -- Welwitschia Noticias (Backend)**

API backend do portal de noticias Welwitschia Noticias da Embaixada da Republica de Angola na Alemanha. Sistema completo para gestao de artigos, categorias, tags, autores, media, fluxo editorial com comentarios de revisao, newsletter com subscricoes, e feeds RSS/Atom.

Parte do [Ecossistema Digital -- Embaixada de Angola na Alemanha](https://github.com/embaixada-angola-alemanha/ecossistema-project).

---

## Stack Tecnologica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.3 |
| Base de Dados | PostgreSQL |
| Migracoes | Flyway |
| Autenticacao | Keycloak (OAuth2 Resource Server / JWT) |
| Armazenamento | MinIO (via commons-storage) |
| Mensageria | RabbitMQ (AMQP) |
| Mapeamento | MapStruct 1.5.5 |
| Feeds RSS/Atom | Rome 2.1.0 |
| Documentacao API | SpringDoc OpenAPI 2.8.6 (Swagger UI) |
| Rate Limiting | Bucket4j |
| Testes | JUnit 5, Testcontainers, H2 |
| Cobertura | JaCoCo |
| CI/CD | GitHub Actions |
| Containerizacao | Docker (Eclipse Temurin 21 JRE Alpine) |

---

## Estrutura do Projecto

```
src/main/java/ao/gov/embaixada/wn/
  config/
    OpenApiConfig.java              # Configuracao Swagger/OpenAPI
  controller/
    ArticleController.java          # CRUD de artigos (admin, autenticado)
    PublicArticleController.java    # Artigos publicos (sem autenticacao)
    EditorialController.java        # Workflow editorial, versoes, comentarios
    CategoryController.java         # CRUD de categorias
    TagController.java              # CRUD de tags
    AuthorController.java           # CRUD de autores
    MediaController.java            # Upload/download de media (MinIO)
    NewsletterController.java       # Newsletter: subscricao publica + admin
    FeedController.java             # Feeds RSS e Atom
    ShareController.java            # Links de partilha social
  dto/
    ArticleCreateRequest.java, ArticleResponse.java, ArticleVersionResponse.java
    CategoryCreateRequest.java, CategoryResponse.java
    TagCreateRequest.java, TagResponse.java
    AuthorCreateRequest.java, AuthorResponse.java
    CommentCreateRequest.java, CommentResponse.java
    MediaFileResponse.java
    NewsletterSubscribeRequest.java, NewsletterSubscriberResponse.java
    ScheduleRequest.java, ShareResponse.java
  entity/
    Article.java, ArticleVersion.java
    Category.java, Tag.java, Author.java
    EditorialComment.java, MediaFile.java
    NewsletterSubscriber.java
  enums/
    EstadoArtigo.java               # DRAFT, SUBMITTED, IN_REVIEW, PUBLISHED, ARCHIVED
    Idioma.java                     # PT, EN, DE
  exception/
    GlobalExceptionHandler.java
    ResourceNotFoundException.java
    DuplicateResourceException.java
    InvalidStateTransitionException.java
  integration/
    WnEventPublisher.java           # Publicacao de eventos via RabbitMQ
    WnSgcConsumer.java              # Consumo de mensagens do SGC
    WnSiConsumer.java               # Consumo de mensagens do SI
  repository/
    ArticleRepository.java, ArticleVersionRepository.java
    CategoryRepository.java, TagRepository.java, AuthorRepository.java
    EditorialCommentRepository.java, MediaFileRepository.java
    NewsletterSubscriberRepository.java
  service/
    ArticleService.java, ArticleVersionService.java
    CategoryService.java, TagService.java, AuthorService.java
    EditorialCommentService.java, MediaService.java
    NewsletterService.java, RssFeedService.java
    SchedulerService.java, ShareService.java

src/main/resources/
  application.yml                   # Configuracao principal (porta 8083)
  application-staging.yml           # Configuracao staging
  application-production.yml        # Configuracao producao
  db/migration/
    V1__create_wn_tables.sql
    V2__editorial_workflow.sql
    V3__newsletter_subscribers.sql
    V4__fix_v2_base_entity_columns.sql
    V5__fix_newsletter_base_entity_columns.sql
```

---

## Endpoints da API

Base URL: `http://localhost:8083`

### Artigos (Admin) -- `/api/v1/articles`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/articles` | Criar artigo | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| GET | `/api/v1/articles` | Listar artigos (filtro por estado) | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| GET | `/api/v1/articles/{id}` | Obter artigo por ID | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| GET | `/api/v1/articles/editorial` | Artigos em fluxo editorial | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| PUT | `/api/v1/articles/{id}` | Actualizar artigo | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| PATCH | `/api/v1/articles/{id}/estado` | Alterar estado | WN-EDITOR, WN-ADMIN |
| DELETE | `/api/v1/articles/{id}` | Eliminar artigo | WN-EDITOR, WN-ADMIN |

### Editorial -- `/api/v1/editorial`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| PATCH | `/api/v1/editorial/articles/{id}/submit` | Submeter para revisao | WN-JOURNALIST, WN-EDITOR, WN-ADMIN |
| PATCH | `/api/v1/editorial/articles/{id}/review` | Iniciar revisao | WN-EDITOR, WN-REVIEWER, WN-ADMIN |
| PATCH | `/api/v1/editorial/articles/{id}/publish` | Publicar artigo | WN-EDITOR, WN-ADMIN |
| PATCH | `/api/v1/editorial/articles/{id}/reject` | Rejeitar (volta a DRAFT) | WN-EDITOR, WN-REVIEWER, WN-ADMIN |
| PATCH | `/api/v1/editorial/articles/{id}/archive` | Arquivar artigo | WN-EDITOR, WN-ADMIN |
| PATCH | `/api/v1/editorial/articles/{id}/schedule` | Agendar publicacao | WN-EDITOR, WN-ADMIN |
| DELETE | `/api/v1/editorial/articles/{id}/schedule` | Cancelar agendamento | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/editorial/articles/{id}/versions` | Listar versoes | Todos os roles |
| GET | `/api/v1/editorial/articles/{id}/versions/{n}` | Obter versao especifica | Todos os roles |
| POST | `/api/v1/editorial/articles/{id}/versions` | Criar versao manual | Todos os roles |
| POST | `/api/v1/editorial/articles/{id}/versions/{n}/restore` | Restaurar versao | Todos os roles |
| GET | `/api/v1/editorial/articles/{id}/comments` | Listar comentarios | Todos os roles |
| GET | `/api/v1/editorial/articles/{id}/comments/unresolved` | Comentarios nao resolvidos | Todos os roles |
| POST | `/api/v1/editorial/articles/{id}/comments` | Adicionar comentario | Todos os roles |
| PATCH | `/api/v1/editorial/comments/{id}/resolve` | Resolver comentario | Todos os roles |
| DELETE | `/api/v1/editorial/comments/{id}` | Eliminar comentario | Todos os roles |

### Categorias -- `/api/v1/categories`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/categories` | Criar categoria | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/categories` | Listar categorias | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/categories/{id}` | Obter categoria | WN-EDITOR, WN-ADMIN |
| PUT | `/api/v1/categories/{id}` | Actualizar categoria | WN-EDITOR, WN-ADMIN |
| PATCH | `/api/v1/categories/{id}/toggle-active` | Activar/desactivar | WN-EDITOR, WN-ADMIN |
| DELETE | `/api/v1/categories/{id}` | Eliminar categoria | WN-EDITOR, WN-ADMIN |

### Tags -- `/api/v1/tags`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/tags` | Criar tag | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/tags` | Listar tags | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/tags/{id}` | Obter tag | WN-EDITOR, WN-ADMIN |
| PUT | `/api/v1/tags/{id}` | Actualizar tag | WN-EDITOR, WN-ADMIN |
| DELETE | `/api/v1/tags/{id}` | Eliminar tag | WN-EDITOR, WN-ADMIN |

### Autores -- `/api/v1/authors`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/authors` | Criar autor | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/authors` | Listar autores | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/authors/{id}` | Obter autor | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/authors/active` | Autores activos | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/authors/keycloak/{keycloakId}` | Obter por Keycloak ID | WN-EDITOR, WN-ADMIN |
| PUT | `/api/v1/authors/{id}` | Actualizar autor | WN-EDITOR, WN-ADMIN |
| PATCH | `/api/v1/authors/{id}/toggle-active` | Activar/desactivar | WN-EDITOR, WN-ADMIN |
| DELETE | `/api/v1/authors/{id}` | Eliminar autor | WN-EDITOR, WN-ADMIN |

### Media -- `/api/v1/media`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/media` | Upload de ficheiro (multipart) | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| POST | `/api/v1/media/resize` | Upload com redimensionamento | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| GET | `/api/v1/media` | Listar ficheiros media | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| GET | `/api/v1/media/{id}` | Metadados do ficheiro | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| GET | `/api/v1/media/{id}/download` | Download do ficheiro | WN-EDITOR, WN-JOURNALIST, WN-ADMIN |
| DELETE | `/api/v1/media/{id}` | Eliminar ficheiro | WN-EDITOR, WN-ADMIN |

### Newsletter

| Metodo | Endpoint | Descricao | Autenticacao |
|---|---|---|---|
| POST | `/api/v1/public/newsletter/subscribe` | Subscrever newsletter | Publica |
| GET | `/api/v1/public/newsletter/confirm?token=` | Confirmar subscricao | Publica |
| POST | `/api/v1/public/newsletter/unsubscribe?email=` | Cancelar subscricao | Publica |
| GET | `/api/v1/newsletter/subscribers` | Listar subscritores | WN-EDITOR, WN-ADMIN |
| GET | `/api/v1/newsletter/subscribers/count` | Contar subscritores | WN-EDITOR, WN-ADMIN |
| DELETE | `/api/v1/newsletter/subscribers/{id}` | Eliminar subscritor | WN-ADMIN |

### API Publica (sem autenticacao) -- `/api/v1/public/articles`

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | `/api/v1/public/articles` | Listar artigos publicados (paginado) |
| GET | `/api/v1/public/articles/{slug}` | Obter artigo por slug (incrementa visualizacoes) |
| GET | `/api/v1/public/articles/featured` | Artigos em destaque |
| GET | `/api/v1/public/articles/category/{categoryId}` | Artigos por categoria |
| GET | `/api/v1/public/articles/tag/{tagId}` | Artigos por tag |
| GET | `/api/v1/public/articles/author/{authorId}` | Artigos por autor |
| GET | `/api/v1/public/articles/search?q=` | Pesquisa full-text |
| GET | `/api/v1/public/articles/categories` | Listar categorias activas |
| GET | `/api/v1/public/articles/tags` | Listar todas as tags |
| GET | `/api/v1/public/articles/{slug}/share` | Links de partilha social |

### Feeds RSS/Atom -- `/api/v1/feed`

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | `/api/v1/feed/rss` | Feed RSS 2.0 |
| GET | `/api/v1/feed/atom` | Feed Atom |

### Outros

| Endpoint | Descricao |
|---|---|
| GET `/actuator/health` | Health check |
| GET `/v3/api-docs` | Especificacao OpenAPI (JSON) |
| GET `/swagger-ui.html` | Swagger UI interactivo |

---

## Fluxo Editorial

```
DRAFT --> SUBMITTED --> IN_REVIEW --> PUBLISHED --> ARCHIVED
  ^                        |
  |                        v
  +------ DRAFT (reject) --+
```

O fluxo editorial inclui:
- **Versionamento automatico**: snapshot criado ao submeter e publicar
- **Comentarios de revisao**: revisores podem adicionar comentarios nos artigos
- **Agendamento de publicacao**: publicacao automatica em data/hora futura
- **Contagem de visualizacoes**: incrementada automaticamente ao ler artigo publico

---

## Roles Keycloak

| Role | Permissoes |
|---|---|
| `WN-ADMIN` | Acesso total: CRUD, workflow, newsletter, eliminacao |
| `WN-EDITOR` | Editar/publicar artigos, gerir categorias/tags/autores |
| `WN-JOURNALIST` | Criar/editar artigos, submeter para revisao, upload media |
| `WN-REVIEWER` | Revisar artigos, adicionar comentarios |

**Nota**: Os roles utilizam letras maiusculas com hifen (e.g., `WN-ADMIN`, `WN-EDITOR`).

---

## Pre-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+
- Keycloak (realm `ecossistema`)
- MinIO (armazenamento de media)
- RabbitMQ (mensageria)

---

## Como Executar

### Desenvolvimento Local

```bash
# Criar base de dados
createdb wn_db

# Executar aplicacao (porta 8083)
mvn spring-boot:run

# Executar testes
mvn clean verify
```

### Build

```bash
# Compilar e gerar JAR
mvn clean package -DskipTests

# Build Docker
docker build -t ecossistema-wn-backend .

# Executar container
docker run -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/wn_db \
  -e SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://keycloak:8080/realms/ecossistema \
  ecossistema-wn-backend
```

---

## Configuracao de Ambiente

| Variavel | Descricao | Valor Default |
|---|---|---|
| `SERVER_PORT` | Porta do servidor | `8083` |
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5432/wn_db` |
| `SPRING_DATASOURCE_USERNAME` | Utilizador BD | `ecossistema` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | (dev only) |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` | Keycloak issuer | `http://localhost:8080/realms/ecossistema` |
| `SPRING_RABBITMQ_HOST` | Host RabbitMQ | `localhost` |
| `ECOSSISTEMA_STORAGE_ENDPOINT` | Endpoint MinIO | `http://localhost:9000` |
| `ECOSSISTEMA_STORAGE_DEFAULT_BUCKET` | Bucket MinIO | `wn-media` |
| `WN_SITE_URL` | URL do site de noticias | `https://noticias.angola-botschaft.de` |
| `WN_SITE_NAME` | Nome do site | `Welwitschia Noticias` |
| `WN_FEED_TITLE` | Titulo do feed RSS/Atom | `Welwitschia Noticias -- Embaixada de Angola` |
| `WN_FEED_MAX_ITEMS` | Maximo de itens no feed | `50` |

Perfis disponiveis: `default`, `staging`, `production`

---

## CI/CD

Pipeline GitHub Actions (`.github/workflows/ci.yml`):
1. **Build & Test** -- `mvn clean verify` com JDK 21
2. **Build Docker Image** -- Build da imagem Docker nos branches `main`/`develop`
3. **Conventional Commits** -- Validacao de mensagens de commit em PRs

---

## Projecto Principal

Este repositorio faz parte do **Ecossistema Digital -- Embaixada de Angola na Alemanha**.

Repositorio principal: [ecossistema-project](https://github.com/embaixada-angola-alemanha/ecossistema-project)

Dominio: `embaixada-angola.site`
