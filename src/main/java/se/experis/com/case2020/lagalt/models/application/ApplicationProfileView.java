package se.experis.com.case2020.lagalt.models.application;

import lombok.Data;
import org.springframework.stereotype.Component;
import se.experis.com.case2020.lagalt.models.enums.ApplicationStatus;

@Component
@Data
public class ApplicationProfileView {

    private String project;
    private String motivation;
    private String feedback;
    private ApplicationStatus status = ApplicationStatus.PENDING;
}
