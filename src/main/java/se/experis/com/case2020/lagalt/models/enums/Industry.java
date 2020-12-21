package se.experis.com.case2020.lagalt.models.enums;

public enum Industry implements EnumItem{

    GAME_DEVELOPMENT("Game Development",
            new Tag[] { Tag.ARTIST3D, Tag.AI_DEVELOPER, Tag.CONCEPT_ARTIST, Tag.GAMEPLAY_DEVELOPER,
                    Tag.GAME_UX_DESIGNER, Tag.GAME_TESTER, Tag.SOUND_ENGINEER }),
    WEB_DEVELOPMENT("Web Development", new Tag[] { Tag.WEB_UX_DESIGNER, Tag.UNIT_TESTER, Tag.FRONTEND_DEVELOPER,
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


    @Override
    public String getLabel() {
        return INDUSTRY_NAME;
    }

}