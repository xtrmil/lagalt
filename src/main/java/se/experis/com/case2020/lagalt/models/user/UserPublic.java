package se.experis.com.case2020.lagalt.models.user;

import org.springframework.stereotype.Component;
import lombok.Data;
import java.net.URL;
import java.util.Set;

@Component
@Data
public class UserPublic {

    private String username;
    private String name;
    private String email;
    private String description;
    private URL imageURL;
    private String portfolio;
    private Set<String> skills;
}