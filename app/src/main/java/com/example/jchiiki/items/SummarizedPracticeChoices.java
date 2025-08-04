package com.example.jchiiki.items;

public class SummarizedPracticeChoices {
    Integer summarizedPracticeID, questionNumber;
    String summarizedPracticeLanguageLevel, summarizedPracticeTitle;

    public SummarizedPracticeChoices(Integer summarizedPracticeID, Integer questionNumber, String summarizedPracticeLanguageLevel, String summarizedPracticeTitle) {
        this.summarizedPracticeID = summarizedPracticeID;
        this.questionNumber = questionNumber;
        this.summarizedPracticeLanguageLevel = summarizedPracticeLanguageLevel;
        this.summarizedPracticeTitle = summarizedPracticeTitle;
    }

    public Integer getSummarizedPracticeID() {
        return summarizedPracticeID;
    }

    public void setSummarizedPracticeID(Integer summarizedPracticeID) {
        this.summarizedPracticeID = summarizedPracticeID;
    }
    public String getSummarizedPracticeLanguageLevel() {
        return summarizedPracticeLanguageLevel;
    }

    public void setSummarizedPracticeLanguageLevel(String summarizedPracticeLanguageLevel) {
        this.summarizedPracticeLanguageLevel = summarizedPracticeLanguageLevel;
    }

    public String getSummarizedPracticeTitle() {
        return summarizedPracticeTitle;
    }

    public void setSummarizedPracticeTitle(String summarizedPracticeTitle) {
        this.summarizedPracticeTitle = summarizedPracticeTitle;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }
}
