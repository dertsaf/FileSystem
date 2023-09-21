package filesystem.constants;

public class Constants {

    public static final String FILE_ALREADY_EXISTS_MESSAGE = "An error occurred while adding a file. " +
            "Please verify that you've provided correct path to the destination file. " +
            "The file you have provided already exists. ";

    public static final String NO_SUCH_FILE_MESSAGE = "An error occurred while updating the file. " +
            "Please verify that you've provided correct path to the file." +
            "The file you have provided does not exist: %s";

    public static final String PROVIDED_FILE_ALREADY_EXISTS_IN_FILE_SYSTEM_MESSAGE = "Provided file already exists in the file system: %s";

    public static final String PROVIDED_FILE_DOES_NOT_EXIST_IN_FILE_SYSTEM_MESSAGE = "Provided file does not exist in the file system: %s";

    public static final String INTERNAL_ERROR_MESSAGE = "Internal Error. Our system has been broken. Please clean the system by deleting this file: ";

    public static final String INTERNAL_ERROR_SYSTEM_FILE_DOES_NOT_EXIST_MESSAGE = "Internal Error. Our system has been broken. Please create new file: ";

    public static final String PROVIDE_BOTH_FILES_MESSAGE = "Please provide both paths to the files.";

    public static final String PROVIDE_FILE_PATH_MESSAGE = "Please provide the path to the file to be deleted.";

    public static final String OPERATION_FAILED_MESSAGE = "Operation failed.";

    public static final String OPERATION_FINISHED_MESSAGE = "The operation was successfully finished.";

    public static final String SERVICE_NOT_REGISTERED_MESSAGE = "Service not registered: %s";
    public static final String EXISTING_IMPLEMENTATION_COULD_NOT_BE_OVERRIDDEN_MESSAGE = "The service %s couldn't be overridden since it was already registered.";

}
