package se.experis.com.case2020.lagalt.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteResult;
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

@Service
public class ProjectService {

    public String testQuery() {
        try {
            String search = "project";
            Firestore db = FirestoreClient.getFirestore();
            var smt = db.collection("projects").orderBy("title").startAt(search).endAt(search + "\uf8ff");

            System.out.println(smt.get().get().getDocuments().size());

            var it = smt.get().get().getDocuments().iterator();
            var list = new ArrayList<>();
            while(it.hasNext()) {
                list.add(it.next().get("title"));
            }
            
            return list.toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    MockAuthService authService = new MockAuthService();
    public ResponseEntity<CommonResponse> getProjectSearch(HttpServletRequest request, HttpServletResponse response, String search) {
        try {
            Command cmd = new Command(request);
            CommonResponse cr = new CommonResponse();
            HttpStatus resp;

            Firestore dbFirestore = FirestoreClient.getFirestore();
            // if(search != null)
            
            Query query = dbFirestore.collection("projects").orderBy("title").startAt(search).endAt(search + "\uf8ff");
            
            DocumentReference projectReference = dbFirestore.collection("projects").document(search);
            DocumentSnapshot projectSnapshot = projectReference.get().get();

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

                project.setProjectId(search);
                project.setCreatedAt(createdAtProject.getCreatedAtForDb().toDate().toString());

                cr.message = "Search result for project with Id: " + search;
                resp = HttpStatus.OK;
                response.addHeader("Location", "/projects/" + search);
            } else {
                cr.message = "No project found with id " + search;
                resp = HttpStatus.NOT_FOUND;
            }
        

            cr.data = project;
            cmd.setResult(resp);
            return new ResponseEntity<>(cr, resp);
        
        } catch(Exception e) {
            // throws ExecutionException, InterruptedException
            return null;
        }
    
    
    }

    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, HttpServletResponse response, String projectName, String Authorization) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        DocumentReference projectReference = getProjectFromName(projectName);
        DocumentReference userReference = projectReference.collection("members").document(Authorization);
        CollectionReference links = projectReference.collection("links");

        DocumentSnapshot projectDocument = projectReference.get().get();
        DocumentSnapshot userDocument = userReference.get().get();

