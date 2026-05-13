package id.ac.ui.cs.advprog.gatra.auth.service;

import id.ac.ui.cs.advprog.gatra.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.gatra.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse registerStudent(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}