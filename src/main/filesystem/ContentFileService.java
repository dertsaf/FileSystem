package filesystem;

import java.io.File;
import java.io.IOException;

public interface ContentFileService {
    void copyContent(File destinationFile, File fileToCopy) throws IOException;

    boolean haveSameContent(File expected, File actual) throws IOException;
}
