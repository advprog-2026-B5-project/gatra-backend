package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.PublicProfileResponse;
import id.ac.ui.cs.advprog.gatra.dto.UserSearchResponse;
import java.util.List;

public interface SocialService {
    List<UserSearchResponse> searchUsers(String query);
    PublicProfileResponse getPublicProfile(String username);
}