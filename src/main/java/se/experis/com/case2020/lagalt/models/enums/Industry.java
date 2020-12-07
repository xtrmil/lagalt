package se.experis.com.case2020.lagalt.models.enums;

public enum Industry {

    GAME_DEVELOPMENT("Game Development",
            new Tag[] { Tag.ARTIST3D, Tag.SCRIPTER, Tag.AI_DEVELOPER, Tag.CONCEPT_ARTIST, Tag.GAMEPLAY_DEVELOPER,
                    Tag.UX_DESIGNER, Tag.TESTER, Tag.SOUND_ENGINEER }),
    WEB_DEVELOPMENT("Web Development", new Tag[] { Tag.UX_DESIGNER, Tag.TESTER, Tag.FRONTEND_DEVELOPER,
            Tag.BACKEND_DEVELOPER, Tag.WEB_ARCHITECT }),
    MUSIC("Music Project", new Tag[] { Tag.DRUMMER, Tag.SINGER })
    // MOVIES;
    ;

    public final String INDUSTRY_NAME;
    public final Tag[] TAGS;

    Industry(String industryName, Tag[] tags) {
        this.INDUSTRY_NAME = industryName;
        this.TAGS = tags;
    }
}