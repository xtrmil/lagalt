package se.experis.com.case2020.lagalt.services;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.application.ApplicationAdminView;
import se.experis.com.case2020.lagalt.models.application.ApplicationProfileView;
import se.experis.com.case2020.lagalt.models.user.UserPublic;
import se.experis.com.case2020.lagalt.utils.Command;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;


@Service
public class ApplicationService {
    UserService userService = new UserService();
    MockAuthService authService = new MockAuthService();

    public ResponseEntity<CommonResponse> getApplications(HttpServletRequest request, HttpServletResponse response, String projectId, String Authorization){
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;
        Set<ApplicationAdminView> applicationSet = new HashSet<>();
        if(authService.loggedInUser(Authorization) != null) {
            Firestore dbFireStore = FirestoreClient.getFirestore();
            CollectionReference ApplicationsCollection =  dbFireStore.collection("projects").document(projectId).collection("applications");

            Iterable<DocumentReference> projectCollections = ApplicationsCollection.listDocuments();

            projectCollections.forEach( application ->{
                ApplicationAdminView applicationAdminView = new ApplicationAdminView();
                try {
                    DocumentReference ApplicationsUser = dbFireStore.collection("applications").document(application.getId());
                    ApiFuture<DocumentSnapshot> userFuture = ApplicationsUser.get();
                    DocumentSnapshot userDocument = userFuture.get();
                    UserPublic user = userService.getUserPublic(userDocument.getData().get("userId").toString());

                    applicationAdminView.setUser(user);
                    applicationSet.add(applicationAdminView);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            cr.data = applicationSet;
            response.addHeader("Location", "/projects/" + projectId + "/applications");
            cr.message ="Successfully retrieved all pending applications for project: " + projectId;
        }else{
            cr.data = null;
            resp = HttpStatus.UNAUTHORIZED;
            cr.message = "Not authorized to retrieve this data";
        }


        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createApplication(HttpServletRequest request, HttpServletResponse response, String projectId, String Authorization, ObjectNode motivation) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;

        Firestore dbFireStore = FirestoreClient.getFirestore();
        if(authService.loggedInUser(Authorization) != null){
            DocumentReference userReference = dbFireStore.collection("users").document(authService.loggedInUser(Authorization));
            DocumentReference projectReference = dbFireStore.collection("projects").document(projectId);

            ApplicationProfileView applicationProfileView = new ApplicationProfileView();
            applicationProfileView.setProjectId(projectId);
            applicationProfileView.setMotivation(motivation.get("motivation").asText());

            var ref = dbFireStore.collection("applications").document(projectId).collection("applications")
                    .document();
            ApiFuture<WriteResult> applicationscollectionApiFuture = dbFireStore.collection("applications").document(ref.getId()).set(applicationProfileView);
            ApiFuture<WriteResult> userApplicationsApiFuture = userReference.collection("appliedTo").document(ref.getId()).set(new HashMap<>());
            ApiFuture<WriteResult> projectApplicationApiFuture = projectReference.collection("applications").document(ref.getId()).set(new HashMap<>());

            response.addHeader("Location", "/projects/" + projectId + "/application/");
            cr.data = "your motivation: " + motivation.get("motivation").asText();
            cr.message = "Application successfully submitted to project: " + projectId;
        }

        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateApplication(HttpServletRequest request, HttpServletResponse response, String projectId, @RequestBody ApplicationAdminView application, String Authorization) {

        return null;
    }
}
