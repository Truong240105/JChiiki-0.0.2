package com.example.jchiiki.items;

public class QuestionItems {
    String question, option1, option2, option3, option4, simplifiedContent, pronunciation;
    Integer correctAnswer, selectedAnswer;

    public QuestionItems(String question, String simplifiedContent, String pronunciation){
        this.question = question;
        this.simplifiedContent = simplifiedContent;
        this.pronunciation = pronunciation;
    }

    public QuestionItems(String question, String option1, String option2, String option3, String option4, Integer correctAnswer, Integer selectedAnswer) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
        this.selectedAnswer = selectedAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Integer getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(Integer selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getSimplifiedContent() {
        return simplifiedContent;
    }

    public void setSimplifiedContent(String simplifiedContent) {
        this.simplifiedContent = simplifiedContent;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
}
