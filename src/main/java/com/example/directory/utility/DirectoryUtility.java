package com.example.directory.utility;

import com.example.directory.models.DirectoryDTO;
import com.example.directory.models.FileClassification;
import com.example.directory.models.FileType;
import com.example.directory.models.TreeDirectoryDTO;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DirectoryUtility {

    public static boolean isValidValue(String value) {
        return value != null && !value.isBlank();
    }

    static Function<Integer, String> separator = "_"::repeat;

    public static void printSeparator(String value) {
        System.out.println("\n" + separator.apply(100));
        System.out.println(value);
        System.out.println(separator.apply(100));
    }

    public static String getArgValueByKey(String[] args, String key) {
        return Arrays.stream(args)
                .filter(a -> a.contains(key))
                .map(a -> a.split("=")[1])
                .findFirst().orElse(null);
    }

    public static List<FileClassification> getNonPublicClassificationList() {
        return Arrays.stream(FileClassification.values())
                .filter(c -> !c.equals(FileClassification.PUBLIC))
                .collect(Collectors.toList());
    }

    public static Boolean isFolder(DirectoryDTO directoryDTO) {
        return directoryDTO.getType().equals(FileType.FOLDER);
    }

    public static Boolean isMatchingClassification(DirectoryDTO directoryDTO, List<FileClassification> classifications) {
        return ((directoryDTO.getClassification() != null && classifications.contains(directoryDTO.getClassification()))
                || classifications == null);
    }

    static Function<Integer, String> indentation = " "::repeat;

    /**
     * Builds a string representation of the directory tree using depth first traversal.
     *
     * @param node the current directory tree node
     * @param step the indentation level for the current node
     * @return the formatted directory structure as a string
     */
    public static String getDirectoryStructureByDFS(TreeDirectoryDTO node, Integer step) {
        if (node == null) {
            return "Provided tree node is null, returning empty string";
        }
        String result = indentation.apply(step) + node.getDirectoryDTO() + "\n";

        //System.out.println(indentation.apply(step) + node.getDirectoryDTO());
        if (isFolder(node.getDirectoryDTO())) {
            step += 1;
            for (TreeDirectoryDTO children : node.getChildren()) {
                result = result.concat(getDirectoryStructureByDFS(children, step));
            }
        }
        return result;
    }

    /**
     * Traverses the tree using depth first search and returns nodes whose classification
     * matches the provided list. If classification is null, all nodes are returned.
     *
     * @param node           the root node to traverse
     * @param classifications the allowed classifications to filter by, or null for all
     * @return a list of matching directory entries
     */
    public static List<DirectoryDTO> getNodesBasedOnGivenClassificationByDFS(TreeDirectoryDTO node, List<FileClassification> classifications) {
        if (node == null) {
            System.out.println("Provided node is null, returning empty list");
            return new ArrayList<>();
        }

        List<DirectoryDTO> files = new ArrayList<>();
        if (isMatchingClassification(node.getDirectoryDTO(), classifications))
            files.add(node.getDirectoryDTO());

        if (isFolder(node.getDirectoryDTO())) {
            for (TreeDirectoryDTO children : node.getChildren()) {
                files.addAll(getNodesBasedOnGivenClassificationByDFS(children, classifications));
            }
        }
        return files;
    }

    /**
     * Traverses the tree using breadth first search and returns all nodes.
     *
     * @param node the root node to start the traversal
     * @return a list of all nodes in breadth first order
     */
    public static List<TreeDirectoryDTO> getAllNodesByBFS(TreeDirectoryDTO node) {
        if (node == null) {
            System.out.println("Provided node is null, returning empty list");
            return new ArrayList<>();
        }

        List<TreeDirectoryDTO> directoryResult = new ArrayList<>();
        Queue<TreeDirectoryDTO> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            TreeDirectoryDTO treeDirectoryNode = queue.poll();
            directoryResult.add(treeDirectoryNode);
            if (treeDirectoryNode.getChildren() != null) {
                queue.addAll(treeDirectoryNode.getChildren());
            }
        }
        return directoryResult;
    }
}

