package se.experis.com.case2020.lagalt.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Acl;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.models.project.ProjectCreate;
import se.experis.com.case2020.lagalt.models.project.ProjectMember;
import se.experis.com.case2020.lagalt.models.project.ProjectNonMember;
import se.experis.com.case2020.lagalt.models.project.ProjectSearch;
import se.experis.com.case2020.lagalt.utils.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ProjectService {


    public ResponseEntity<CommonResponse> getProjectSearch(HttpServletRequest request, HttpServletResponse response, String projectId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("projects").document(projectId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        ProjectSearch project = null;

        if (document.exists()) {

            CollectionReference membersReference = dbFirestore.collection("projects").document(projectId).collection("members");
            Iterable<DocumentReference> members = membersReference.listDocuments();

            project = document.toObject(ProjectSearch.class);
            ProjectSearch tempProject = project;
            Set<String> tagSet = new HashSet<>();
            members.forEach(collection -> {
                tempProject.setMemberCount(tempProject.getMemberCount() + 1);
            });
            project = tempProject;

            CollectionReference tagCollectionReference = dbFirestore.collection("projects").document(projectId).collection("tags");
            Iterable<DocumentReference> tags = tagCollectionReference.listDocuments();

            tags.forEach(tag -> {
                tagSet.add(tag.getId());
            });
            project.setTags(tagSet);
            project.setProjectId(projectId);

            cr.message = "Search result for project with Id: " + projectId;
            resp = HttpStatus.OK;
            response.addHeader("Location", "/projects/" + projectId);
        } else {
            cr.message = "No project found with id " + projectId;
            resp = HttpStatus.NOT_FOUND;
        }

        cr.data = project;
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, HttpServletResponse response, String projectId, String userId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();

        DocumentReference projectReference = dbFirestore.collection("projects").document(projectId);
        DocumentReference userReference = projectReference.collection("members").document(userId);
        CollectionReference links = projectReference.collection("links");

        ApiFuture<DocumentSnapshot> userFuture = userReference.get();
        ApiFuture<DocumentSnapshot> projectFuture = projectReference.get();

        DocumentSnapshot userDocument = userFuture.get();
        DocumentSnapshot projectDocument = projectFuture.get();


        if (projectDocument.exists()) {

            Map<String, Set<String>> projectInfo = new HashMap<>();
            Iterable<CollectionReference> projectCollections = projectReference.listCollections();
            projectCollections.forEach(collection -> {

                Iterable<DocumentReference> projectIds = collection.listDocuments();
                projectIds.forEach(id -> {
                    if (!id.get().equals("chat") && !id.get().equals("links")) {
                        projectInfo.computeIfAbsent(collection.getId(), k -> new HashSet<>()).add(id.getId());
                    }
                });
            });

            if (userDocument.exists() || projectDocument.toObject(ProjectMember.class).getOwnerId().equals(userDocument.getId())) {
                ProjectMember project = projectDocument.toObject(ProjectMember.class);
                Map<String, String> linksMap = new HashMap<>();

                Iterable<DocumentReference> projectlinks = links.listDocuments();
                projectlinks.forEach(link -> {
                    ApiFuture<DocumentSnapshot> linkFuture = link.get();
                    try {
                        DocumentSnapshot linkSnapShot = linkFuture.get();
                        linksMap.put(linkSnapShot.getData().get("name").toString(), linkSnapShot.getData().get("url").toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });

                project = (ProjectMember) addDataToProject(project, projectInfo, projectId);
                project.setMessageBoards(projectInfo.get("messageBoards"));
                project.setLinks(linksMap);

                cr.data = project;
            } else {
                ProjectNonMember project = projectDocument.toObject(ProjectNonMember.class);
                project = addDataToProject(project, projectInfo, projectId);

                cr.data = project;
            }
            resp = HttpStatus.OK;
            cr.message = "Project details for " + projectId + " found.";

        } else {
            resp = HttpStatus.NOT_FOUND;
            cr.message = "No project with id: " + projectId + "found";
        }

        response.addHeader("Location", "/projects/" + projectId);
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, HttpServletResponse response, ProjectCreate project){
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;

        project.setIndustry(addIndustry(project.getIndustry()));
        addTagsToCollection(project);
        project.setTags(null);

        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("projects").document(project.getProjectId()).set(project);



        response.addHeader("Location", "/projects/" + project.getProjectId());
        cr.message = "Project with id " + project.getProjectId() + " Created at " + project.getCreatedAt().toDate();
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request, HttpServletResponse response, ProjectMember project) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("projects").document(project.getProjectId());
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {

            addTagsToCollection(project);
            project.setTags(null);
            project.setIndustry(addIndustry(project.getIndustry()));

            Firestore dbFireStore = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("projects").document(project.getProjectId()).set(project);

            cr.data = collectionApiFuture.get().getUpdateTime().toString();
            cr.message = "Project data successfully updated for project: " + project.getProjectId();
            resp = HttpStatus.OK;
            response.addHeader("Location", "/projects/" + project.getProjectId());

        } else {
            resp = HttpStatus.NOT_FOUND;
            cr.message = "No Project with Id " + project.getProjectId() + " Found";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ProjectNonMember addDataToProject(ProjectNonMember project, Map<String, Set<String>> projectInfo, String projectId) {
        project.setMembers(projectInfo.get("members"));
        project.setAdmins(projectInfo.get("admins"));
        project.setTags(projectInfo.get("tags"));
        project.setProjectId(projectId);
        return project;
    }

    void deleteCollection(CollectionReference collection, int batchSize) {
        try {
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                deleteCollection(collection, batchSize);
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }

    public void addTagsToCollection(ProjectNonMember project) {

        if (project.getTags() != null) {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            deleteCollection(dbFirestore.collection("projects").document(project.getProjectId()).collection("tags"), 10);
            project.getTags().forEach(tag -> {
                if (EnumUtils.isValidEnum(Tag.class, tag)) {
                    ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("projects").document(project.getProjectId())
                            .collection("tags").document(tag).set(new HashMap<String, Object>());
                }
            });
        }
    }

    public String addIndustry(String industry) {
        if (industry != null) {
            if (EnumUtils.isValidEnum(Industry.class, industry)) {
                return industry;
            }
        }
        return null;
    }
}
