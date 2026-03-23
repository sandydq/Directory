package com.example.directory.models;

/**
 * Data transfer object representing a directory entry for both folders and files.
 */
public class DirectoryDTO {
    private Long id;
    private Long parentId;
    private String name;
    private FileType type;
    private Double size;
    private FileClassification classification;
    private Long checkSum;

    public DirectoryDTO(Long id, String name, Long parentId, FileType type, Double size, FileClassification classification, Long checkSum) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.size = size == null ? 0d : size;
        this.classification = classification;
        this.checkSum = checkSum;
    }

    public DirectoryDTO() {
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size == null ? 0d : size;
    }

    public FileClassification getClassification() {
        return classification;
    }

    public void setClassification(FileClassification classification) {
        this.classification = classification;
    }

    public Long getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(Long checkSum) {
        this.checkSum = checkSum;
    }

    public void addFolderSize(Double size) {
        if (this.type.equals(FileType.FOLDER)) {
            this.size += size == null ? 0d : size;
        }
    }

    @Override
    public String toString() {
        if (this.type.equals(FileType.FOLDER)) {
            return "name = " + name +
                    ", type = " + type.getName() +
                    ", size = " + size;
        } else {
            return "name = " + name +
                    ", type = " + type.getName() +
                    ", size = " + size +
                    ", classification = " + (classification == null ? null : classification.getName()) +
                    ", checksum = " + checkSum;
        }
    }
}
