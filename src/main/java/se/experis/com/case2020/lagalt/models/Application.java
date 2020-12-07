package se.experis.com.case2020.lagalt.models;

import lombok.Data;
import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.enums.ApplicationStatus;

@Component
@Data
public class Application {

    private String applicationId;
    private String userId;
    private String projectId;
    private String motivation;
    private String feedback;

    private Enum<ApplicationStatus> status;
}
