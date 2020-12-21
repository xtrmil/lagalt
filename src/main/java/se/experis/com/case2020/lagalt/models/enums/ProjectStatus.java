package se.experis.com.case2020.lagalt.models.enums;

public enum ProjectStatus implements EnumItem{

    FOUNDING("Founding"), INPROGRESS("In Progress"), STALLED("Stalled"), COMPLETED("Completed");
    public final String STATUS;
    ProjectStatus(String status) {
        this.STATUS = status;
    }

    @Override
    public String getLabel() {
        return STATUS;
    }


}
