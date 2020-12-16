package se.experis.com.case2020.lagalt.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firestore.v1.Document;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.models.project.ProjectMemberView;
import se.experis.com.case2020.lagalt.models.project.ProjectNonMemberView;
import se.experis.com.case2020.lagalt.models.project.ProjectSummarizedView;
import se.experis.com.case2020.lagalt.utils.Command;

@Service
public class ProjectService {

    MockAuthService authService = new MockAuthService();

    public String testQuery() {
        try {
            String search = "project";
            Firestore db = FirestoreClient.getFirestore();
            var smt = db.collection("projects").orderBy("title").startAt(search).endAt(search + "\uf8ff");

            System.out.println(smt.get().get().getDocuments().size());

            var it = smt.get().get().getDocuments().iterator();
            var list = new ArrayList<>();
            while(it.hasNext()) {
                it.next().getReference();
                list.add(it.next().get("title"));
            }
            
            return list.toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ResponseEntity<CommonResponse> getProjectsSearch(HttpServletRequest request, String search) {
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        Command cmd = new Command(request);
        
        try {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            var documents = dbFirestore.collection("projects").orderBy("title").startAt(search).endAt(search + "\uf8ff").get().get().getDocuments();
            List<DocumentReference> projects = new ArrayList<>();
            for(var document : documents) {
                projects.add(document.getReference());
            }

            
            cr.data = getFormattedProjects(projects);
            cr.message = "Search result for " + search;
            resp = HttpStatus.OK;
        } catch(Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
    
    public ResponseEntity<CommonResponse> getProjects(HttpServletRequest request, HttpServletResponse response) {
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        Command cmd = new Command(request);

        try {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            var projects = dbFirestore.collection("projects").listDocuments();
            var formattedProjects = getFormattedProjects(projects);
            cr.data = formattedProjects;
            resp = HttpStatus.OK;
        }  catch(Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    private List<ProjectSummarizedView> getFormattedProjects(Iterable<DocumentReference> projects) {
        List<ProjectSummarizedView> allProjects = new ArrayList<>();
                   
        projects.forEach(p -> {
            try {
                var project =  p.get().get();
                
                ProjectSummarizedView jsonProject = project.toObject(ProjectSummarizedView.class);
                var memberCount = p.collection("members").get().get().getDocuments().size();
                jsonProject.setMemberCount(memberCount);
                allProjects.add(jsonProject);
                
                var tags = p.collection("tags").get().get().getDocuments();
                Set<String> tagSet = new HashSet<>();
                tags.forEach(tag -> {
                    tagSet.add(tag.getId());
                });
                jsonProject.setTags(tagSet);
                
                Timestamp createdAtForDb = (Timestamp) project.get("createAtForDb");
                jsonProject.setCreatedAt(createdAtForDb.toDate().toString());

            } catch(Exception e) {
                e.printStackTrace();
            }
            
        });

        return allProjects;
    }

    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, String owner, String projectName, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            DocumentReference projectReference = getProjectFromName(owner, projectName);
            CollectionReference links = projectReference.collection("links");
            DocumentSnapshot projectDocument = projectReference.get().get();

            if (projectDocument.exists()) {

                Map<String, Set<String>> projectInfo = new HashMap<>();
                projectReference.listCollections().forEach(projectCollection -> {

                    projectCollection.listDocuments().forEach(document -> {
                        if (!document.get().equals("chat") && !document.get().equals("links")) {
                            projectInfo.computeIfAbsent(projectCollection.getId(), k -> new HashSet<>()).add(document.getId());
                        }
                    });
                });
                
                if (authService.isProjectMember(owner, projectName, Authorization)) {
                    ProjectMemberView project = projectDocument.toObject(ProjectMemberView.class);
                    project.setOwner(getProjectOwner(project));
                    Map<String, String> linksMap = new HashMap<>();

                    links.listDocuments().forEach(link -> {
                        try {
                            DocumentSnapshot linkSnapShot = link.get().get();
                            linksMap.put(linkSnapShot.get("name").toString(), linkSnapShot.get("url").toString());
                            System.out.println(linkSnapShot.get("name").toString());
                            System.out.println(linkSnapShot.get("url").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    project = (ProjectMemberView) addDataToResponseProject(project, projectInfo, projectName);
                    project.setLinks(linksMap);
                    project.setMessageBoards(projectInfo.get("messageBoards"));
                    cr.data = project;
                } else {

                    ProjectNonMemberView project = projectDocument.toObject(ProjectNonMemberView.class);
                    project.setOwner(getProjectOwner(project));
                    project = addDataToResponseProject(project, projectInfo, projectName);
                    cr.data = project;
                }
                resp = HttpStatus.OK;

            } else {
                resp = HttpStatus.NOT_FOUND;
                cr.message = "Project not found";
            }

        } catch(Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, ProjectNonMemberView project, String Authorization) {
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
                    return new ResponseEntity<>(cr, resp);
                } else {
                    resp = HttpStatus.CONFLICT;
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
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
        
    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request, ProjectMemberView project, String Authorization) throws InterruptedException, ExecutionException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String projectId = getProjectId(project);

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("projects").document(projectId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if(document.exists()) {
            if (authService.isProjectAdmin("bogus", projectId, Authorization)) {

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

    public ProjectNonMemberView addDataToResponseProject(ProjectNonMemberView project, Map<String, Set<String>> projectInfo, String projectId) {
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
                        dbFirestore.collection("projects").document(projectId)
                            .collection(entry.getKey()).document(tag).set(new HashMap<String, Object>());
                    }
                });
            } else {

                entry.getValue().forEach(item -> {
                    try {
                        if (dbFirestore.collection("userRecords").document(item).get().get().exists()) {
                            dbFirestore.collection("projects").document(projectId)
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

    public String getProjectId(ProjectNonMemberView project) {
        return getProjectOwner(project) + "-" + project.getTitle().replaceAll(" ", "-");
    }
 
    public String getProjectId(String owner, String projectName) {
        return owner + "-" + projectName;
    }

    private DocumentReference getProjectFromName(String owner, String projectName) {
        try {
            var db = FirestoreClient.getFirestore();

            var projectRecord = db.collection("projectRecords").document(owner + "-" + projectName).get().get();
            if(projectRecord.exists()) {
                var projectId = (String) projectRecord.get("pid");
                return db.collection("projects").document(projectId);
            }
        } catch(Exception e) {
            System.err.println("getProjectFromName:" + e.getMessage());
        }
        return null;
    }


    private String getProjectOwner(ProjectNonMemberView project) {
        return authService.getUsername(project.getOwner());
    }
}


