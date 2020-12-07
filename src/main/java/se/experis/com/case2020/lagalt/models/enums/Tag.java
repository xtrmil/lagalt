package se.experis.com.case2020.lagalt.models.enums;

public enum Tag {
    // Music
    SINGER("Singer"), GUITARRIST("Guitarrist"), BASSIST("Bassist"), DRUMMER("Drummer"),

    // game
    ARTIST3D("3D Artist"), AI_DEVELOPER("AI developer"), CONCEPT_ARTIST("Concept artist"),
    GAMEPLAY_DEVELOPER("Gameplay developer"), LEVEL_DESIGNER("Level designer"), SOUND_ENGINEER("Sound engineer"),

    // web
    FULLSTACK_DEVELOPER("Fullstack developer"), FRONTEND_DEVELOPER("Frontend developer"),
    BACKEND_DEVELOPER("Backend developer"), WEB_ARCHITECT("Web architect"),

    // Game, Web
    UX_DESIGNER("UX designer"), SCRIPTER("Scripter"), TESTER("Tester");

    public final String DISPLAY_TAG;

    Tag(String displayTag) {
        this.DISPLAY_TAG = displayTag;
    }
}