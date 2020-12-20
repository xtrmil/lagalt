package se.experis.com.case2020.lagalt.models.exceptions;

public class InvalidProjectException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidProjectException(String msg) {
        super(msg);
    }
}
