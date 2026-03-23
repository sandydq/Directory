package com.example.directory.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data transfer object representing a directory node in a tree structure.
 */
public class TreeDirectoryDTO {
    private DirectoryDTO directoryDTO;
    private List<TreeDirectoryDTO> children;

    public TreeDirectoryDTO(DirectoryDTO directoryDTO) {
        this.directoryDTO = directoryDTO;
        if (this.directoryDTO.getType().equals(FileType.FILE)) {
            this.children = null;
        } else {
            this.children = new ArrayList<>();
        }
    }

    public DirectoryDTO getDirectoryDTO() {
        return directoryDTO;
    }

    public List<TreeDirectoryDTO> getChildren() {
        return children != null ? children : Collections.emptyList();
    }

    public void addChildren(TreeDirectoryDTO node) {
        if (node == null)
            throw new IllegalArgumentException("Provided node is null, cannot add to children list");
        this.children.add(node);
    }
}
