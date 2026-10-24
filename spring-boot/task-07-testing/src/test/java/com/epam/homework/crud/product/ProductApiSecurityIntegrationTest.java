package com.epam.homework.crud.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Full application context, H2 database with Flyway migrations, real security filter chains. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiSecurityIntegrationTest {

    private static final String BODY = "{\"name\":\"Laptop\",\"price\":1299.99}";

    @Autowired
    private MockMvc mockMvc;

    private static RequestPostProcessor asUser() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private static RequestPostProcessor asAdmin() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Test
    void apiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/products")).andExpect(status().isUnauthorized());
    }

    @Test
    void userCanReadButNotModify() throws Exception {
        mockMvc.perform(get("/api/products").with(asUser())).andExpect(status().isOk());
        mockMvc.perform(post("/api/products").with(asUser()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/products/1").with(asUser())).andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateReadAndDelete() throws Exception {
        String location = mockMvc.perform(post("/api/products").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        String path = location.substring(location.indexOf("/api"));
        mockMvc.perform(get(path).with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
        mockMvc.perform(delete(path).with(asAdmin())).andExpect(status().isNoContent());
        mockMvc.perform(get(path).with(asAdmin())).andExpect(status().isNotFound());
    }

    @Test
    void healthIsPublicButOtherActuatorEndpointsAreAdminOnly() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/metrics")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/actuator/metrics").with(asUser())).andExpect(status().isForbidden());
        mockMvc.perform(get("/actuator/metrics").with(asAdmin())).andExpect(status().isOk());
    }

    @Test
    void invalidBodyIsRejectedWith400() throws Exception {
        mockMvc.perform(post("/api/products").with(asAdmin()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":-1}"))
                .andExpect(status().isBadRequest());
    }
}
