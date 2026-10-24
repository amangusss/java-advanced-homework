package com.epam.homework.crud.product;

import com.epam.homework.crud.monitoring.ProductMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMetrics metrics;

    @InjectMocks
    private ProductService service;

    @Test
    void createSavesProductAndCountsIt() {
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = service.create(new ProductRequest("Laptop", new BigDecimal("1299.99"), "15 inch"));

        assertThat(response.name()).isEqualTo("Laptop");
        assertThat(response.price()).isEqualByComparingTo("1299.99");
        assertThat(response.description()).isEqualTo("15 inch");
        verify(metrics).productCreated();
    }

    @Test
    void findAllMapsEntitiesToResponses() {
        when(repository.findAll()).thenReturn(List.of(
                new Product("Laptop", new BigDecimal("1299.99"), null),
                new Product("Mouse", new BigDecimal("19.90"), null)));

        assertThat(service.findAll()).extracting(ProductResponse::name).containsExactly("Laptop", "Mouse");
    }

    @Test
    void findByIdThrowsWhenProductDoesNotExist() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(42L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void updateChangesAllFields() {
        Product existing = new Product("Laptop", new BigDecimal("1299.99"), null);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        ProductResponse response = service.update(1L, new ProductRequest("Laptop Pro", new BigDecimal("1499.00"), "16 inch"));

        assertThat(response.name()).isEqualTo("Laptop Pro");
        assertThat(response.price()).isEqualByComparingTo("1499.00");
        assertThat(response.description()).isEqualTo("16 inch");
    }

    @Test
    void updateThrowsWhenProductDoesNotExist() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(42L, new ProductRequest("A", BigDecimal.ONE, null)))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void deleteRemovesExistingProduct() {
        Product existing = new Product("Laptop", new BigDecimal("1299.99"), null);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(repository).delete(existing);
    }

    @Test
    void deleteThrowsAndRemovesNothingWhenProductDoesNotExist() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(42L)).isInstanceOf(ProductNotFoundException.class);
        verify(repository, never()).delete(any());
    }
}
