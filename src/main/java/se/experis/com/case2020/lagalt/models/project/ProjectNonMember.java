package se.experis.com.case2020.lagalt.models.project;

import java.util.Map;
import java.util.Set;

import com.google.cloud.Timestamp;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ProjectNonMember {

    private String title;
    private String description;
    private String owner;
    private String industry;
    private Map<String,String> images;
    private String createdAt;
    private Set<String> tags;
    private Set<String> admins;
    private Set<String> members;
    private Timestamp createdAtForDb = Timestamp.now();

}
