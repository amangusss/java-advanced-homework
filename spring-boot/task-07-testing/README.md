# Task 7 (Optional) - CRUD application: testing

The CRUD application from Task 6 covered by automated tests. Run them with:

```
mvn verify
```

Tests use the `test` profile (`src/test/resources/application-test.yml`): an in-memory **H2** database
with the real Flyway migrations applied, so the schema under test is the production schema.

| Test class | Kind | What it verifies |
|---|---|---|
| `ProductRepositoryTest` | `@DataJpaTest` | save / find / delete against H2, nullable `description` |
| `ProductServiceTest` | unit (Mockito) | service logic, `ProductNotFoundException`, metrics counter |
| `ProductControllerTest` | `@WebMvcTest` + `MockMvc` | status codes, `Location` header, validation (400), `ProblemDetail` (404) |
| `ProductApiSecurityIntegrationTest` | `@SpringBootTest` + `MockMvc` | whole stack: JWT auth (401), roles (`USER` 403 / `ADMIN` 2xx), actuator access rules |

The integration test authenticates with Spring Security's `jwt()` request post-processor, which injects a
decoded JWT with the required authorities, so no running authorization flow is needed.
