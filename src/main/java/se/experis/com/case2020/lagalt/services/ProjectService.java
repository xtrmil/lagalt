package se.experis.com.case2020.lagalt.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.models.project.ProjectMember;
import se.experis.com.case2020.lagalt.models.project.ProjectNonMember;
import se.experis.com.case2020.lagalt.models.project.ProjectSearch;
import se.experis.com.case2020.lagalt.utils.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ProjectService {

    public ResponseEntity<CommonResponse> getProjectSearch(HttpServletRequest request, HttpServletResponse response, String projectId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference projectReference = dbFirestore.collection("projects").document(projectId);
        ApiFuture<DocumentSnapshot> future = projectReference.get();
        DocumentSnapshot projectSnapshot = future.get();

        ProjectSearch project = null;
        ProjectNonMember createdAtProject = null;

        if (projectSnapshot.exists()) {

            project = projectSnapshot.toObject(ProjectSearch.class);
            createdAtProject = projectSnapshot.toObject(ProjectNonMember.class);
            CollectionReference membersReference = projectReference.collection("members");
            if (membersReference != null) {
                ProjectSearch tempProject = project;
                Iterable<DocumentReference> members = membersReference.listDocuments();

                members.forEach(collection -> {
                    tempProject.setMemberCount(tempProject.getMemberCount() + 1);
                });
                project = tempProject;
            }
            CollectionReference tagCollectionReference = projectReference.collection("tags");
            if (tagCollectionReference != null) {
                Iterable<DocumentReference> tags = tagCollectionReference.listDocuments();
                Set<String> tagSet = new HashSet<>();
                tags.forEach(tag -> {
                    tagSet.add(tag.getId());
                });
                project.setTags(tagSet);
            }

            project.setProjectId(projectId);
            project.setCreatedAt(createdAtProject.getCreatedAtForDb().toDate().toString());

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

                project = (ProjectMember) addDataToResponseProject(project, projectInfo, projectId);
                project.setMessageBoards(projectInfo.get("messageBoards"));
                project.setLinks(linksMap);

                cr.data = project;
            } else {
                ProjectNonMember project = projectDocument.toObject(ProjectNonMember.class);
                project = addDataToResponseProject(project, projectInfo, projectId);

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

    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, HttpServletResponse response, ProjectNonMember project) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;

        project.setIndustry(addIndustry(project.getIndustry()));

        addDataToCollection(project.getProjectId(), new HashMap<>() {{
            put("tags", project.getTags());
        }});

        project.setTags(null);

        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("projects").document(project.getProjectId()).set(project);

        response.addHeader("Location", "/projects/" + project.getProjectId());
        cr.message = "Project with id " + project.getProjectId() + " Created at " + project.getCreatedAtForDb().toDate();
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request, HttpServletResponse response, ProjectMember project, String projectId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();

        DocumentReference documentReference = dbFirestore.collection("projects").document(projectId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {

            project.setIndustry(addIndustry(project.getIndustry()));

            addDataToCollection(projectId, new HashMap<>() {{
                put("tags", project.getTags());
                put("admins", project.getAdmins());
                put("members", project.getMembers());
            }});

            project.setTags(null);
            project.setAdmins(null);
            project.setMembers(null);

            Firestore dbFireStore = FirestoreClient.getFirestore();

            ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("projects").document(projectId).set(project);

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

    public ProjectNonMember addDataToResponseProject(ProjectNonMember project, Map<String, Set<String>> projectInfo, String projectId) {
        project.setMembers(projectInfo.get("members"));
        project.setAdmins(projectInfo.get("admins"));
        project.setTags(projectInfo.get("tags"));
        project.setProjectId(projectId);
        project.setCreatedAt(project.getCreatedAtForDb().toDate().toString());
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

    public void addDataToCollection(String projectId, Map<String, Set<String>> data) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        data.entrySet().forEach(entry -> {

            if (dbFirestore.collection("projects").document(projectId).collection(entry.getKey()) != null) {
                deleteCollection(dbFirestore.collection("projects").document(projectId).collection(entry.getKey()), 10);
            }

            if (entry.getKey().equals("tags")) {

                entry.getValue().forEach(tag -> {
                    if (EnumUtils.isValidEnum(Tag.class, tag)) {
                        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("projects").document(projectId)
                                .collection(entry.getKey()).document(tag).set(new HashMap<String, Object>());
                    }
                });
            } else {

                entry.getValue().forEach(item -> {

                    DocumentReference documentReference = dbFirestore.collection("users").document(item);
                    ApiFuture<DocumentSnapshot> future = documentReference.get();

                    try {
                        DocumentSnapshot document = future.get();
                        if (document.exists()) {     // checking if users exists in the database before adding them as admins/members

                            ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("projects").document(projectId)
                                    .collection(entry.getKey()).document(item).set(new HashMap<String, Object>());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
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
