package filesystem.utils;

public record FileMetaData(long startFilePosition, long fileLength) {

    public static final String METADATA_SEPARATOR = ",";

    @Override
    public String toString() {
        return startFilePosition + METADATA_SEPARATOR + fileLength;
    }
}
