package se.experis.com.case2020.lagalt.services;

import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MockAuthService extends AuthService {

    @Autowired
    private ProjectService projectService;
    
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
        return "Bumpfel".toLowerCase();
    }

    @Override
    public String getUsernameFromToken(String invalidToken) {
        return "Bumpfel".toLowerCase();
    }

    @Override
    public boolean isPartOfProjectStaff(String owner, String projectName, String invalidToken) {
        String userId = getUserIdFromToken(invalidToken);

        try {
            var db = FirestoreClient.getFirestore();
            boolean isAdmin = isPartOfProjectCollection(owner, projectName, "members");
            boolean isMember = isPartOfProjectCollection(owner, projectName, "admins");
            String projectId = projectService.getProjectId(owner, projectName);
            
            var ownerId = db.collection("projects").document(projectId).get().get().getString("owner");
            return ownerId == userId || isAdmin || isMember;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isPartOfProjectCollection(String owner, String projectName, String collection) {
        String userId = getUserIdFromToken(null);

        try {
            var db = FirestoreClient.getFirestore();
            String projectId = projectService.getProjectId(owner, projectName);
            var ref = db.collection("projects").document(projectId).collection(collection).document(userId).get().get();
            return ref.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}