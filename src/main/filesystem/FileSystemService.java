package filesystem;

import java.io.IOException;

public interface FileSystemService {
    /**
     * todo
     */
    void addFile(String destinationFile, String fileToAdd) throws IOException;

    /**
     * todo
     */
    void updateFile(String destinationFile, String fileToUpdateWith) throws IOException;

    /**
     * todo
     */
    void deleteFile(String fileToDelete) throws IOException;

    /**
     * todo
     */
    void readFile(String fileToReadFrom, String fileToCopyTo) throws IOException;
}
