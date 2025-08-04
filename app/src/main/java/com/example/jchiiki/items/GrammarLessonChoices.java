package com.example.jchiiki.items;

public class GrammarLessonChoices {
    String lessonID;
    String lessonTitle;
    String lessonName;

    public GrammarLessonChoices(String lessonID, String lessonTitle, String lessonName) {
        this.lessonID = lessonID;
        this.lessonTitle = lessonTitle;
        this.lessonName = lessonName;
    }

    public String getLessonID() {
        return lessonID;
    }

    public void setLessonID(String lessonID) {
        this.lessonID = lessonID;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }


}
