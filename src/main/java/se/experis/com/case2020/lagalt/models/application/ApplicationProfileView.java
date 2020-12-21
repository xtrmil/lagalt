package se.experis.com.case2020.lagalt.models.application;

import lombok.Data;
import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.enums.ApplicationStatus;

@Component
@Data
public class ApplicationProfileView {

    private String userId;
    private String projectId;
    private String motivation;
    private String feedback;
    private Enum<ApplicationStatus> status = ApplicationStatus.PENDING;
}
