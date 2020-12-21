package se.experis.com.case2020.lagalt.models.user;

import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UserProfileView extends UserPublicView {
    private Boolean hidden = false;
    private Set<String> memberOf;
    private Set<String> contributedTo;
    private Set<String> appliedTo;
}
