import com.example.directory.DirectoryMain;
import com.example.directory.models.DirectoryDTO;
import com.example.directory.models.FileClassification;
import com.example.directory.models.FileType;
import com.example.directory.models.TreeDirectoryDTO;
import com.example.directory.service.DirectoryOperationImpl;
import com.example.directory.utility.DirectoryUtility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DirectoryTest {

    DirectoryOperationImpl directoryOperation;
    TreeDirectoryDTO rootNode;

    @BeforeClass
    public void setUp() {
        directoryOperation = new DirectoryOperationImpl();
        rootNode = createMockTreeStructure();
    }

    @Test(groups = "part 1")
    public void testDirectoryModelAddSize() {
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, null, null, null);
        rootDTO.addFolderSize(40.0);
        Assert.assertEquals(rootDTO.getSize(), 40.0);
    }

    @Test(groups = "part 1")
    public void testDirectoryModelAddNullSize() {
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, null, null, null);
        rootDTO.addFolderSize(null);
        Assert.assertEquals(rootDTO.getSize(), 0.0);
    }

    @Test(groups = "part 1")
    public void testTreeDirectoryAddChildren() {
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, 490d, null, null);
        TreeDirectoryDTO root = new TreeDirectoryDTO(rootDTO);
        DirectoryDTO sf1 = new DirectoryDTO(2L, "secretFile1", 1L, FileType.FILE, 100.0, FileClassification.SECRET, 42L);
        TreeDirectoryDTO secretFile1 = new TreeDirectoryDTO(sf1);
        root.addChildren(secretFile1);

        Assert.assertEquals(root.getChildren().size(), 1);
        Assert.assertEquals(root.getChildren().get(0).getDirectoryDTO().getId(), 2L);
    }

    @Test(groups = "part 1")
    public void testTreeDirectoryAddNullChildren() {
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, 490d, null, null);
        TreeDirectoryDTO root = new TreeDirectoryDTO(rootDTO);

        Assert.assertThrows(IllegalArgumentException.class, () -> root.addChildren(null));
    }

    @Test(groups = "part 2")
    public void testParseCSVFile() throws IOException {
        String filePath = "src/main/resources/directory-structure.csv";
        List<DirectoryDTO> directoryDTOList = directoryOperation.parseCSVFile(filePath);

        Assert.assertNotNull(directoryDTOList);
        Assert.assertFalse(directoryDTOList.isEmpty());
        Assert.assertEquals(directoryDTOList.size(), 11);
    }

    @Test(groups = "part 2")
    public void testParseNotExistCSVFile() throws FileNotFoundException {
        String filePath = "src/main/resources/notExist-structure.csv";
        Assert.assertThrows(FileNotFoundException.class, () -> directoryOperation.parseCSVFile(filePath));
    }

    @Test(groups = "part 2")
    public void testCreateTreeStructure() {
        List<DirectoryDTO> directoryDTOList = new ArrayList<>();

        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, null, null, null);
        DirectoryDTO sf1 = new DirectoryDTO(2L, "secretFile1", 1L, FileType.FILE, 100.0, FileClassification.SECRET, 42L);
        DirectoryDTO tsf1 = new DirectoryDTO(3L, "topSecretFile1", 1L, FileType.FILE, 80.0, FileClassification.TOP_SECRET, 42L);
        DirectoryDTO f1 = new DirectoryDTO(4L, "folder1", 1L, FileType.FOLDER, 0d, null, null);
        DirectoryDTO pf1 = new DirectoryDTO(5L, "publicFile1", 4L, FileType.FILE, 10.0, FileClassification.PUBLIC, 42L);

        directoryDTOList.add(rootDTO);
        directoryDTOList.add(sf1);
        directoryDTOList.add(tsf1);
        directoryDTOList.add(f1);
        directoryDTOList.add(pf1);

        TreeDirectoryDTO rootNode = new TreeDirectoryDTO(rootDTO);
        directoryOperation.createTreeStructure(rootNode, directoryDTOList);

        Assert.assertNotNull(rootNode.getChildren());
        Assert.assertEquals(rootNode.getChildren().size(), 3);
        Assert.assertEquals(rootNode.getChildren().get(0).getDirectoryDTO().getId(), 4L);
    }

    @Test(groups = "part 2")
    public void testFindRootDirectoryWithSingleRoot() {
        List<DirectoryDTO> directoryDTOList = new ArrayList<>();

        DirectoryDTO root = new DirectoryDTO();
        root.setId(1L);
        root.setParentId(null);
        root.setName("root");

        DirectoryDTO child = new DirectoryDTO();
        child.setId(2L);
        child.setParentId(1L);
        child.setName("child");

        directoryDTOList.add(root);
        directoryDTOList.add(child);

        DirectoryDTO result = directoryOperation.findRootDirectory(directoryDTOList);

        Assert.assertEquals(result.getId(), 1L);
        Assert.assertNull(result.getParentId());
        Assert.assertEquals(result.getName(), "root");
    }

    @Test(groups = "part 2")
    public void testFindRootDirectoryWithMultipleRootsThrowsException() {
        List<DirectoryDTO> directoryDTOList = new ArrayList<>();

        DirectoryDTO root1 = new DirectoryDTO();
        root1.setId(1L);
        root1.setParentId(null);
        root1.setName("root1");

        DirectoryDTO root2 = new DirectoryDTO();
        root2.setId(2L);
        root2.setParentId(null);
        root2.setName("root2");

        directoryDTOList.add(root1);
        directoryDTOList.add(root2);

        Assert.assertThrows(RuntimeException.class, () -> directoryOperation.findRootDirectory(directoryDTOList));
    }

    @Test(groups = "part 2")
    public void testFindRootDirectoryWithNoRootThrowsException() {
        List<DirectoryDTO> directoryDTOList = new ArrayList<>();

        DirectoryDTO child = new DirectoryDTO();
        child.setId(2L);
        child.setParentId(1L);

        DirectoryDTO child2 = new DirectoryDTO();
        child2.setId(3L);
        child2.setParentId(1L);

        directoryDTOList.add(child);
        directoryDTOList.add(child2);

        Assert.assertThrows(RuntimeException.class, () -> directoryOperation.findRootDirectory(directoryDTOList));
    }

    @Test(groups = "part 3-a")
    public void testPrintDirectoryStructure() {
        String expectedOutput = read();
        String actualOutput = DirectoryUtility.getDirectoryStructureByDFS(rootNode, 0);

        Assert.assertNotNull(actualOutput);
        Assert.assertFalse(actualOutput.isEmpty());
        Assert.assertEquals(actualOutput.trim(), expectedOutput.trim());
    }

    @Test(groups = "part 3-a")
    public void testPrintNullStructure() {
        String result = DirectoryUtility.getDirectoryStructureByDFS(null, 0);
        Assert.assertTrue(result.isEmpty());
    }

    @Test(groups = "part 3-b")
    public void testGetNodesWithTopSecretClassification() {
        List<FileClassification> topSecretClassification = List.of(FileClassification.TOP_SECRET);
        String result = DirectoryMain.getNodesByClassification(rootNode, topSecretClassification);
        List<String> classifications = getClassificationsFromString(result);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(classifications.stream().allMatch(c -> c.equals("Top secret")));
        Assert.assertEquals(classifications.size(), 2);
    }

    @Test(groups = "part 3-b")
    public void testGetNodesWithTopSecretClassificationEmpty() {
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, null, null, null);
        TreeDirectoryDTO root = new TreeDirectoryDTO(rootDTO);
        DirectoryDTO sf1 = new DirectoryDTO(2L, "secretFile1", 1L, FileType.FILE, 80.0, FileClassification.SECRET, 42L);
        TreeDirectoryDTO secretFile1 = new TreeDirectoryDTO(sf1);
        root.addChildren(secretFile1);

        List<FileClassification> secretClassification = List.of(FileClassification.TOP_SECRET);
        String result = DirectoryMain.getNodesByClassification(root, secretClassification);

        Assert.assertTrue(result.isEmpty());
    }

    @Test(groups = "part 3-c")
    public void testGetNodesWithSecretClassification() {
        List<FileClassification> secretClassification = List.of(FileClassification.SECRET);

        String result = DirectoryMain.getNodesByClassification(rootNode, secretClassification);
        List<String> classifications = getClassificationsFromString(result);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(classifications.stream().allMatch(c -> c.equals("Secret")));
        Assert.assertEquals(classifications.size(), 2);
    }

    @Test(groups = "part 3-c")
    public void testGetNodesWithSecretEmptyClassification() {
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, null, null, null);
        TreeDirectoryDTO root = new TreeDirectoryDTO(rootDTO);
        DirectoryDTO tsf1 = new DirectoryDTO(2L, "topSecretFile1", 1L, FileType.FILE, 80.0, FileClassification.TOP_SECRET, 42L);
        TreeDirectoryDTO topSecretFile1 = new TreeDirectoryDTO(tsf1);
        root.addChildren(topSecretFile1);

        List<FileClassification> secretClassification = List.of(FileClassification.SECRET);
        String result = DirectoryMain.getNodesByClassification(root, secretClassification);

        Assert.assertTrue(result.isEmpty());
    }

    @Test(groups = "part 3-d")
    public void testGetNodesWithTopSecretAndSecretClassification() {
        List<FileClassification> nonPublicClassificationList = DirectoryUtility.getNonPublicClassificationList();
        String result = DirectoryMain.getNodesByClassification(rootNode, nonPublicClassificationList);

        List<String> classifications = getClassificationsFromString(result);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(classifications.stream().noneMatch((c -> c.equals("Public"))));
        Assert.assertEquals(classifications.size(), 4);
        Assert.assertEquals(classifications.stream().filter(c -> c.equals("Secret")).count(), 2);
        Assert.assertEquals(classifications.stream().filter(c -> c.equals("Top secret")).count(), 2);
    }

    @Test(groups = "part 3-d")
    public void testGetNodesWithTopSecretAndSecretEmptyClassification() {
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, null, null, null);
        TreeDirectoryDTO root = new TreeDirectoryDTO(rootDTO);
        DirectoryDTO pf1 = new DirectoryDTO(2L, "publicFile1", 1L, FileType.FILE, 80.0, FileClassification.PUBLIC, 42L);
        TreeDirectoryDTO publicFile1 = new TreeDirectoryDTO(pf1);
        root.addChildren(publicFile1);

        List<FileClassification> nonPublicClassificationList = DirectoryUtility.getNonPublicClassificationList();
        String result = DirectoryMain.getNodesByClassification(root, nonPublicClassificationList);

        Assert.assertTrue(result.isEmpty());
    }

    @Test(groups = "part 3-e")
    public void testSumOfSizeForPublicClassificationFile() {
        List<FileClassification> classification = List.of(FileClassification.PUBLIC);
        Double totalSize = DirectoryMain.getSumOfSizeForByClassification(rootNode, classification);
        Assert.assertEquals(totalSize, 220.0);
    }

    @Test(groups = "part 3-e")
    public void testSumOfSizeForSecretClassificationFile() {
        List<FileClassification> classification = List.of(FileClassification.SECRET);
        Double totalSize = DirectoryMain.getSumOfSizeForByClassification(rootNode, classification);
        Assert.assertEquals(totalSize, 140.0);
    }

    @Test(groups = "part 3-e")
    public void testSumOfSizeForTopSecretClassificationFile() {
        List<FileClassification> classification = List.of(FileClassification.TOP_SECRET);
        Double totalSize = DirectoryMain.getSumOfSizeForByClassification(rootNode, classification);
        Assert.assertEquals(totalSize, 130.0);
    }

    @Test(groups = "part 3-f")
    public void testGetChildFileNodeUnderGivenFolderExceptPublic() {
        String folderName = "folder1";
        List<FileClassification> nonPublicClassificationList = DirectoryUtility.getNonPublicClassificationList();

        String result = DirectoryMain.getChildFileNodesByFolderName(folderName, rootNode, nonPublicClassificationList);
        List<String> classificationsFromResult = getClassificationsFromString(result);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(classificationsFromResult.size(), 2);
        Assert.assertTrue(classificationsFromResult.stream().noneMatch(c -> c.equalsIgnoreCase("public")));
        Assert.assertEquals(classificationsFromResult.stream().filter(c -> c.equalsIgnoreCase("secret")).count(), 1);
        Assert.assertEquals(classificationsFromResult.stream().filter(c -> c.equalsIgnoreCase("top secret")).count(), 1);
    }

    @Test(groups = "part 3-f")
    public void testGetChildFileNodeUnderGivenInvalidFolder() {
        String folderName = "folder15";
        List<FileClassification> nonPublicClassificationList = DirectoryUtility.getNonPublicClassificationList();
        String result = DirectoryMain.getChildFileNodesByFolderName(folderName, rootNode, nonPublicClassificationList);
        Assert.assertEquals(result, folderName + " is not exist in the given directory structure");
    }

    @Test(groups = "part 3-f")
    public void testGetChildFileNodeUnderNonFolder() {
        String fileName = "secretFile1";
        List<FileClassification> nonPublicClassificationList = DirectoryUtility.getNonPublicClassificationList();
        String result = DirectoryMain.getChildFileNodesByFolderName(fileName, rootNode, nonPublicClassificationList);
        Assert.assertEquals(result, fileName + " is not a folder, please provide a valid folder name");
    }

    @Test(groups = "others")
    public void testNullRootNodeTraversalDFS() {
        Integer result = DirectoryUtility.getNodesBasedOnGivenClassificationByDFS(null, null).size();
        Assert.assertEquals(result, 0);
    }

    @Test(groups = "others")
    public void testNullRootNodeTraversalBFS() {
        Integer result = DirectoryUtility.getAllNodesByBFS(null).size();
        Assert.assertEquals(result, 0);
    }

    @Test(groups = "others")
    public void testGetArgValueByKey() {

        String csvPathValue = DirectoryUtility.getArgValueByKey(new String[]{"csv.path=test/test.csv", "folder.search=testName"}, "csv.path");
        String folderNameValue = DirectoryUtility.getArgValueByKey(new String[]{"csv.path=test/test.csv", "folder.search=testName"}, "folder.search");

        Assert.assertNotNull(csvPathValue);
        Assert.assertNotNull(folderNameValue);
        Assert.assertEquals(csvPathValue, "test/test.csv");
        Assert.assertEquals(folderNameValue, "testName");
    }

    private static TreeDirectoryDTO createMockTreeStructure() {
        // Create mock directory structure with SECRET classified files
        DirectoryDTO rootDTO = new DirectoryDTO(1L, "root", null, FileType.FOLDER, 490d, null, null);
        TreeDirectoryDTO root = new TreeDirectoryDTO(rootDTO);

        DirectoryDTO sf1 = new DirectoryDTO(2L, "secretFile1", 1L, FileType.FILE, 100.0, FileClassification.SECRET, 42L);
        TreeDirectoryDTO secretFile1 = new TreeDirectoryDTO(sf1);

        DirectoryDTO tsf1 = new DirectoryDTO(3L, "topSecretFile1", 1L, FileType.FILE, 80.0, FileClassification.TOP_SECRET, 42L);
        TreeDirectoryDTO topSecretFile1 = new TreeDirectoryDTO(tsf1);

        DirectoryDTO f1 = new DirectoryDTO(4L, "folder1", 1L, FileType.FOLDER, 310d, null, null);
        TreeDirectoryDTO folder1 = new TreeDirectoryDTO(f1);

        DirectoryDTO pf1 = new DirectoryDTO(5L, "publicFile1", 4L, FileType.FILE, 10.0, FileClassification.PUBLIC, 42L);
        TreeDirectoryDTO publicFile1 = new TreeDirectoryDTO(pf1);

        DirectoryDTO sf2 = new DirectoryDTO(6L, "secretFile12", 4L, FileType.FILE, 40.0, FileClassification.SECRET, 42L);
        TreeDirectoryDTO secretFile2 = new TreeDirectoryDTO(sf2);

        DirectoryDTO f2 = new DirectoryDTO(7L, "folder2", 4L, FileType.FOLDER, 260d, null, null);
        TreeDirectoryDTO folder2 = new TreeDirectoryDTO(f2);

        DirectoryDTO pf2 = new DirectoryDTO(8L, "publicFile2", 6L, FileType.FILE, 60.0, FileClassification.PUBLIC, 42L);
        TreeDirectoryDTO publicFile2 = new TreeDirectoryDTO(pf2);

        DirectoryDTO tsf2 = new DirectoryDTO(9L, "topSecretFile2", 6L, FileType.FILE, 50.0, FileClassification.TOP_SECRET, 42L);
        TreeDirectoryDTO topSecretFile2 = new TreeDirectoryDTO(tsf2);

        DirectoryDTO pf3 = new DirectoryDTO(10L, "publicFile3", 6L, FileType.FILE, 70.0, FileClassification.PUBLIC, 42L);
        TreeDirectoryDTO publicFile3 = new TreeDirectoryDTO(pf3);

        DirectoryDTO pf4 = new DirectoryDTO(11L, "publicFile4", 6L, FileType.FILE, 80.0, FileClassification.PUBLIC, 42L);
        TreeDirectoryDTO publicFile4 = new TreeDirectoryDTO(pf4);

        // Mock tree structure:
        //     root (1)
        //     |─ secretFile1 (2) [SECRET]
        //     |─ topSecretFile1 (3) [TOP_SECRET]
        //     |─ folder1 (4)
        //         |─ publicFile1 (5) [PUBLIC]
        //         |─ secretFile12 (6) [SECRET]
        //         |─ folder2 (7)
        //             |─ publicFile2 (8) [PUBLIC]
        //             |─ topSecretFile2 (9) [TOP_SECRET]
        //             |─ publicFile3 (10) [PUBLIC]
        //             |─ publicFile4 (11) [PUBLIC]

        root.addChildren(secretFile1);
        root.addChildren(topSecretFile1);
        root.addChildren(folder1);

        folder1.addChildren(publicFile1);
        folder1.addChildren(secretFile2);
        folder1.addChildren(folder2);

        folder2.addChildren(publicFile2);
        folder2.addChildren(topSecretFile2);
        folder2.addChildren(publicFile3);
        folder2.addChildren(publicFile4);

        return root;
    }

    public String read() {
        return """
                name = root, type = Directory, size = 490
                 name = secretFile1, type = File, size = 100, classification = Secret, checksum = 42
                 name = topSecretFile1, type = File, size = 80, classification = Top secret, checksum = 42
                 name = folder1, type = Directory, size = 310
                  name = publicFile1, type = File, size = 10, classification = Public, checksum = 42
                  name = secretFile12, type = File, size = 40, classification = Secret, checksum = 42
                  name = folder2, type = Directory, size = 260
                   name = publicFile2, type = File, size = 60, classification = Public, checksum = 42
                   name = topSecretFile2, type = File, size = 50, classification = Top secret, checksum = 42
                   name = publicFile3, type = File, size = 70, classification = Public, checksum = 42
                   name = publicFile4, type = File, size = 80, classification = Public, checksum = 42
                """;
    }

    public static List<String> getClassificationsFromString(String result) {
        return Arrays.stream(result.split(", "))
                .filter(l -> l.contains("classification"))
                .map(c -> c.split(" = ")[1])
                .toList();
    }
}



