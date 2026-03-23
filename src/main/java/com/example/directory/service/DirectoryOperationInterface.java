package com.example.directory.service;

import com.example.directory.models.DirectoryDTO;
import com.example.directory.models.TreeDirectoryDTO;

import java.io.IOException;
import java.util.List;

public interface DirectoryOperationInterface {

    List<DirectoryDTO> parseCSVFile(String filePath) throws IOException;

    DirectoryDTO findRootDirectory(List<DirectoryDTO> directoryDTOList);

    void createTreeStructure(TreeDirectoryDTO folderNode, List<DirectoryDTO> source);

    //List<DirectoryDTO> getFilesForGivenClassification(List<FileClassification> classification, List<DirectoryDTO> directoryDTOList);
}
