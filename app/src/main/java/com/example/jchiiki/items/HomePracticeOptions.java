package com.example.jchiiki.items;

public class HomePracticeOptions {
    private int practiceOptionIcon;
    private String title;

    public HomePracticeOptions(int practiceOptionIcon, String title) {
        this.practiceOptionIcon = practiceOptionIcon;
        this.title = title;
    }

    public int getPracticeOptionIcon() {
        return practiceOptionIcon;
    }

    public void setPracticeOptionIcon(int practiceOptionIcon) {
        this.practiceOptionIcon = practiceOptionIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
