package se.experis.com.case2020.lagalt.models.messageboard;

import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.Timestamp;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MessageBoardPost {
    private String text;
    private String user;
    private boolean deleted = false;

    @Exclude
    private Timestamp createdAt;
}