package com.mpascal.programmaker.core;

public class Routine {
    private String title;
    private String text;

    public Routine(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String text) {
        title =text;
    }
}
