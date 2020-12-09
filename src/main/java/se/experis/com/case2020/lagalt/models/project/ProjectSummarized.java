package se.experis.com.case2020.lagalt.models.project;

import lombok.Data;
import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.Tag;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Set;

@Component
@Data
public class ProjectSummarized {

    private String projectId;
    private String title;
    private String description;
    private String ownerId;
    private Industry industry;
    private Set<Tag> tags;
    private URL thumbnailImage;
    private Timestamp createdAt;
    private int memberCount;

}
