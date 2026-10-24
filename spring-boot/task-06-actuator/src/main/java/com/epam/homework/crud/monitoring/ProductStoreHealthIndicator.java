package com.epam.homework.crud.monitoring;

import com.epam.homework.crud.product.ProductRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/** Reports whether the product store can actually be queried, not just whether a connection exists. */
@Component("productStore")
public class ProductStoreHealthIndicator implements HealthIndicator {

    private final ProductRepository repository;

    public ProductStoreHealthIndicator(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        try {
            return Health.up().withDetail("products", repository.count()).build();
        } catch (RuntimeException ex) {
            return Health.down(ex).build();
        }
    }
}
