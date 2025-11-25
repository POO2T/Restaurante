# 🧩 Back-End Restaurante — Perfis e instruções de execução

Este diretório contém o backend em Spring Boot do projeto **Restaurante**.

Foram adicionados dois perfis de aplicação (arquivos de propriedades) em `src/main/resources`:

- `application-postgres.properties` — configuração para execução com PostgreSQL (desenvolvimento/produção).
- `application-h2.properties` — configuração para execução com H2 em memória (testes / execução local rápida).

---

## Pré-requisitos

- Java 24+ (ou versão compatível com o Spring Boot definido no `pom.xml`).
- Maven ou utilize o wrapper incluído (`mvnw.cmd` / `mvnw`).
- Se for usar o perfil Postgres: um servidor PostgreSQL em execução e um banco (ex.: `restaurante_db`).

---

## Executando com PostgreSQL (recomendado para desenvolvimento)

1. Crie o banco de dados (exemplo com `psql`, ajuste usuário/host/porta conforme necessário):

```powershell
psql -U postgres -c "CREATE DATABASE restaurante_db;"
```

2. Ajuste `src/main/resources/application-postgres.properties` com as credenciais corretas (username/password) se necessário.

3. Inicie a aplicação com o profile `postgres`:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

Ou empacote e execute o JAR:

```powershell
.\mvnw.cmd package -DskipTests
java -jar target\Back-End_Restaurante-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres
```

> A aplicação usará as configurações de `application-postgres.properties` (JDBC URL, dialect, etc.).

---

## Executando com H2 (teste / desenvolvimento leve)

O perfil `h2` utiliza um banco em memória — bom para testes rápidos.

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```

Executando os testes com H2:

```powershell
.\mvnw.cmd test "-Dspring.profiles.active=h2"
```

### Console do H2

Com o perfil `h2` ativo, o console web estará disponível em:

```
http://localhost:8080/h2-console
```

Use a JDBC URL definida em `application-h2.properties` para conectar-se.

---

## Testes como Admin
Com o banco de dados configurado, você pode utilizar os comandos a seguir para adicionar um perfil de administrador no sistema para poder realizar as configurações do programa

```powershell
@'
{
  "nome": "Admin Teste",
  "email": "admin@local.test",
  "senha": "SenhaSegura123!",
  "telefone": "+55 11 99999-0000",
  "dataCriacao": "2025-11-05T00:00:00Z",
  "tipoFuncionario": "ADMINISTRADOR",
  "salario": 0,
  "dataAdmissao": "2025-11-05T00:00:00Z"
}
'@ > payload.json
```
- Criação do JSON com informações do Admin

```powershell
@'
{
  "email": "existing_admin@local.test",
  "senha": "SenhaDoAdminExistente123!"
}
'@ > login.json
```

- Criação do JSON com informações de login para Admin

```powershell
$login = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType 'application/json' -Body (Get-Content .\login.json -Raw)
$token = $login.token
Write-Output "Token obtido: $token"
```

- Criando uma requisição para obter um token de autenticação

```powershell
$headers = @{ Authorization = "Bearer $token" }
Invoke-RestMethod -Uri "http://localhost:8080/api/funcionarios" -Method Post -ContentType 'application/json' -Headers $headers -Body (Get-Content .\payload.json -Raw)
```

- Finalização para cadastro


## 🧭Observações e solução de problemas

---

- Se alterar `server.servlet.context-path` em algum perfil, os endpoints serão prefixados (ex.: `/api`). Atualize as URLs do frontend conforme necessário.
- Spring Security: o `formLogin()` padrão espera `application/x-www-form-urlencoded`; SPAs geralmente enviam JSON. Para SPAs é recomendado expor um endpoint REST (ex.: `/api/auth/login`) que aceite JSON e retorne token (JWT) ou informações do usuário.
- CORS: se houver falha no preflight, verifique a configuração de CORS no backend e a origem do frontend (padrão: `http://localhost:4200`).
- É possível adicionar scripts automatizados no pom.xml ou em um Makefile para simplificar tarefas comuns, como:

```powershell
make run-h2
make run-postgres
make test-h2
```
