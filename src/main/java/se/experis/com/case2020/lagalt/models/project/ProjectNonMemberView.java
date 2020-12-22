package se.experis.com.case2020.lagalt.models.project;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.Exclude;

import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.enums.ProjectStatus;
import se.experis.com.case2020.lagalt.models.enums.Industry;

import lombok.Data;

@Component
@Data
public class ProjectNonMemberView {

    private String title;
    private String description;
    private String owner;
    private String createdAt;
    private ProjectStatus status = ProjectStatus.FOUNDING;

    @JsonProperty(access = Access.WRITE_ONLY)
    private Industry industryKey;

    @Exclude
    private Map<Industry, String> industry;

    @Exclude
    private Map<String, String> images;

    @Exclude
    private Map<String, String> tags;

    @Exclude
    private Set<String> admins;

    @Exclude
    private Set<String> members;

    @JsonIgnore
    private Timestamp createdAtForDb = Timestamp.now();
}
