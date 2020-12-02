package se.experis.com.case2020.lagalt.models;


import lombok.Data;
import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.enums.FieldType;
import se.experis.com.case2020.lagalt.models.enums.ProjectStatus;
import se.experis.com.case2020.lagalt.models.enums.SkillType;

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
    private Set<String> admins;
    private Set<String> members;
    private Set<String> followers;

    private Enum<ProjectStatus> status;
    private Enum<FieldType> field;
    private Enum<SkillType> tags;
    private Set<String> links;
    private URL thumbnailImage;
    private Timestamp createdAt;

    private Set<String> activeApplications;
    private Set<String> archivedApplications;



}
