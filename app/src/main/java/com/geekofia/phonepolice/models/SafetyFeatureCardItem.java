package com.geekofia.phonepolice.models;

public class SafetyFeatureCardItem {
    private final String title;
    private final String description;
    private final int imageResId;
    private final Class<?> activityClass;

    public SafetyFeatureCardItem(String title, int imageResId, Class<?> activityClass) {
        this.title = title;
        this.description = "Safety feature description";
        this.imageResId = imageResId;
        this.activityClass = activityClass;
    }

    public SafetyFeatureCardItem(String title, String description, int imageResId, Class<?> activityClass) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.activityClass = activityClass;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }
}
