package se.experis.com.case2020.lagalt.models.application;

import lombok.Data;
import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.user.UserPublic;

@Component
@Data
public class ApplicationAdminView {

    private String applicationId;
    private String motivation;
    private UserPublic userPublic;
}
