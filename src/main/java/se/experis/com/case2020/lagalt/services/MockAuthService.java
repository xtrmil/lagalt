package se.experis.com.case2020.lagalt.services;

import org.springframework.stereotype.Service;

@Service
public class MockAuthService extends AuthService {

    @Override
    public boolean belongsToUser(String userId, String invalidToken) {
        return true;
    }

    @Override
    public boolean isProjectAdmin(String owner, String projectName, String invalidToken) {
        return true;
    }

    @Override
    public boolean isProjectMember(String owner, String projectName, String invalidToken) {
        return true;
    }

    @Override
    public String getUserIdFromToken(String invalidToken) {
        return "E0DE04K9y6WP5VeKYQRbYBNtMPi2";
    }

    @Override
    public String getUsername(String userId) {
        return "Bumpfel";
    }

    @Override
    public String getUsernameFromToken(String invalidToken) {
        return "Bumpfel";
    }
}