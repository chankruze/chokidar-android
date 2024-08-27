package com.geekofia.chokidar.data.models;

public class PreferenceKeyItem {
    private final String key;
    private final String featureName;

    public PreferenceKeyItem(String key, String featureName) {
        this.key = key;
        this.featureName = featureName;
    }

    public String getKey() {
        return key;
    }

    public String getFeatureName() {
        return featureName;
    }
}
