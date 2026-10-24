package com.epam.homework.crud.product;

import com.epam.homework.crud.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Controller layer only: security is covered by {@link ProductApiSecurityIntegrationTest}. */
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    private static final String VALID_BODY = "{\"name\":\"Laptop\",\"price\":1299.99,\"description\":\"15 inch\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Test
    void createReturns201WithLocationHeader() throws Exception {
        when(service.create(any(ProductRequest.class)))
                .thenReturn(new ProductResponse(1L, "Laptop", new BigDecimal("1299.99"), "15 inch"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void createRejectsInvalidBody() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllReturnsProducts() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new ProductResponse(1L, "Laptop", new BigDecimal("1299.99"), null),
                new ProductResponse(2L, "Mouse", new BigDecimal("19.90"), null)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    void findByIdReturnsProduct() throws Exception {
        when(service.findById(1L)).thenReturn(new ProductResponse(1L, "Laptop", new BigDecimal("1299.99"), null));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void findByIdReturns404ProblemDetailWhenMissing() throws Exception {
        when(service.findById(42L)).thenThrow(new ProductNotFoundException(42L));

        mockMvc.perform(get("/api/products/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product with id 42 not found"));
    }

    @Test
    void updateReturnsUpdatedProduct() throws Exception {
        when(service.update(any(Long.class), any(ProductRequest.class)))
                .thenReturn(new ProductResponse(1L, "Laptop", new BigDecimal("1299.99"), "15 inch"));

        mockMvc.perform(put("/api/products/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("15 inch"));
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/api/products/1")).andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        doThrow(new ProductNotFoundException(42L)).when(service).delete(42L);

        mockMvc.perform(delete("/api/products/42")).andExpect(status().isNotFound());
    }
}
