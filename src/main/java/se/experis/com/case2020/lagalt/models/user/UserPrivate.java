package se.experis.com.case2020.lagalt.models.user;

import org.springframework.stereotype.Component;
import lombok.Data;
import java.util.Set;

@Component
@Data
public class UserPrivate extends UserPublic {

    private String userId;
    private Boolean hidden = false;
    private Set<String> memberOf;
    private Set<String> following;
    private Set<String> contributedTo;
    private Set<String> appliedTo;

}