package com.example.jchiiki.items;

import java.util.List;

public class GrammarPoint {
    private String title;
    private String explanation;
    private List<ExampleSentence> examples;
    private boolean expanded = false; // trạng thái expand/collapse

    public GrammarPoint(String title, String explanation, List<ExampleSentence> examples) {
        this.title = title;
        this.explanation = explanation;
        this.examples = examples;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<ExampleSentence> getExamples() { return examples; }
    public void setExamples(List<ExampleSentence> examples) { this.examples = examples; }

    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
} 