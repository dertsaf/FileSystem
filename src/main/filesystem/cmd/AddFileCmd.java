package filesystem.cmd;

import filesystem.FileSystemService;
import filesystem.ServiceProvider;

import java.io.IOException;

public class AddFileCmd {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Please provide both paths to the files");
            return;
        }

        try {
            var destinationFile = args[0];
            var fileToAdd = args[1];
            var fileSystem = ServiceProvider.getInstance(FileSystemService.class);

            fileSystem.addFile(destinationFile, fileToAdd);

            System.out.println("The file '" + destinationFile + "' was successfully added");
        } catch (IOException e) {
            System.err.println("Operation failed.");
            e.printStackTrace();
        }

    }
}
