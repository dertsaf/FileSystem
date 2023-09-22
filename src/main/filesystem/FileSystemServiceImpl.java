package filesystem;

import filesystem.constants.Constants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public void addFile(String destinationFile, String fileToAdd) throws IOException, URISyntaxException {
        Path fileToAddPath = Paths.get(fileToAdd);

        if (!Files.exists(fileToAddPath))
            throw new NoSuchFileException(fileToAdd, null, Constants.NO_SUCH_FILE_MESSAGE);

        ServiceProvider
                .getInstance(ContentFileService.class)
                .saveFileToFileSystem(destinationFile, fileToAddPath.toFile());
    }

    @Override
    public void updateFile(String destinationFile, String fileToUpdateWith) throws IOException, URISyntaxException {
        Path fileToUpdateWithPath = Paths.get(fileToUpdateWith);

        if (!Files.exists(fileToUpdateWithPath)) {
            throw new NoSuchFileException(fileToUpdateWith, null, Constants.NO_SUCH_FILE_MESSAGE);
        }

        ServiceProvider
                .getInstance(ContentFileService.class)
                .updateFileInFileSystem(destinationFile, fileToUpdateWithPath.toFile());
    }

    @Override
    public void deleteFile(String fileToDelete) throws IOException, URISyntaxException {
        ServiceProvider
                .getInstance(ContentFileService.class)
                .deleteFileFromFileSystem(fileToDelete);
    }

    @Override
    public void readFile(String fileToReadFrom, String fileToCopyTo) throws IOException, URISyntaxException {
        Path fileToCopyToPath = Paths.get(fileToCopyTo);

        ServiceProvider
                .getInstance(ContentFileService.class)
                .readFileFromFileSystem(fileToReadFrom, fileToCopyToPath.toFile());
    }
}
