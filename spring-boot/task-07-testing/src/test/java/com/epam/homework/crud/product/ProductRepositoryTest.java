package com.epam.homework.crud.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void savesProductAndGeneratesId() {
        Product saved = repository.save(new Product("Laptop", new BigDecimal("1299.99"), "15 inch"));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).hasValueSatisfying(found -> {
            assertThat(found.getName()).isEqualTo("Laptop");
            assertThat(found.getPrice()).isEqualByComparingTo("1299.99");
            assertThat(found.getDescription()).isEqualTo("15 inch");
        });
    }

    @Test
    void allowsProductWithoutDescription() {
        Product saved = repository.save(new Product("Mouse", new BigDecimal("19.90"), null));

        assertThat(repository.findById(saved.getId()))
                .hasValueSatisfying(found -> assertThat(found.getDescription()).isNull());
    }

    @Test
    void findAllReturnsEverySavedProduct() {
        repository.save(new Product("Laptop", new BigDecimal("1299.99"), null));
        repository.save(new Product("Mouse", new BigDecimal("19.90"), null));

        assertThat(repository.findAll()).extracting(Product::getName).containsExactlyInAnyOrder("Laptop", "Mouse");
    }

    @Test
    void deleteRemovesProduct() {
        Product saved = repository.save(new Product("Laptop", new BigDecimal("1299.99"), null));

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
