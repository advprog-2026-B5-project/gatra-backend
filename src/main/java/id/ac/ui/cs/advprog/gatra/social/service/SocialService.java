package id.ac.ui.cs.advprog.gatra.social.service;

import id.ac.ui.cs.advprog.gatra.auth.dto.PublicProfileResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.UserSearchResponse;
import java.util.List;

public interface SocialService {
    List<UserSearchResponse> searchUsers(String query);
    PublicProfileResponse getPublicProfile(String username);
}