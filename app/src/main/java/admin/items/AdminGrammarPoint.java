package admin.items;

public class AdminGrammarPoint {
    private String grammarPointID;
    private String grammarPoint;
    private String explanation;
    private String example;
    private String translation;
    private String lessonTitle;

    public AdminGrammarPoint(String grammarPointID, String grammarPoint, String explanation, String example, String translation, String lessonTitle) {
        this.grammarPointID = grammarPointID;
        this.grammarPoint = grammarPoint;
        this.explanation = explanation;
        this.example = example;
        this.translation = translation;
        this.lessonTitle = lessonTitle;
    }

    public String getGrammarPointID() {
        return grammarPointID;
    }

    public void setGrammarPointID(String grammarPointID) {
        this.grammarPointID = grammarPointID;
    }

    public String getGrammarPoint() {
        return grammarPoint;
    }

    public void setGrammarPoint(String grammarPoint) {
        this.grammarPoint = grammarPoint;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }
} 