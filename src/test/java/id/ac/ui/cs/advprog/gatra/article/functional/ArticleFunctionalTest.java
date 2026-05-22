package id.ac.ui.cs.advprog.gatra.article.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.article.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.article.dto.CategoryRequest;
import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Functional: Complete CRUD Flow for Category and Article")
    @WithMockUser(username = "gatra_author")
    void testCompleteArticleFlow() throws Exception {
        
        // 0. PERSIAPAN DATA (SEEDING) PENGGUNA
        User mockAuthor = new User();
        mockAuthor.setUsername("gatra_author");
        mockAuthor.setEmail("author@gatra.com");
        mockAuthor.setPassword("rahasia123");
        mockAuthor.setDisplayName("Sang Penulis Gatra");
        mockAuthor.setRole(Role.ROLE_STUDENT); // Gunakan Enum Role yang valid
        userRepository.save(mockAuthor);
        
        // 1. CREATE CATEGORY (POST)
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Teknologi");

        MvcResult categoryResult = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teknologi"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String categoryResponseStr = categoryResult.getResponse().getContentAsString();
        String categoryIdStr = JsonPath.parse(categoryResponseStr).read("$.id");
        UUID categoryId = UUID.fromString(categoryIdStr);

        
        // 2. CREATE ARTICLE (POST)
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setTitle("Panduan Spring Boot");
        articleRequest.setContent("Ini adalah isi dari panduan Spring Boot.");
        articleRequest.setCategoryId(categoryId);

        MvcResult articleResult = mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Panduan Spring Boot"))
                .andExpect(jsonPath("$.content").value("Ini adalah isi dari panduan Spring Boot."))
                .andExpect(jsonPath("$.categoryId").value(categoryId.toString()))
                .andExpect(jsonPath("$.createdBy").value("gatra_author"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String articleResponseStr = articleResult.getResponse().getContentAsString();
        String articleId = JsonPath.parse(articleResponseStr).read("$.id");

        
        // 3. READ ALL ARTICLES (GET)
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == '" + articleId + "')]").exists());

        
        // 4. READ ARTICLE BY ID (GET)
        mockMvc.perform(get("/api/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Panduan Spring Boot"));

        
        // 5. UPDATE ARTICLE (PUT)
        ArticleRequest updateRequest = new ArticleRequest();
        updateRequest.setTitle("Panduan Spring Boot Lanjutan");
        updateRequest.setContent("Isi telah diperbarui dengan materi lanjutan.");
        updateRequest.setCategoryId(categoryId);

        mockMvc.perform(put("/api/articles/" + articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Panduan Spring Boot Lanjutan"))
                .andExpect(jsonPath("$.content").value("Isi telah diperbarui dengan materi lanjutan."));

        
        // 6. DELETE ARTICLE (DELETE)
        mockMvc.perform(delete("/api/articles/" + articleId))
                .andExpect(status().isNoContent());

        // Verifikasi apakah artikel masuk ke endpoint "deleted"
        mockMvc.perform(get("/api/articles/deleted"))
                .andExpect(status().isOk());
    }
}