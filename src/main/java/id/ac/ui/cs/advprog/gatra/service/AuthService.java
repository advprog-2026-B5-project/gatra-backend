package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AuthResponse;
import id.ac.ui.cs.advprog.gatra.dto.LoginRequest;
import id.ac.ui.cs.advprog.gatra.dto.RegisterRequest;

public interface AuthService {
    AuthResponse registerStudent(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}