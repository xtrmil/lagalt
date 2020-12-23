package se.experis.com.case2020.lagalt.models.messageboard;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MessageBoardThread {
    private String title;
    private int nrOfMessages;
    private String createdBy;
    private String link;
    // private String latestPostBy;
    // private String latestPostTimestamp;
    
}
