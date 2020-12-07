package se.experis.com.case2020.lagalt.models.enums;

public enum ProjectStatus {

    FOUNDING("Founding"), INPROGRESS("In Progress"), STALLED("Stalled"), COMPLETED("Completed");

    public final String LABEL;

    ProjectStatus(String label) {
        this.LABEL = label;
    }
}
