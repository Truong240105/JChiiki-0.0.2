package com.example.jchiiki.items;

public class ExampleSentence {
    private String sentence;
    private String reading;
    private String meaning;

    public ExampleSentence(String sentence, String reading, String meaning) {
        this.sentence = sentence;
        this.reading = reading;
        this.meaning = meaning;
    }

    public String getSentence() { return sentence; }
    public void setSentence(String sentence) { this.sentence = sentence; }

    public String getReading() { return reading; }
    public void setReading(String reading) { this.reading = reading; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
} 