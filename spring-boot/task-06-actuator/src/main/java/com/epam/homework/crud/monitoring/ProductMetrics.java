package com.epam.homework.crud.monitoring;

import com.epam.homework.crud.product.ProductRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/** Business metrics exposed to Prometheus as {@code products_added_total} and {@code products_count}. */
@Component
public class ProductMetrics {

    private final Counter created;

    public ProductMetrics(MeterRegistry registry, ProductRepository repository) {
        this.created = Counter.builder("products.added")
                .description("Number of products created through the API")
                .register(registry);
        Gauge.builder("products.count", repository, ProductRepository::count)
                .description("Current number of products in the store")
                .register(registry);
    }

    public void productCreated() {
        created.increment();
    }
}
