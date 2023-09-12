# FileSystem 

This documentation provides an overview of an application called FileSystem.
The application can do 4 things: 
- add a file to the system
- update a file in the system
- read a file
- delete a file

## Prerequisites
In order to run the application you need to either download the FileSystem.jar or clone the repository 
and build artifacts yourself using Intellij Idea.
In order to build artifacts you need to go to Build -> Build Artifacts.
Generated .jar file should be located in the /out/artifacts/FileSystem_jar/ folder

## `addFile`

Adds a file to the file system at the specified destination path. 

### Example
The following script will create a file called _NewFile.txt_ with the content of  _ExistingFile.txt_ 

`java -cp FileSystem.jar filesystem.cmd.AddFileCmd /folder1/folder2/NewFile.txt ./ExistingFile.txt`


## `updateFile`

Updates the contents of a file in the file system with the content of another file.

### Example
The following script will update the _ToBeUpdated.txt_ file with the content of  _UpdateWith.txt_

`java -cp FileSystem.jar filesystem.cmd.updateFileCmd /folder1/folder2/ToBeUpdated.txt ./UpdateWith.txt`


## `readFile`

Reads the contents of a file from the file system and copies it to another file.

### Execution
The following script will create a file _FileToCopyTo.txt_ and copy the content of  _FileToReadFrom.txt_ to it

`java -cp FileSystem.jar filesystem.cmd.readFileCmd /folder1/folder2/FileToReadFrom.txt ./FileToCopyTo.txt`


## `deleteFile`

Deletes a file from the file system.

### Execution
The following script will delete _ExistingFileToDelete.txt_ file from the file system.

`java -cp FileSystem.jar filesystem.cmd.deleteFileCmd ./ExistingFileToDelete.txt`
