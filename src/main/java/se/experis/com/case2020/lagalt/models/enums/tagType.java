package se.experis.com.case2020.lagalt.models.enums;

public enum tagType {
    MUSICIAN("Musician", "Music Project"),
    FILMMAKER("Filmmaker", "Film Project"),
    GAMEDEVELOPER("Game Developer", "Game Application"),
    WEBDEVELOPER("Web Developer", "Web Application");

    public final String userTag,projectTag;

    tagType(String userTag,String projectTag) {
        this.userTag = userTag;
        this.projectTag = projectTag;
    }
}


