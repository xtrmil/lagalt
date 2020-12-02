package se.experis.com.case2020.lagalt.models;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import se.experis.com.case2020.lagalt.models.enums.SkillType;

@Component
@Data
public class User {

    private String userId;
    private String name;
    private String email;
    private List <SkillType> skills = new ArrayList<>();
    private List<String> memberOf = new ArrayList<>();
    private List<String> following = new ArrayList<>();
    private Boolean hidden = false;

    private List<String> seen = new ArrayList<>();
    private List<String> visited= new ArrayList<>();
    private List<String> appliedTo = new ArrayList<>();
    private List<String> contributedTo = new ArrayList<>();

    public User() { }
}