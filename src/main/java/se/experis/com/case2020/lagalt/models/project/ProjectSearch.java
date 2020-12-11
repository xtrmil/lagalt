package se.experis.com.case2020.lagalt.models.project;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;

@Component
@Data
public class ProjectSearch {

    private String projectId;
    private String title;
    private String description;
    private String ownerId;
    private String industry;
    private Set<String> tags;
    private Map<String,String> images;
    private String createdAt;
    private int memberCount;
}
