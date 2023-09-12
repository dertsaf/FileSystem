package filesystem;

import java.io.IOException;

/**
 * The FileSystemService provides methods for managing files in a file system.
 * These methods allow you to add, update, delete, and read files in the file system.
 */
public interface FileSystemService {

    /**
     * Adds a file to the file system at the specified destination path.
     *
     * @param destinationFile The path where the file should be added in the file system.
     * @param fileToAdd       The path to the file to be added to the file system.
     * @throws IOException If an I/O error occurs during the operation.
     */
    void addFile(String destinationFile, String fileToAdd) throws IOException;

    /**
     * Updates the contents of a file in the file system with the contents of another file.
     *
     * @param destinationFile The path of the file to be updated in the file system.
     * @param fileToUpdateWith The path to the file whose contents will be used for the update.
     * @throws IOException If an I/O error occurs during the operation.
     */
    void updateFile(String destinationFile, String fileToUpdateWith) throws IOException;

    /**
     * Deletes a file from the file system.
     *
     * @param fileToDelete The path of the file to be deleted from the file system.
     * @throws IOException If an I/O error occurs during the operation.
     */
    void deleteFile(String fileToDelete) throws IOException;

    /**
     * Reads the contents of a file from the file system and copies it to another file.
     *
     * @param fileToReadFrom The path of the file to be read from the file system.
     * @param fileToCopyTo   The path to the file where the contents will be copied.
     * @throws IOException If an I/O error occurs during the operation.
     */
    void readFile(String fileToReadFrom, String fileToCopyTo) throws IOException;
}
