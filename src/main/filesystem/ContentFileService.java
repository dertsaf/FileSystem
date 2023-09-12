package filesystem;

import java.io.File;
import java.io.IOException;

/**
 * The ContentFileService provides methods for working with files based on their content.
 * It allows you to copy content from one file to another and check if two files have the same content.
 */
public interface ContentFileService {

    /**
     * Copies the content of one file to another.
     *
     * @param destinationFile The file where the content will be copied to.
     * @param fileToCopy      The file whose content will be copied.
     * @throws IOException If an I/O error occurs during the copy operation.
     */
    void copyContent(File destinationFile, File fileToCopy) throws IOException;

    /**
     * Checks whether two files have the same content.
     *
     * @param expected The first file for content comparison.
     * @param actual   The second file for content comparison.
     * @return true if the files have the same content, false otherwise.
     * @throws IOException If an I/O error occurs during the comparison.
     */
    boolean haveSameContent(File expected, File actual) throws IOException;
}
