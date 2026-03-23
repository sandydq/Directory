package com.example.directory;

import com.example.directory.models.DirectoryDTO;
import com.example.directory.models.FileClassification;
import com.example.directory.models.FileType;
import com.example.directory.models.TreeDirectoryDTO;
import com.example.directory.service.DirectoryOperationImpl;
import com.example.directory.service.DirectoryOperationInterface;
import com.example.directory.utility.DirectoryUtility;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DirectoryMain {

    private static final String CSV_DEFAULT_FILE_PATH = "src/main/resources/directory-structure.csv";
    private static final String CSV_DEFAULT_FOLDER_NAME_TO_SEARCH = "folder11";
    private static final String ARG_FILE_PATH_KEY = "csv.path=";
    private static final String ARG_FOLDER_TO_SEARCH_KEY = "folder.search=";

    public static void main(String[] args) {

        // Retrieve the file path from command-line arguments, or use the default if not provided
        String filePathFromArg = DirectoryUtility.getArgValueByKey(args, ARG_FILE_PATH_KEY);
        String filepath = filePathFromArg != null && !filePathFromArg.isBlank() ? filePathFromArg : CSV_DEFAULT_FILE_PATH;
        DirectoryOperationInterface directoryOperation = new DirectoryOperationImpl();

        try {
            DirectoryUtility.printSeparator("1) Create domain model to represent the state of the directory structure " + "\n"
                    + "2) Create a parser that reads the csv file (directory-structure.csv) and populates the model");
            List<DirectoryDTO> directoryDTOList = directoryOperation.parseCSVFile(filepath);
            directoryDTOList.forEach(System.out::println);

            DirectoryDTO rootDirectory = directoryOperation.findRootDirectory(directoryDTOList);
            TreeDirectoryDTO rootNode = new TreeDirectoryDTO(rootDirectory);
            directoryOperation.createTreeStructure(rootNode, directoryDTOList);

            DirectoryUtility.printSeparator("3a) Printing indented tree structure of the directory structure");
            String treeStructure = DirectoryUtility.getDirectoryStructureByDFS(rootNode, 0);
            System.out.println(treeStructure);

            DirectoryUtility.printSeparator("3b) Printing all file nodes with classification TOP SECRET under directory structure");
            String topSecretFileNodes = getNodesByClassification(rootNode, List.of(FileClassification.TOP_SECRET));
            System.out.println(topSecretFileNodes);

            DirectoryUtility.printSeparator("3c) Printing all file nodes with classification SECRET under directory structure");
            String secretFileNodes = getNodesByClassification(rootNode, List.of(FileClassification.SECRET));
            System.out.println(secretFileNodes);

            DirectoryUtility.printSeparator("3d) Printing all file nodes with classification SECRET or TOP SECRET under directory structure");
            List<FileClassification> nonPublicClassifications = DirectoryUtility.getNonPublicClassificationList();
            String fileNodesExceptPublic = getNodesByClassification(rootNode, nonPublicClassifications);
            System.out.println(fileNodesExceptPublic);

            DirectoryUtility.printSeparator("3e) Printing the sum of size for all file nodes with classification PUBLIC under directory structure");
            Double sumOfPublicNodeSize = getSumOfSizeForByClassification(rootNode, List.of(FileClassification.PUBLIC));
            System.out.println(sumOfPublicNodeSize);

            // Retrieve the folder to search from command-line arguments, or use the default if not provided
            String folderToSearchFromArg = DirectoryUtility.getArgValueByKey(args, ARG_FOLDER_TO_SEARCH_KEY);
            String folderName = folderToSearchFromArg != null && !folderToSearchFromArg.isBlank() ? folderToSearchFromArg : CSV_DEFAULT_FOLDER_NAME_TO_SEARCH;
            DirectoryUtility.printSeparator("3f) Printing all file nodes under " + folderName + " with classification other than Public");
            String fileNodesUnderFolder11ExceptPublic = getChildFileNodesByFolderName(folderName, rootNode, nonPublicClassifications);
            System.out.println(fileNodesUnderFolder11ExceptPublic);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves file nodes filtered by the given classification(s) and returns them as a formatted string.
     * The nodes are sorted by ID and joined with newline separators.
     *
     * @param rootNode       the root node of the directory tree structure
     * @param classification a list of FileClassification values to filter
     * @return a string representation of nodes matching the given classification(s), sorted by ID and separated by newlines
     */
    public static String getNodesByClassification(TreeDirectoryDTO rootNode, List<FileClassification> classification) {
        String nodesByGivenClassification;
        nodesByGivenClassification = DirectoryUtility.getNodesBasedOnGivenClassificationByDFS(rootNode, classification)
                .stream()
                .sorted(Comparator.comparing(DirectoryDTO::getId))
                .map(DirectoryDTO::toString)
                .collect(Collectors.joining("\n"));
        return nodesByGivenClassification;
    }

    /**
     * Calculates the total size of all file nodes matching the given classification(s).
     *
     * @param rootNode       the root node of the directory tree structure
     * @param classification a list of FileClassification values to filter
     * @return the sum of sizes of all file nodes matching the given classification(s)
     */
    public static Double getSumOfSizeForByClassification(TreeDirectoryDTO rootNode, List<FileClassification> classification) {
        Double sumOfSizeForGivenClassification;
        sumOfSizeForGivenClassification = DirectoryUtility.getNodesBasedOnGivenClassificationByDFS(rootNode, classification)
                .stream()
                .map(DirectoryDTO::getSize)
                .reduce(0.0, Double::sum);
        return sumOfSizeForGivenClassification;
    }

    /**
     * Retrieves all child file nodes under the specified folder that match the given classification(s).
     *
     * @param folderName      the name of the folder to search within the directory tree
     * @param rootNode        the root node of the directory tree structure
     * @param classifications a list of FileClassification values to filter
     * @return a formatted string of matching child file nodes sorted by ID,
     * or an error message if the folder is not found or the name does not refer to a folder
     */
    public static String getChildFileNodesByFolderName(String folderName, TreeDirectoryDTO rootNode, List<FileClassification> classifications) {
        // First, find the subtree node to the given folder name.
        Optional<TreeDirectoryDTO> folderNode = DirectoryUtility.getAllNodesByBFS(rootNode)
                .stream()
                .filter(td -> td.getDirectoryDTO().getName().equals(folderName))
                .findFirst();

        if (folderNode.isEmpty()) {
            return folderName + " is not exist in the given directory structure";
        } else if (!folderNode.get().getDirectoryDTO().getType().equals(FileType.FOLDER)) {
            return folderName + " is not a folder, please provide a valid folder name";
        }
        // Get all child nodes under the folder node with the given classifications.
        return getNodesByClassification(folderNode.get(), classifications);
    }
}

//        2(F)
//    4               11(F)
//            3(F)           10(F)
//        1   5   6   7    8       9

//BFS - 2, 4, 11, 3, 10, 1, 5, 6, 7, 8, 9
//DFS - 2, 4, 11, 3, 1, 5, 6, 7, 10, 8, 9
//Valid- 2, 4, 11, 3, 1, 5, 6, 7, 10, 8, 9

