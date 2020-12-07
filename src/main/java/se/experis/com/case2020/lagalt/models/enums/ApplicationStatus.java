package se.experis.com.case2020.lagalt.models.enums;

public enum ApplicationStatus {

    PENDING("Pending"), REJECTED("Rejected"), APPROVED("Approved");

    public final String LABEL;

    ApplicationStatus(String label) {
        this.LABEL = label;
    }
}
