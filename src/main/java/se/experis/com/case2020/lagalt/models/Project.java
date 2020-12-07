package se.experis.com.case2020.lagalt.models;

import lombok.Data;
import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.ProjectStatus;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Set;

@Component
@Data
public class Project {

    private String projectId;
    private String name;
    private String description;
    private String ownerId;
    private URL thumbnailImage;
    private Timestamp createdAt;

    private Set<String> admins;
    private Set<String> members;
    private Set<String> followers;

    private ProjectStatus status;
    private Industry industry;
    private Set<String> links;

    private Set<String> activeApplications;
    private Set<String> archivedApplications;

}
