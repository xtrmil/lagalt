package se.experis.com.case2020.lagalt.models.user;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UserPublicView {
    private String user;
    private String name;
    private String email;
    private String description;
    private URL imageURL;
    private String portfolio;
    private Set<String> skillKeys;
    private Map<String,String> skills;
}