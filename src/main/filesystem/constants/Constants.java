package filesystem.constants;

public class Constants {

    public static final String FILE_ALREADY_EXISTS_MESSAGE = "An error occurred while adding a file. " +
            "Please verify that you've provided correct path to the destination file. " +
            "The file you have provided already exists.";

    public static final String NO_SUCH_FILE_MESSAGE = "An error occurred while updating the file. " +
            "Please verify that you've provided correct path to the destination file." +
            "The file you have provided does not exist.";

    public static final String PROVIDE_BOTH_FILES_MESSAGE = "Please provide both paths to the files.";

    public static final String PROVIDE_FILE_PATH_MESSAGE = "Please provide the path to the file to be deleted.";

    public static final String OPERATION_FAILED_MESSAGE = "Operation failed.";

    public static final String OPERATION_FINISHED_MESSAGE = "The operation was successfully finished.";

    public static final String SERVICE_NOT_REGISTERED_MESSAGE = "Service not registered: %s";
    public static final String EXISTING_IMPLEMENTATION_COULD_NOT_BE_OVERRIDDEN_MESSAGE = "The service %s couldn't be overridden since it was already registered.";

}
