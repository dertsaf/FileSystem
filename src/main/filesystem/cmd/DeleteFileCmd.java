package filesystem.cmd;

import filesystem.FileSystemService;
import filesystem.ServiceProvider;

import java.io.IOException;

public class DeleteFileCmd {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Please provide the path to the file to be deleted.");
            return;
        }

        try {
            var fileToDelete = args[0];
            var fileSystem =  ServiceProvider.getInstance(FileSystemService.class);

            fileSystem.deleteFile(fileToDelete);

            System.out.println("The file '" + fileToDelete + "' was successfully deleted.");
        } catch (IOException e) {
            System.err.println("Operation failed.");
            e.printStackTrace();
        }
    }
}
