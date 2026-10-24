package com.epam.homework.crud.product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price) {

    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }
}
