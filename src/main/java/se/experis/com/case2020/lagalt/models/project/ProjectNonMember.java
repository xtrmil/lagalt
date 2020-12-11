package se.experis.com.case2020.lagalt.models.project;

import lombok.Data;
import org.springframework.stereotype.Component;
import com.google.cloud.Timestamp;
import java.util.Map;
import java.util.Set;

@Component
@Data
public class ProjectNonMember {

    private String projectId;
    private String title;
    private String description;
    private String ownerId;
    private String industry;
    private Map<String,String> images;
    private String createdAt;
    private Set<String> tags;
    private Set<String> admins;
    private Set<String> members;
    private Timestamp createdAtForDb = Timestamp.now();

}