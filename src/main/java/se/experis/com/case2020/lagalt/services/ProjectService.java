package se.experis.com.case2020.lagalt.services;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.core.ApiFutures;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    public ResponseEntity<CommonResponse> getProjectsSearch(HttpServletRequest request, String search) {
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        Command cmd = new Command(request);

        try {
            var db = FirestoreClient.getFirestore();

            var documents = db.collection("projects").orderBy("title").startAt(search).endAt(search + "\uf8ff").get()
                    .get().getDocuments();

            cr.data = getFormattedProjects(snapshotsToDocumentsList(documents), null);
            cr.message = "Search result for " + search;
            resp = HttpStatus.OK;
        } catch (Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    private Timestamp checkTimestamp(ObjectNode timestamp) {
        if (timestamp == null) {
            return Timestamp.MIN_VALUE;
        } else {
            return getCreatedAt(timestamp);
        }
    }

    public ResponseEntity<CommonResponse> getProjects(HttpServletRequest request, HttpServletResponse response,
            ObjectNode timestamp) {
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        Command cmd = new Command(request);
        int fetchLimit = 6;
        List<QueryDocumentSnapshot> projects;
        Firestore db = FirestoreClient.getFirestore();
        try {
            if (timestamp == null) {
                projects = db.collection("projects").orderBy("createdAt", Query.Direction.DESCENDING)
                        .limitToLast(fetchLimit).get().get().getDocuments();
            } else {
                projects = db.collection("projects").orderBy("createdAt", Query.Direction.DESCENDING)
                        .startAfter(checkTimestamp(timestamp)).limitToLast(fetchLimit).get().get().getDocuments();
            }

            var formattedProjects = getFormattedProjects(snapshotsToDocumentsList(projects), null);
            cr.data = formattedProjects;
            resp = HttpStatus.OK;

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> getProjectsBasedOnHistory(HttpServletRequest request,
            HttpServletResponse response, String Authorization, ObjectNode timestamp) {
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = null;
        Command cmd = new Command(request);
        Timestamp time;
        Map<String, DocumentReference> filteredProjectsMap = new HashMap<>();
        try {
            if (authService.getUserIdFromToken(Authorization) != null) {

                String userId = authService.getUserIdFromToken(Authorization);
                String favourite = getFavouriteIndustry(userId);
                var db = FirestoreClient.getFirestore();
                List<QueryDocumentSnapshot> filteredProjects;
                if (timestamp != null) {
                    filteredProjects = db.collection("projects").orderBy("createdAt", Query.Direction.DESCENDING)
                            .whereEqualTo("industryKey", favourite).startAfter(getCreatedAt(timestamp)).limit(3).get()
                            .get().getDocuments();
                } else {
                    filteredProjects = db.collection("projects").orderBy("createdAt", Query.Direction.DESCENDING)
                            .whereEqualTo("industryKey", favourite).limit(3).get().get().getDocuments();
                }

                filteredProjects.forEach(document -> {
                    filteredProjectsMap.put(document.getId(), document.getReference());
                });

                int startPosition = 0;
                int desiredMapLength = 5;
                List<QueryDocumentSnapshot> randomProjects;
                while (filteredProjectsMap.size() < desiredMapLength) {
                    randomProjects = db.collection("projects").orderBy("createdAt").startAfter(startPosition)
                            .limit(desiredMapLength).get().get().getDocuments();

                    randomProjects.forEach(p -> {
                        if (filteredProjectsMap.size() < desiredMapLength) {
                            filteredProjectsMap.put(p.getId(), p.getReference());
                        }
                    });
                    startPosition += desiredMapLength;
                }
                List<DocumentReference> filteredProjectsList = new ArrayList<>(filteredProjectsMap.values());

                var formattedProjects = getFormattedProjects(filteredProjectsList, Authorization);
                cr.data = formattedProjects;
                resp = HttpStatus.OK;

            } else {
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    private String getFavouriteIndustry(String userId) throws ExecutionException, InterruptedException {
        var db = FirestoreClient.getFirestore();
        var documents = db.collection("users").document(userId).collection("visited").listDocuments();
        HashMap<String, Integer> projects = new HashMap();
        for (var document : documents) {
            String industryKey = document.get().get().get("industryKey").toString();
            if (!projects.containsKey(industryKey)) {
                projects.put(industryKey, 1);
                System.out.println("first entry");
                System.out.println(industryKey);
            } else {
                int count = projects.get(industryKey);
                projects.put(industryKey, count + 1);
            }
        }
        String favourite = mapToStreamSortedByValue(projects).findFirst().get().getKey();

        return favourite;
    }

    private List<ProjectSummarizedView> getFormattedProjects(Iterable<DocumentReference> projects,
            String Authorization) {
        List<ProjectSummarizedView> allProjects = new ArrayList<>();

        projects.forEach(p -> {
            try {
                var projectDocument = p.get().get();

                ProjectSummarizedView summarizedProject = projectDocument.toObject(ProjectSummarizedView.class);
                var memberCount = p.collection("members").get().get().getDocuments().size();
                summarizedProject.setMemberCount(memberCount);
                allProjects.add(summarizedProject);

                summarizedProject.setOwner(authService.getUsername(summarizedProject.getOwner()));

                String industryKey = projectDocument.getString("industryKey");
                summarizedProject.setIndustry(Map.of(industryKey, Industry.valueOf(industryKey).INDUSTRY_NAME));

                var tags = p.collection("tags").get().get().getDocuments();
                Map<String, String> tagsMap = new HashMap<>();
                tags.forEach(tag -> {
                    tagsMap.put(tag.getId(), Tag.valueOf(tag.getId().toString()).DISPLAY_TAG);
                });

                summarizedProject.setTags(tagsMap);
                summarizedProject.setCreatedAt(projectDocument.getCreateTime());
                if (Authorization != null) {
                    String userId = authService.getUserIdFromToken(Authorization);
                    var db = FirestoreClient.getFirestore();
                    db.collection("users").document(userId).collection("seen").document(p.getId()).set(new HashMap<>());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        return allProjects;
    }

    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, String owner,
            String projectName, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            DocumentReference projectReference = getProjectDocumentReference(owner, projectName);

            if (projectReference != null) {
                CollectionReference links = projectReference.collection("links");
                CollectionReference tags = projectReference.collection("tags");
                DocumentSnapshot projectDocument = projectReference.get().get();

                Map<String, Set<String>> projectInfo = new HashMap<>();
                projectReference.listCollections().forEach(projectCollection -> {

                    projectCollection.listDocuments().forEach(document -> {
                        if (!document.get().equals("chat") && !document.get().equals("links")) {
                            projectInfo.computeIfAbsent(projectCollection.getId(), k -> new HashSet<>())
                                    .add(document.getId());
                        }
                    });
                });

                if (authService.isProjectMember(owner, projectName, Authorization)) {
                    ProjectMemberView project = projectDocument.toObject(ProjectMemberView.class);
                    project.setOwner(authService.getUsername(project.getOwner()));
                    Map<String, String> linksMap = new HashMap<>();

                    links.listDocuments().forEach(link -> {
                        try {
                            DocumentSnapshot linkSnapShot = link.get().get();
                            linksMap.put(linkSnapShot.getString("name"), linkSnapShot.getString("url"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    Map<String, String> tagsMap = new HashMap<>();

                    tags.listDocuments().forEach(tag -> {
                        tagsMap.put(tag.getId(), Tag.valueOf(tag.getId()).DISPLAY_TAG);
                    });
                    project.setTags(tagsMap);
                    Industry industryKey = project.getIndustryKey();
                    project.setIndustry(Map.of(industryKey, industryKey.getLabel()));

                    project = (ProjectMemberView) addDataToResponseProject(project, projectInfo, projectName,
                            projectDocument.getCreateTime());
                    project.setLinks(linksMap);
                    project.setMessageBoards(projectInfo.get("messageBoards"));

                    cr.data = project;

                } else {
                    ProjectNonMemberView project = projectDocument.toObject(ProjectNonMemberView.class);
                    project.setOwner(authService.getUsername(project.getOwner()));
                    project = addDataToResponseProject(project, projectInfo, projectName,
                            projectDocument.getCreateTime());

                    cr.data = project;
                }

                DocumentReference userReference = userService
                        .getUserDocument(authService.getUserIdFromToken(Authorization));
                var visited = userReference.collection("visited").document(projectReference.getId());
                visited.set(new HashMap<>());
                visited.update("industryKey", projectDocument.get("industryKey"));

                resp = HttpStatus.OK;
            } else {
                resp = HttpStatus.NOT_FOUND;
                cr.message = "Project not found";
            }

        } catch (Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
            e.printStackTrace();
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, ProjectNonMemberView project,
            String Authorization) {
        CommonResponse cr = new CommonResponse();
        Command cmd = new Command(request);
        HttpStatus resp = HttpStatus.CREATED;

        try {
            String userId = authService.getUserIdFromToken(Authorization);

            if (userId != null) {
                var db = FirestoreClient.getFirestore();

                project.setOwner(userId);
                String projectId = getProjectId(project);

                var recordsRef = db.collection("projectRecords").document(projectId);
                if (!recordsRef.get().get().exists()) {

                    String industryKey = project.getIndustry().keySet().iterator().next().toString();
                    project.setIndustryKey(Industry.valueOf(industryKey));

                    var docRef = db.collection("projects").document();

                    if (project.getTags() != null) {
                        addCollectionsToProjectDocument(docRef.getId(), Map.of("tags", project.getTags().keySet()));
                    }

                    project.setIndustry(null);
                    docRef.set(project);

                    recordsRef.set(Map.of("pid", docRef.getId()));
                    cr.message = "Project with id " + projectId + " Created at " + Timestamp.now();
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
        } catch (Exception e) {
            cr.message = "Server error";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request, String owner,
            String projectName, ProjectMemberView partialProject, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String projectNameId = getProjectNameId(owner, projectName);

        try {
            var db = FirestoreClient.getFirestore();
            var projectRecord = db.collection("projectRecords").document(projectNameId).get().get();

            if (projectRecord.exists()) {

                String pid = projectRecord.getString("pid");

                if (authService.isProjectAdmin(owner, projectName, Authorization)) {
                    var projectRef = getProjectDocumentReference(pid);
                    var dbProject = projectRef.get().get().toObject(ProjectMemberView.class);

                    // checks for valid new admins and members from the json and turns it into a set
                    // of user ids
                    String editor = authService.getUsernameFromToken(Authorization);
                    partialProject.setOwner(owner);
                    var newMemberIds = getNewMemberIds(partialProject, pid, editor);

                    // updates the memberOf collections in users to match the updated project's
                    // admins and members
                    Set<String> previousUserIds = projectRef.collection("members").get().get().getDocuments().stream()
                            .map(user -> user.getId()).collect(Collectors.toSet());
                    Set<String> previousAdminIds = projectRef.collection("admins").get().get().getDocuments().stream()
                            .map(user -> user.getId()).collect(Collectors.toSet());

                    previousUserIds.addAll(previousAdminIds);
                    previousUserIds.removeAll(newMemberIds);
                    previousUserIds.forEach(userId -> userService.deleteFromUserCollection(userId, "memberOf", pid));

                    var projectCollections = new HashMap<String, Set<String>>();
                    projectCollections.put("members", newMemberIds);

                    if (partialProject.getTags() != null) {
                        projectCollections.put("tags", partialProject.getTags().keySet());
                    }

                    addCollectionsToProjectDocument(pid, projectCollections);

                    if (partialProject.getDescription() != null) {
                        dbProject.setDescription(partialProject.getDescription());
                    }
                    if (partialProject.getIndustry() != null) {
                        String industryKey = partialProject.getIndustry().keySet().iterator().next().toString();
                        dbProject.setIndustryKey(Industry.valueOf(industryKey));
                    }
                    if (partialProject.getStatus() != null) {
                        dbProject.setStatus(partialProject.getStatus());
                    }
                    if (partialProject.getLinks() != null) {
                        dbProject.setLinks(partialProject.getLinks());
                    }
                    if (partialProject.getImages() != null) {
                        dbProject.setImages(partialProject.getImages());
                    }

                    projectRef.set(dbProject);
                    cr.message = "Project data successfully updated for project: " + projectNameId;
                    resp = HttpStatus.OK;

                } else {
                    cr.message = "You are not authorized to edit project with id: " + projectNameId;
                    resp = HttpStatus.UNAUTHORIZED;
                }
            } else {
                cr.message = "Project not found";
                resp = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            cr.message = "Server error";
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    private ProjectNonMemberView addDataToResponseProject(ProjectNonMemberView project,
            Map<String, Set<String>> projectInfo, String projectId, Timestamp createdAt) {
        var admins = projectInfo.get("admins");
        if (admins != null) {
            Set<String> adminNames = new HashSet<>();
            admins.forEach(admin -> {
                adminNames.add(authService.getUsername(admin));
            });
            project.setAdmins(adminNames);
        }

        var members = projectInfo.get("members");
        if (members != null) {
            Set<String> memberNames = new HashSet<>();
            members.forEach(member -> {
                memberNames.add(authService.getUsername(member));
            });
            project.setMembers(memberNames);
        }
        project.setCreatedAt(createdAt);

        return project;
    }

    private void addCollectionsToProjectDocument(String projectId, Map<String, Set<String>> data) {
        DatabaseService databaseService = new DatabaseService();

        data.entrySet().forEach(entry -> {
            try {
                var collectionName = entry.getKey();
                var documentId = entry.getValue();

                var collectionRef = getProjectDocumentReference(projectId).collection(collectionName);

                if (collectionRef != null) {
                    var futures = databaseService.emptyCollection(collectionRef);
                    ApiFutures.allAsList(futures).get(); // block thread until done
                }

                if (collectionName.equals("tags")) {

                    documentId.forEach(tag -> {
                        if (EnumUtils.isValidEnum(Tag.class, tag)) {
                            collectionRef.document(tag).set(new HashMap<String, Object>());
                        } else {
                            System.err.println("IGNORING INVALID TAG: " + tag);
                        }
                    });
                } else {

                    documentId.forEach(item -> {
                        try {
                            collectionRef.document(item).set(new HashMap<String, Object>());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getProjectId(ProjectNonMemberView project) {
        return (authService.getUsername(project.getOwner()) + "-" + project.getTitle().replaceAll(" ", "-"))
                .toLowerCase();
    }

    public String getProjectNameId(String owner, String projectName) {
        return (owner + "-" + projectName).toLowerCase();
    }

    public DocumentReference getProjectDocumentReference(String owner, String projectName) {
        try {
            var db = FirestoreClient.getFirestore();
            var projectRecord = db.collection("projectRecords").document(getProjectNameId(owner, projectName)).get()
                    .get();
            if (projectRecord.exists()) {
                var projectId = projectRecord.getString("pid");
                return getProjectDocumentReference(projectId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DocumentReference getProjectDocumentReference(String projectId) {
        return FirestoreClient.getFirestore().collection("projects").document(projectId);
    }

    public String getProjectId(String owner, String projectName) {
        var docRef = getProjectDocumentReference(owner, projectName);
        return docRef.getId();
    }

    private Set<String> getLowerCaseSet(Set<String> set) {
        if (set == null) {
            return new HashSet<>();
        }
        return set.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    /**
     * deals with: owner cannot be either member or admin a user cannot be both
     * member and admin (becomes only admin if specified as both) editing user
     * cannot remove him/herself as admin
     *
     * @param project
     * @param pid
     * @param editor
     * @return
     */
    private Set<String> getNewMemberIds(ProjectMemberView project, String pid, String editor) {
        var newAdminNames = getLowerCaseSet(project.getAdmins());
        newAdminNames.add(editor);
        newAdminNames.remove(project.getOwner().toLowerCase());

        var newMemberNames = getLowerCaseSet(project.getMembers());
        newMemberNames.removeAll(newAdminNames);
        newMemberNames.remove(project.getOwner().toLowerCase());

        var newAdminIds = mapNamesToIds(newAdminNames, pid);
        var newMemberIds = mapNamesToIds(newMemberNames, pid);

        var set = new HashSet<String>();
        set.addAll(newAdminIds);
        set.addAll(newMemberIds);

        return set;
    }

    private Set<String> mapNamesToIds(Set<String> users, String pid) {
        var set = new HashSet<String>();
        users.forEach(member -> {
            String userId = authService.getUserId(member);
            if (userId != null) {
                userService.addCollectionToUserDocument(userId, "memberOf", pid);
                set.add(userId);
            }
        });
        return set;
    }

    public String getProjectTitle(String projectId) {
        try {
            var db = FirestoreClient.getFirestore();
            return db.collection("projects").document(projectId).get().get().getString("title");
        } catch (Exception e) {
            return null;
        }
    }

    public Set<String> translateIdsToProjectNames(Set<String> collection) {
        Set<String> projectNames = new HashSet<>();
        if (collection != null) {
            collection.forEach(projectId -> {
                projectNames.add(getProjectTitle(projectId));
            });
        }
        return projectNames;
    }

    private Stream<Map.Entry<String, Integer>> mapToStreamSortedByValue(HashMap<String, Integer> map) {

        return map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
    }

    private List<DocumentReference> snapshotsToDocumentsList(List<QueryDocumentSnapshot> snapshots) {

        return snapshots.stream().map(QueryDocumentSnapshot::getReference).collect(Collectors.toList());
    }

    private Timestamp getCreatedAt(ObjectNode timestamp) {

        return Timestamp.ofTimeSecondsAndNanos(timestamp.get("createdAt").get("seconds").asLong(),
                timestamp.get("createdAt").get("nanos").asInt());
    }
}
