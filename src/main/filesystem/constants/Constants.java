package filesystem.constants;

public class Constants {

    public static final String FILE_ALREADY_EXISTS_MESSAGE = "An error occurred while adding a file. " +
            "Please verify that you've provided correct path to the destination file. " +
            "The file you have provided already exists.";

    public static final String NO_SUCH_FILE_MESSAGE = "An error occurred while updating the file. " +
            "Please verify that you've provided correct path to the destination file." +
            "The file you have provided does not exist.";

    public static final String FILE_NOT_FOUND_MESSAGE = "An error occurred while copying the file's content. " +
            "Please verify that you've provided correct path to the files.";

    public static final String INVALID_PATH_MESSAGE = "Please provide correct path to the file";
}
