package filesystem.cmd;

import filesystem.FileSystemService;
import filesystem.ServiceProvider;

import java.io.IOException;
import java.net.URISyntaxException;

import static filesystem.constants.Constants.*;

public class DeleteFileCmd {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println(PROVIDE_FILE_PATH_MESSAGE);
            return;
        }

        try {
            var fileToDelete = args[0];
            var fileSystem =  ServiceProvider.getInstance(FileSystemService.class);

            fileSystem.deleteFile(fileToDelete);

            System.out.println(OPERATION_FINISHED_MESSAGE);
        } catch (IOException | URISyntaxException e) {
            System.err.println(OPERATION_FAILED_MESSAGE);
            e.printStackTrace();
        }
    }
}
