package se.experis.com.case2020.lagalt.models.user;

import org.springframework.stereotype.Component;
import lombok.Data;
import se.experis.com.case2020.lagalt.models.enums.Tag;

import java.net.URL;
import java.util.Set;

@Component
@Data
public class UserProfile {

    private String userId;
    private String name;
    private String email;
    private String description;
    private URL imageURL;
    private String portfolio;
    private Boolean hidden = false;
    private Set<String> memberOf;
    private Set<String> following;
    private Set<String> contributedTo;
    private Set<String> appliedTo;
    private Set<String> skills;

}