package filesystem.cmd;

import filesystem.FileSystemService;
import filesystem.ServiceProvider;

import java.io.IOException;

public class UpdateFileCmd {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Please provide both paths to the files");
            return;
        }

        try {
            var destinationFile = args[0];
            var fileToAdd = args[1];
            var fileSystem =  ServiceProvider.getInstance(FileSystemService.class);

            fileSystem.updateFile(destinationFile, fileToAdd);

            System.out.println("The file '" + destinationFile + "' was successfully updated.");
        } catch (IOException e) {
            System.err.println("Operation failed.");
            e.printStackTrace();
        }
    }
}
