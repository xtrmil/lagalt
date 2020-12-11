package se.experis.com.case2020.lagalt.models.enums;

public enum Tag {
    // Music
    SINGER("Singer","Music"), GUITARRIST("Guitarrist","Music"), BASSIST("Bassist","Music"), DRUMMER("Drummer","Music"),

    // game
    ARTIST3D("3D Artist", "Game"), AI_DEVELOPER("AI developer", "Game"), CONCEPT_ARTIST("Concept artist", "Game"),
    GAMEPLAY_DEVELOPER("Gameplay developer", "Game"), LEVEL_DESIGNER("Level designer", "Game"), SOUND_ENGINEER("Sound engineer", "Game"),
    GAME_TESTER("Game tester", "Game"), GAME_UX_DESIGNER("UX designer", "Game"),

    // web
    FULLSTACK_DEVELOPER("Fullstack developer", "Web"), FRONTEND_DEVELOPER("Frontend developer", "Web"),
    BACKEND_DEVELOPER("Backend developer", "Web"), WEB_ARCHITECT("Web architect", "Web"), WEB_UX_DESIGNER("Web UX Designer", "Web"),
    UNIT_TESTER("Tester", "Web"), JAVA("Java", "Web"), JAVA_SCRIPT("JavaScript", "Web"), C_SHARP("C#", "Web"),
    CPP("C++", "Web"), DOT_NET(".NET", "Web");

    public final String DISPLAY_TAG,INDUSTRY;


    Tag(String displayTag, String industry) {

        this.DISPLAY_TAG = displayTag;
        this.INDUSTRY = industry;
    }
}