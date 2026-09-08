# GhumDoZa — Ticketing System

Welcome. This repo is the **backend** for GhumDoZa, a project ticketing system (think lightweight Jira): users, projects, tickets, comments, and optional OpenAI helpers for grammar, task wording, and search.

The UI is a separate frontend that talks to this API. CORS is set up for `http://localhost:3000` for now

---
## Stack

- Java 25
- Spring Boot 4.1.1
- Spring Web + Spring Data JPA
- PostgreSQL
- Lombok
- Maven (`pom.xml`; wrappers `mvnw` / `mvnw.cmd` are in the repo)

There is no Spring Security filter chain. Login/register are application endpoints that hash passwords

---

## Prerequisites for local run

1. **JDK 25**
2. **Maven** (or use `./mvnw` / `mvnw.cmd`)
3. **PostgreSQL** running locally
4. An IDE (IntelliJ or Cursor/VS Code with Java support) with a **Lombok** plugin enabled

---

## Local database setup

1. Create a database named `ghumdoza`.
2. Match credentials in `src/main/resources/application.properties`:


Do not commit real secrets. 

3. Run schema SQL in a Postgres client (psql, pgAdmin, DBeaver):

   - `src/main/resources/initialize.sql` — **drops and recreates** tables. Destructive. Use on a fresh local DB.
   - `src/main/resources/data.sql` — sample users, teams, projects.
   - `src/main/resources/demo-data.sql` — extra demo data if you need it.

Hibernate is **not** configured to auto-create tables (`ddl-auto` is unset). If tables are missing, the app will fail at runtime when it hits the DB.

---

## Run the API

From the repo root:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Or run `GhumDoZaApplication` from the IDE.

Default URL: **http://localhost:8080**


## OpenAI (optional) - turned off for now

`OpenAIController` and `OpenAIConfig` read:

- `openai.model`
- `openai.api.key`
- `openai.api.url`

Those properties are **commented out** in `application.properties`. 

Endpoints (query param `text`):

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/openai/grammar` | Grammar correction |
| GET | `/openai/task` | Rewrite a task description |
| GET | `/openai/search` | Keyword extract, then ticket search |

---

## API map

Base path is the host only (no `/api` prefix).

### User — `/user`

| Method | Path | Notes |
|--------|------|--------|
| GET | `/user/{id}` | Profile by UUID |
| GET | `/user/username/{username}` | Profile by username |
| PUT | `/user/login` | Body: `UserLoginInfoDto` |
| PUT | `/user/register` | Same DTO; also sends `firstName` / `lastName` |

### Project — `/project`

| Method | Path |
|--------|------|
| GET | `/project/list/{userId}` |
| PUT | `/project/create` |
| GET | `/project/participants/{projectId}` |
| POST | `/project/participants/add` |
| DELETE | `/project/participants/remove` |

Project codes must be unique (`DuplicateProjectCodeException` → 400).

### Ticket — `/ticket`

| Method | Path |
|--------|------|
| GET | `/ticket/{ticketId}` |
| POST | `/ticket/create` | Body uses **project code** (`projectCode`), not project UUID |
| GET | `/ticket/list/{userId}` |
| GET | `/ticket/list/project/{projectId}` |
| POST | `/ticket/update` |
| DELETE | `/ticket/delete/{ticketId}` |

Statuses: `TO_DO`, `IN_PROGRESS`, `DONE`.

### Comment — `/comment`

| Method | Path |
|--------|------|
| GET | `/comment/list/{ticketId}` |
| PUT | `/comment/create` |

### Errors

`ExcpetionController` maps:

- 404 — ticket / user / project-by-code not found
- 401 — bad login
- 400 — duplicate user, duplicate project code, unsupported user deletion

---

## How we work

1. **Small PRs.** One concern per change (bugfix, endpoint, SQL, etc.).
2. **Match existing style.** Controllers stay thin; logic goes in services. Use Lombok like neighboring classes.
3. **Do not commit** API keys, production passwords, or `.idea` / `target/`.