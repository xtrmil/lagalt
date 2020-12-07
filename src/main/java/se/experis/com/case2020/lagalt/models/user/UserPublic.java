package se.experis.com.case2020.lagalt.models.user;

import org.springframework.stereotype.Component;
import lombok.Data;
import java.net.URL;
import java.util.Set;
import se.experis.com.case2020.lagalt.models.enums.Tag;


@Component
@Data
public class UserPublic {

    private String name;
    private String email;
    private String description;
    private URL imageURL;
    private String portfolio;
    private Set<Tag> skills;
}