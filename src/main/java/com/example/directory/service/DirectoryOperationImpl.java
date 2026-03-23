package com.example.directory.service;

import com.example.directory.models.DirectoryDTO;
import com.example.directory.models.FileClassification;
import com.example.directory.models.FileType;
import com.example.directory.models.TreeDirectoryDTO;
import com.example.directory.utility.DirectoryUtility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DirectoryOperationImpl implements DirectoryOperationInterface {

    private static final String CSV_DELIMITER = ";";

    /**
     * Parses a CSV file and converts each line into a DirectoryDTO object.
     *
     * @param filePath the path to the CSV file to parse
     * @return a list of DirectoryDTO objects parsed from the CSV file
     * @throws RuntimeException if an IOException occurs while reading the file
     */
    @Override
    public List<DirectoryDTO> parseCSVFile(String filePath) throws IOException {
        List<DirectoryDTO> directoryDTOS = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comment lines (starting with #)
                if (!line.startsWith("#") && !line.isEmpty()) {
                    // Split the line by semicolon delimiter
                    String[] values = line.replaceAll(";", "; ").split(CSV_DELIMITER);
                    DirectoryDTO directoryDTO = new DirectoryDTO();
                    // Parse and set each field from the CSV values
                    directoryDTO.setId(DirectoryUtility.isValidValue(values[0]) ? Long.parseLong(values[0].trim()) : null);
                    directoryDTO.setParentId(DirectoryUtility.isValidValue(values[1]) ? Long.parseLong(values[1].trim()) : null);
                    directoryDTO.setName(DirectoryUtility.isValidValue(values[2]) ? values[2].trim() : null);
                    directoryDTO.setType(DirectoryUtility.isValidValue(values[3]) ? FileType.fromName(values[3].trim().toLowerCase()) : null);
                    directoryDTO.setSize(DirectoryUtility.isValidValue(values[4]) ? Double.parseDouble(values[4].trim()) : 0d);
                    directoryDTO.setClassification(DirectoryUtility.isValidValue(values[5]) ? FileClassification.fromName(values[5].trim().toLowerCase()) : null);
                    directoryDTO.setCheckSum(DirectoryUtility.isValidValue(values[6]) ? Long.parseLong(values[6].trim()) : null);
                    directoryDTOS.add(directoryDTO);
                }
            }
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new FileNotFoundException("File not found at path: " + filePath);
            }
            throw e;
        }
        return directoryDTOS;
    }

    /**
     * Finds the root directory entry from the provided list.
     *
     * @param directoryDTOList the list of directory entries to search
     * @return the single root directory entry (the one with a `null` parentId)
     */
    @Override
    public DirectoryDTO findRootDirectory(List<DirectoryDTO> directoryDTOList) {
        List<DirectoryDTO> rootDirectory = directoryDTOList
                .stream()
                .filter(dir -> dir.getParentId() == null)
                .toList();

        if (rootDirectory.size() != 1) {
            throw new RuntimeException("More than one or zero root element present in the given CSV file");
        }
        return rootDirectory.get(0);
    }

    /**
     * Builds the tree data structure for the given folder node by attaching all direct and nested children.
     * Also aggregates child sizes into the current folder size:
     * - If node is folder type, includes their computed total size after recursion
     * - If node is file type , includes the file size directly
     *
     * @param folderNode the current folder node to add children and Initially starts with the root directory node
     * @param source     the flat list of directory entries used to resolve parent-child relationships
     */
    @Override
    public void createTreeStructure(TreeDirectoryDTO folderNode, List<DirectoryDTO> source) {
        Long parentId = folderNode.getDirectoryDTO().getId();
        List<DirectoryDTO> children = source
                .stream()
                .filter(dir -> dir.getParentId() != null && dir.getParentId().equals(parentId))
                .sorted(Comparator.comparing(DirectoryDTO :: getName))
                .toList();

        for (DirectoryDTO child : children) {
            TreeDirectoryDTO treeNode = new TreeDirectoryDTO(child);
            folderNode.addChildren(treeNode);
            // Do recursion for child nodes only if it's a folder.
            if (child.getType().equals(FileType.FOLDER)) {
                createTreeStructure(treeNode, source);
                // After processing the child folder, add its size to the parent folder's size
                folderNode.getDirectoryDTO().addFolderSize(treeNode.getDirectoryDTO().getSize());
            } else {
                // If it's a file, add its size to the parent folder's size
                folderNode.getDirectoryDTO().addFolderSize(child.getSize());
            }
        }
    }

    /*@Override
    @Deprecated
    public List<DirectoryDTO> getFilesForGivenClassification(List<FileClassification> classification, List<DirectoryDTO> directoryDTOList) {
        return directoryDTOList
                .stream()
                .filter(d -> d.getClassification() != null && classification.contains(d.getClassification()))
                .sorted(Comparator.comparing(DirectoryDTO::getId))
                .collect(Collectors.toList());
    }*/
}
