package se.experis.com.case2020.lagalt.models.user;

import java.net.URL;
import java.util.Map;

import com.google.cloud.firestore.annotation.Exclude;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UserPublicView {

    @Exclude
    private String username;
    private String name;
    private String email;
    private String description;
    private URL imageURL;
    private String portfolio;
    private Map<String, String> tags;
}