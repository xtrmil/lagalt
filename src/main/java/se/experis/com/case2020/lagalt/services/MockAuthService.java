package se.experis.com.case2020.lagalt.services;

import org.springframework.stereotype.Service;

@Service
public class MockAuthService extends AuthService {

    @Override
    public boolean belongsToUser(String userId, String invalidToken) {
        return true;
    }

    @Override
    public boolean isProjectAdmin(String projectId, String invalidToken) {
        return true;
    }

    @Override
    public boolean isProjectMember(String projectId, String invalidToken) {
        return true;
    }

    @Override
    public String getUserId(String invalidToken) {
        return "E0DE04K9y6WP5VeKYQRbYBNtMPi2";
    }

    @Override
    public String getUsername(String userId) {
        return "Bumpfel";
    }
}