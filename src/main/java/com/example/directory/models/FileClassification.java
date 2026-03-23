package com.example.directory.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the classification level assigned to a file.
 */
public enum FileClassification {
    PUBLIC("Public"),
    SECRET("Secret"),
    TOP_SECRET("Top secret");

    private String name;
    private static final Map<String, FileClassification> map = new HashMap<>();

    static {
        for (FileClassification value : values()) {
            map.put(value.name.toLowerCase(), value);
        }
    }

    FileClassification(String classification) {
        name = classification;
    }

    public String getName() {
        return name;
    }

    public static FileClassification fromName(String classification) {
        return map.get(classification);
    }

}
