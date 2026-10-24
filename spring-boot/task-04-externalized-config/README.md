# Task 4 (Optional) - CRUD application: externalized configuration

The secured CRUD application from Task 3 with environment-specific configuration based on Spring Profiles.

| Profile | Database | `ddl-auto` | Pool size | Logging |
|---|---|---|---|---|
| `local` (default) | H2 in-memory | `create-drop` | default | `DEBUG`, SQL shown |
| `dev` | PostgreSQL `localhost:5432/products_dev` | `update` | 5 | `DEBUG`, SQL shown |
| `stg` | PostgreSQL `${DB_HOST}:5432/products_stg` | `validate` | 10 | `INFO` |
| `prod` | PostgreSQL `${DB_HOST}:5432/products_prod` | `validate` | 30 | `WARN` / `INFO` |

Shared settings (application name, OAuth2 client) are in `application.yml`; each environment has its own
`application-{profile}.yml`. Credentials are never stored in files: `stg` and `prod` require the
`DB_USERNAME` and `DB_PASSWORD` environment variables, `DB_HOST` can override the default host.

## Run

```
mvn spring-boot:run                                   # local profile (default)
java -jar target/crud-externalized-config-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
SPRING_PROFILES_ACTIVE=prod DB_USERNAME=... DB_PASSWORD=... java -jar target/crud-externalized-config-0.0.1-SNAPSHOT.jar
```

The active profile is printed at startup: `The following 1 profile is active: "dev"`.