        if (projectDocument.exists()) {

            Map<String, Set<String>> projectInfo = new HashMap<>();
            Iterable<CollectionReference> projectCollections = projectReference.listCollections();
            projectCollections.forEach(collection -> {

                Iterable<DocumentReference> projectData = collection.listDocuments();
                projectData.forEach(id -> {
                    if (!id.get().equals("chat") && !id.get().equals("links")) {
                        projectInfo.computeIfAbsent(collection.getId(), k -> new HashSet<>()).add(id.getId());
                    }
                });
            });

            if (userDocument.exists() || projectDocument.toObject(ProjectMember.class).getOwner().equals(userDocument.getId())) {
                ProjectMember project = projectDocument.toObject(ProjectMember.class);
                project.setOwner(authService.getUsername(project.getOwner()));
                Map<String, String> linksMap = new HashMap<>();

                Iterable<DocumentReference> projectlinks = links.listDocuments();
                projectlinks.forEach(link -> {
                    ApiFuture<DocumentSnapshot> linkFuture = link.get();
                    try {
                        DocumentSnapshot linkSnapShot = linkFuture.get();
                        linksMap.put(linkSnapShot.getData().get("name").toString(), linkSnapShot.getData().get("url").toString());
                        System.out.println(linkSnapShot.getData().get("name").toString());
                        System.out.println(linkSnapShot.getData().get("url").toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });

                project = (ProjectMember) addDataToResponseProject(project, projectInfo, projectName);
                project.setLinks(linksMap);
                project.setMessageBoards(projectInfo.get("messageBoards"));
                cr.data = project;
            } else {

                ProjectNonMember project = projectDocument.toObject(ProjectNonMember.class);
                project.setOwner(authService.getUsername(project.getOwner()));
                project = addDataToResponseProject(project, projectInfo, projectName);
                cr.data = project;
            }
            resp = HttpStatus.OK;
            cr.message = "Project details for " + projectName;

        } else {
            resp = HttpStatus.NOT_FOUND;
            cr.message = "No project with id: " + projectName + " found";
        }

        response.addHeader("Location", "/projects/" + projectName);
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, ProjectNonMember project, String Authorization) {
        CommonResponse cr = new CommonResponse();
        Command cmd = new Command(request);
        HttpStatus resp = HttpStatus.CREATED;
        
        try {
            String userId = authService.getUserId(Authorization);
            
            if(userId != null) {
                project.setIndustry(addIndustry(project.getIndustry()));
                
                Firestore dbFireStore = FirestoreClient.getFirestore();
                var docRef = dbFireStore.collection("projects").document();
                project.setOwner(userId);
                addToProjectDb(docRef.getId(), new HashMap<>() {{
                    put("tags", project.getTags());
                }});
                project.setTags(null);
                docRef.set(project);
                
                String projectId = getProjectId(project);
                
                var projectRecord = new HashMap<String, String>() {{ put("pid", docRef.getId()); }};
                var recordsRef = dbFireStore.collection("projectRecords").document(projectId);

                if(!recordsRef.get().get().exists()) {
                    recordsRef.set(projectRecord);
                    cr.message = "Project with id " + projectId + " Created at " + project.getCreatedAtForDb().toDate();
                    resp = HttpStatus.CREATED;
                    cmd.setResult(resp);
                    return new ResponseEntity<>(cr, resp);
                } else {
                    resp = HttpStatus.CONFLICT;
                    cmd.setResult(resp);
                    cr.message = "The project name is not available";
                }
            } else {
                cr.message = "Cannot create project. You are not logged in";
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch(Exception e) {
            cr.message = "Server error";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(cr, resp);
    }
        
    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request, ProjectMember project, String Authorization) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String projectId = getProjectId(project);

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("projects").document(projectId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if(document.exists()) {
            if (authService.isProjectAdmin(projectId,Authorization)) {

                project.setIndustry(addIndustry(project.getIndustry()));

                addToProjectDb(projectId, new HashMap<>() {{
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
                cr.message = "Project data successfully updated for project: " + getProjectId(project);
                resp = HttpStatus.OK;

            } else {
                resp = HttpStatus.UNAUTHORIZED;
                cr.message = "You are not authorized to edit project with id: " + projectId;
            }
        }else{
            resp = HttpStatus.NOT_FOUND;
            cr.message = "Project not found";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ProjectNonMember addDataToResponseProject(ProjectNonMember project, Map<String, Set<String>> projectInfo, String projectId) {
        project.setMembers(projectInfo.get("members"));
        project.setAdmins(projectInfo.get("admins"));
        project.setTags(projectInfo.get("tags"));
        project.setCreatedAt(project.getCreatedAtForDb().toDate().toString());
        return project;
    }

    public void addToProjectDb(String projectId, Map<String, Set<String>> data) {
        DatabaseService databaseService = new DatabaseService();
        Firestore dbFirestore = FirestoreClient.getFirestore();
        data.entrySet().forEach(entry -> {

            if (dbFirestore.collection("projects").document(projectId).collection(entry.getKey()) != null) {
                databaseService.emptyCollection(dbFirestore.collection("projects").document(projectId).collection(entry.getKey()), 10);
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
                    try {
                        if (dbFirestore.collection("userRecords").document(item).get().get().exists()) {
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


    private String getProjectId(ProjectNonMember project) {
        String owner = authService.getUsername(project.getOwner());
        return owner + "-" + project.getTitle().replaceAll(" ", "-");
    }

    private DocumentReference getProjectFromName(String projectName) {
        try {
            var db = FirestoreClient.getFirestore();

            var projectRecord = db.collection("projectRecords").document(projectName).get().get();
            if(projectRecord.exists()) {
                var projectId = (String) projectRecord.get("pid");
                return db.collection("projects").document(projectId);
            }
        } catch(Exception e) {
        }
        return null;
    }

}


