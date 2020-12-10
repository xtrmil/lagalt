package se.experis.com.case2020.lagalt.models.project;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.net.URL;
import com.google.cloud.Timestamp;
import java.util.Set;

@Component
@Data
public class ProjectNonMember {

    private String projectId;
    private String title;
    private String description;
    private String ownerId;
    private String industry;
    private String thumbnailImageUrl;
    private Timestamp createdAt;
    private Set<String> tags;
    private Set<String> admins;
    private Set<String> members;

}
