package se.experis.com.case2020.lagalt.models.project;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;

@Component
@Data
public class ProjectMemberView extends ProjectNonMemberView {

    private Map<String,String> links;
    private Set<String> messageBoards;
}