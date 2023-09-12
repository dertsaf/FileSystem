package filesystem;

import main.filesystem.constants.Constants;
import main.filesystem.utils.FileSystemUtil;

import java.io.IOException;
import java.nio.file.*;

public class FileSystemServiceImpl implements FileSystemService {

    @Override
    public void addFile(String destinationFile, String fileToAdd) throws IOException {
        Path destinationFilePath = Paths.get(destinationFile);
        Path fileToAddPath = Paths.get(fileToAdd);

        Path destinationDiRectory = FileSystemUtil.extractDirectory(destinationFile);

        if (destinationDiRectory != null)
            Files.createDirectories(destinationDiRectory);

        if (Files.exists(destinationFilePath))
            throw new FileAlreadyExistsException(destinationFile, null, Constants.FILE_ALREADY_EXISTS_MESSAGE);

        if (!Files.exists(fileToAddPath))
            throw new NoSuchFileException(fileToAdd, null, Constants.NO_SUCH_FILE_MESSAGE);

        ServiceProvider
                .getInstance(ContentFileService.class)
                .copyContent(destinationFilePath.toFile(), fileToAddPath.toFile());
    }

    @Override
    public void updateFile(String destinationFile, String fileToUpdateWith) throws IOException {
        Path destinationFilePath = Paths.get(destinationFile);
        Path fileToUpdateWithPath = Paths.get(fileToUpdateWith);

        var destinationFileExists = Files.exists(destinationFilePath);
        if (!destinationFileExists || !Files.exists(fileToUpdateWithPath)) {
            throw new NoSuchFileException(destinationFileExists ? destinationFile : fileToUpdateWith, null, Constants.NO_SUCH_FILE_MESSAGE);
        }

        ServiceProvider
                .getInstance(ContentFileService.class)
                .copyContent(destinationFilePath.toFile(), fileToUpdateWithPath.toFile());
    }

    @Override
    public void deleteFile(String fileToDelete) throws IOException {
        Files.delete(Paths.get(fileToDelete));
    }

    @Override
    public void readFile(String fileToReadFrom, String fileToCopyTo) throws IOException {
        addFile(fileToCopyTo, fileToReadFrom);
    }
}
