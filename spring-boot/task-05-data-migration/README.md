# Task 5 (Optional) - CRUD application: data migrating

The CRUD application from Task 4 with schema managed by **Flyway**.

- Migrations live in `src/main/resources/db/migration` and run automatically on application startup.
- `V1__create_product_table.sql` creates the `product` table.
- `V2__add_product_description.sql` adds the `description` column (schema update), exposed through the API.
- Hibernate no longer generates the schema: `spring.jpa.hibernate.ddl-auto` is `validate` in every profile,
  so a mismatch between entities and migrations fails the startup.
- The SQL is portable, so the same scripts run on H2 (`local` profile) and PostgreSQL (`dev`, `stg`, `prod`).

To change the schema, add a new versioned script (`V3__...sql`); never edit an already applied one.

## Run

```
mvn spring-boot:run
```

The startup log shows the applied migrations:

```
Migrating schema "PUBLIC" to version "1 - create product table"
Migrating schema "PUBLIC" to version "2 - add product description"
Successfully applied 2 migrations to schema "PUBLIC", now at version v2
```
