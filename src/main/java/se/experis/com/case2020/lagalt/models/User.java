package se.experis.com.case2020.lagalt.models;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import se.experis.com.case2020.lagalt.models.enums.tagType;

@Component
@Data
public class User {

    private String userId;
    private String name;
    private String email;
    private String description;
    private URL imageURL;
    private Boolean hidden = false;

    private List <tagType> skills = new ArrayList<>();
    private List <String> memberOf;
    private List <String> following;
    private List <String> seen;
    private List <String> visited;
    private List <String> appliedTo;
    private List <String> contributedTo;

    public User() { }
}