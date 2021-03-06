package se.experis.com.case2020.lagalt.models.application;

import org.springframework.stereotype.Component;
import lombok.Data;
import com.google.cloud.Timestamp;

import se.experis.com.case2020.lagalt.models.user.UserPublicView;

@Component
@Data
public class ApplicationAdminView {

    private String applicationId;
    private String motivation;
    private UserPublicView user;
    private Timestamp createdAt;
}
