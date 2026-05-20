package id.ac.ui.cs.advprog.gatra.auth.service;

import id.ac.ui.cs.advprog.gatra.auth.dto.UserResponse;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    void deleteUserById(UUID userId);
    User updateUser(UUID userId, String newDisplayName, String newPhoneNumber);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    User getUserEntityById(UUID id);
    User getUserEntityByUsername(String username);
}