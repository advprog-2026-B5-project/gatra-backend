package id.ac.ui.cs.advprog.gatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.dto.UpdateUserRequest;
import id.ac.ui.cs.advprog.gatra.dto.UserResponse;
import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UUID dummyId = UUID.randomUUID();

    @Test
    void testGetAllUsers_ReturnsOk() throws Exception {
        UserResponse response = UserResponse.builder().id(dummyId).username("anya").role(Role.ROLE_STUDENT).build();
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(response));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("anya"));
    }

    @Test
    void testGetAllUsers_ReturnsInternalServerError() throws Exception {
        Mockito.when(userService.getAllUsers()).thenThrow(new RuntimeException("Database down"));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testEditUser_ReturnsOk() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setDisplayName("Anya Keren");
        request.setPhoneNumber("081122334455");
        User updatedUser = User.builder().id(dummyId).displayName("Anya Keren").phoneNumber("081122334455").build();
        Mockito.when(userService.updateUser(eq(dummyId), any(), any())).thenReturn(updatedUser);
        mockMvc.perform(put("/api/users/" + dummyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Anya Keren"));
    }

    @Test
    void testEditUser_ReturnsBadRequest_WhenIllegalArgumentException() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        Mockito.when(userService.updateUser(eq(dummyId), any(), any()))
                .thenThrow(new IllegalArgumentException("Data tidak valid"));
        mockMvc.perform(put("/api/users/" + dummyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Data tidak valid"));
    }

    @Test
    void testEditUser_ReturnsInternalServerError_WhenGenericException() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        Mockito.when(userService.updateUser(eq(dummyId), any(), any()))
                .thenThrow(new RuntimeException("Server error"));
        mockMvc.perform(put("/api/users/" + dummyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Terjadi kesalahan pada server saat mengupdate user"));
    }

    @Test
    void testDeleteUser_ReturnsOk() throws Exception {
        Mockito.doNothing().when(userService).deleteUserById(dummyId);
        mockMvc.perform(delete("/api/users/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(content().string("User berhasil dihapus beserta profilnya"));
    }

    @Test
    void testDeleteUser_ReturnsBadRequest_WhenIllegalArgumentException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User tidak ditemukan"))
                .when(userService).deleteUserById(dummyId);
        mockMvc.perform(delete("/api/users/" + dummyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User tidak ditemukan"));
    }

    @Test
    void testDeleteUser_ReturnsInternalServerError_WhenGenericException() throws Exception {
        Mockito.doThrow(new RuntimeException("Gagal konek DB"))
                .when(userService).deleteUserById(dummyId);
        mockMvc.perform(delete("/api/users/" + dummyId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Terjadi kesalahan pada server saat menghapus user"));
    }

    @Test
    void testGetUserById_ReturnsOk() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(dummyId).username("anya").email("anya@gatra.com").build();
        Mockito.when(userService.getUserById(dummyId)).thenReturn(response);
        mockMvc.perform(get("/api/users/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anya"))
                .andExpect(jsonPath("$.email").value("anya@gatra.com"));
    }

    @Test
    void testGetUserById_ReturnsBadRequest_WhenNotFound() throws Exception {
        Mockito.when(userService.getUserById(dummyId))
                .thenThrow(new IllegalArgumentException("User tidak ditemukan"));
        mockMvc.perform(get("/api/users/" + dummyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User tidak ditemukan"));
    }

    @Test
    void testGetUserById_ReturnsInternalServerError() throws Exception {
        Mockito.when(userService.getUserById(dummyId))
                .thenThrow(new RuntimeException("Database error"));
        mockMvc.perform(get("/api/users/" + dummyId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Terjadi kesalahan pada server saat mengambil data profil"));
    }
}