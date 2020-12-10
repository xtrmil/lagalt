package se.experis.com.case2020.lagalt.services;

import org.springframework.stereotype.Service;

@Service
public class MockAuthService extends AuthService {

    @Override
    public boolean belongsToUser(String userId, String jwtToken) {
        return true;
    }

    @Override
    public boolean isProjectAdmin(String projectId, String jwtToken) {
        return true;
    }

    @Override
    public boolean isProjectMember(String projectId, String jwtToken) {
        return true;
    }
}
