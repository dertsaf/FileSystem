package filesystem.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemUtil {
    private FileSystemUtil(){}

    public static Path extractDirectory(String filePath) {
        Path path = Paths.get(filePath);
        return path.getParent();
    }
}
