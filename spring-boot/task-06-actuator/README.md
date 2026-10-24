# Task 6 - Actuator

The CRUD application from Task 5 with Spring Boot Actuator for monitoring.

## Endpoints

Exposed: `health`, `info`, `metrics`, `prometheus`.

| Endpoint | Anonymous | `USER` | `ADMIN` |
|---|---|---|---|
| `/actuator/health` | 200, status only | 200, status only | 200, with details |
| other `/actuator/**` | 401 | 403 | 200 |

Use the JWT from Task 3 (`Authorization: Bearer <token>`) to call protected endpoints.

## Custom health indicator

`ProductStoreHealthIndicator` (`health.productStore`) runs a real query against the product table and
reports `UP` with the number of products, or `DOWN` with the exception when the store cannot be queried.

## Custom metrics

`ProductMetrics` registers Micrometer metrics exported by the Prometheus registry at `/actuator/prometheus`:

| Metric | Type | Meaning |
|---|---|---|
| `products_added_total` | counter | products created through the API |
| `products_count` | gauge | current number of products in the store |

Every metric carries the `application` tag. The names avoid the `_created` / `_total` suffix clash
of the Prometheus client (`products.created` would silently collide with `products.total`).

## Run

```
mvn spring-boot:run
curl http://localhost:8080/actuator/health
curl -H "Authorization: Bearer <admin token>" http://localhost:8080/actuator/prometheus
```
