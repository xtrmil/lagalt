package se.experis.com.case2020.lagalt.models;

import com.google.cloud.Timestamp;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MessageBoardPost {
    private String text;
    private String userId;
    private Timestamp createdAt;
    private Timestamp editedAt;
    private boolean deleted = false;
}
