package se.experis.com.case2020.lagalt.models;

import com.google.cloud.Timestamp;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ChatMessage {
    private String user;
    private String text;
    private Timestamp timestamp = Timestamp.now();
}
