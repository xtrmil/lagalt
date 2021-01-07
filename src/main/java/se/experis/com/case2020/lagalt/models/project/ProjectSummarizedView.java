package se.experis.com.case2020.lagalt.models.project;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.google.cloud.firestore.annotation.Exclude;
import org.springframework.stereotype.Component;
import com.google.cloud.Timestamp;

import lombok.Data;
import se.experis.com.case2020.lagalt.models.enums.EnumItem;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.ProjectStatus;

@Component
@Data
public class ProjectSummarizedView {

    @JsonProperty(access = Access.WRITE_ONLY)
    private String projectId;
    private String title;
    private String description;
    private String owner;
    private Map<String, String> industry;
    private Map<String, String> tags;
    private Map<String,String> images;
    private Timestamp createdAt;
    private int memberCount;

    @JsonProperty(access = Access.WRITE_ONLY)
    private Set<String> admins;

    @JsonProperty(access = Access.WRITE_ONLY)
    private Set<String> members;

    @JsonProperty(access = Access.WRITE_ONLY)
    private Industry industryKey;
    @JsonProperty(access = Access.WRITE_ONLY)
    private ProjectStatus status = ProjectStatus.FOUNDING;
    @JsonProperty(access = Access.WRITE_ONLY)
    private Map<String,String> links;
    @JsonProperty(access = Access.WRITE_ONLY)
    private Set<String> messageBoards;

}
