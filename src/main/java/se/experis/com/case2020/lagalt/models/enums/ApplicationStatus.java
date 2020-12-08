package se.experis.com.case2020.lagalt.models.enums;

public enum ApplicationStatus {

    PENDING("Pending"), REJECTED("Rejected"), APPROVED("Approved");

    public final String STATUS;

    ApplicationStatus(String status) {
        this.STATUS = status;
    }
}
