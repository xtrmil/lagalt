package se.experis.com.case2020.lagalt.models.project;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.springframework.stereotype.Component;
import com.google.cloud.Timestamp;

import lombok.Data;
import se.experis.com.case2020.lagalt.models.enums.EnumItem;

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

}
