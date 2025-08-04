package admin.items;

public class AdminLessonChoices {
    String lessonID;
    String lessonTitle;
    String lessonName;
    String lessonImage;

    public AdminLessonChoices(String lessonID, String lessonTitle, String lessonName, String lessonImage) {
        this.lessonID = lessonID;
        this.lessonTitle = lessonTitle;
        this.lessonName = lessonName;
        this.lessonImage = lessonImage;
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
    public String getLessonImage() {
        return lessonImage;
    }

    public void setLessonImage(String lessonImage) {
        this.lessonImage = lessonImage;
    }
}
