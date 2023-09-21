package filesystem.cmd;

import filesystem.FileSystemService;
import filesystem.ServiceProvider;

import java.io.IOException;
import java.net.URISyntaxException;

import static filesystem.constants.Constants.*;

public class AddFileCmd {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println(PROVIDE_BOTH_FILES_MESSAGE);
            return;
        }

        try {
            var destinationFile = args[0];
            var fileToAdd = args[1];
            var fileSystem = ServiceProvider.getInstance(FileSystemService.class);

            fileSystem.addFile(destinationFile, fileToAdd);

            System.out.println(OPERATION_FINISHED_MESSAGE);
        } catch (IOException | URISyntaxException e) {
            System.err.println(OPERATION_FAILED_MESSAGE);
            e.printStackTrace();
        }

    }
}
