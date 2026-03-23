package com.example.directory.models;

import java.util.HashMap;
import java.util.Map;

/** Represents the supported file system entry types. */
public enum FileType {
    FILE("File"),
    FOLDER("Directory");

    private String name;
    private static final Map<String, FileType> map = new HashMap<>();

    static {
        for (FileType value : values()) {
            map.put(value.name.toLowerCase(), value);
        }
    }

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FileType fromName(String type) {
        return map.get(type);
    }
}
