package se.experis.com.case2020.lagalt.models.user;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UserPrivate extends UserPublic {

    @JsonIgnore
    private String userId;
    private Boolean hidden = false;
    private Set<String> memberOf;
    private Set<String> following;
    private Set<String> contributedTo;
    private Set<String> appliedTo;

}