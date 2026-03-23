# Directory Structure Parser
## Overview
A Java application that parses flat CSV files into hierarchical directory tree structures and performs analysis operations.

---
## Features

- **CSV Parsing**: Parse flat CSV files into domain models
- **Tree Structure**: Build hierarchical trees from flat directory lists
- **Tree Traversal**: DFS and BFS traversal algorithms
- **Filtering**: Filter files/folders by security classification (PUBLIC, SECRET, TOP_SECRET)
- **Analysis**: Calculate aggregate sizes and search for specific folders
---
## Project Structure

```
src/main/java/com/example/directory/
─ DirectoryMain.java              # Entry point
|─ models/
    ─ DirectoryDTO.java           # Data model
    ─ TreeDirectoryDTO.java       # Tree node model
    ─ FileType.java               # File/Folder enum
    ─ FileClassification.java     # Classification enum
|─ service/
    ─ DirectoryOperationInterface.java    # Service interface
    ─ DirectoryOperationImpl.java         # Service implementation
|─ utility/
    ─ DirectoryUtility.java       # Helper utilities
```
---
## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6.0+

### Build
```bash
mvn clean install
```

### Run
```bash
# Using default CSV file(directory-structure.csv)
java -jar target/Directory-1.0.0-jar-with-dependencies.jar

# Using custom CSV file
java -jar target/Directory-1.0.0-jar-with-dependencies.jar csv.path=/path/file.csv

# Search in specific folder
java -jar target/Directory-1.0.0-jar-with-dependencies.jar folder.search=folder_name

# Search for specific folder in custom CSV file
java -jar target/Directory-1.0.0-jar-with-dependencies.jar csv.path=/path/file.csv folder.search=folder_name
```
---
## CSV Format

```
#id; parentId; name; type; size; classification; checksum;

1;;root;directory;;;;
2;1;folder1;directory;;;;
3;1;file1.txt;file;1024;Public;12345;
```
---
## Technology Stack

- **Java**: 17
- **Build**: Maven
- **Testing**: TestNG
- **Code Coverage**: JaCoCo
---
## Testing

```bash
# Run all tests
mvn test

# View test report
target/surefire-reports/index.html
```
---
## Code Coverage

```bash
# Generate code coverage report
mvn jacoco:report

# View coverage report
target/site/jacoco/index.html
```
---


