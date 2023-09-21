package filesystem;

import filesystem.utils.FileMetaData;
import filesystem.utils.FileSystemUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static filesystem.constants.Constants.*;

public class ContentFileServiceImpl implements ContentFileService {

    private static final String FILE_SYSTEM_PATH = "virtual-file-system";

    private static final String OFFSET_SEPARATOR = "#;";

    private static final long INITIAL_FILE_CONTENT_OFFSET = 100;

    private static final int BUFFER_SIZE = 4096;

    private Map<String, FileMetaData> indexMap;

    private long fileContentOffset = -1;

    public void saveFileToFileSystem(String destinationFile, File fileToCopy) throws IOException, URISyntaxException {
        //load map and offset
        loadIndexMapAndContentOffsetIfNeeded();

        if (!fileToCopy.exists())
            throw new NoSuchFileException(String.format(NO_SUCH_FILE_MESSAGE, fileToCopy));

        if (indexMap.containsKey(destinationFile)) {
            System.out.println(String.format(PROVIDED_FILE_ALREADY_EXISTS_IN_FILE_SYSTEM_MESSAGE, destinationFile));
            return;
        }

        var metaData = appendToEndOfSystemFile(destinationFile, fileToCopy);
        indexMap.put(destinationFile, metaData);
        saveIndexMapToFile();
    }

    public void readFileFromFileSystem(String fileToReadFrom, File fileToCopyTo) throws IOException, URISyntaxException {
        //load map and offset
        loadIndexMapAndContentOffsetIfNeeded();

        //there is no such file in the file system
        if (!indexMap.containsKey(fileToReadFrom)) {
            System.out.println(String.format(PROVIDED_FILE_DOES_NOT_EXIST_IN_FILE_SYSTEM_MESSAGE, fileToReadFrom));
            return;
        }

        var fileSystemPath = getSystemFilePath();
        try (OutputStream outputStream = new FileOutputStream(fileToCopyTo);
             RandomAccessFile systemFile = new RandomAccessFile(fileSystemPath, "rw")) {

            var fileMetaData = indexMap.get(fileToReadFrom);
            systemFile.seek(fileMetaData.startFilePosition() + System.lineSeparator().length());
            var readFileLength = fileMetaData.fileLength();

            var fileName = systemFile.readLine();
            if (!fileToReadFrom.equals(fileName)) {
                throw new RuntimeException(String.format(INTERNAL_ERROR_MESSAGE, FILE_SYSTEM_PATH));
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            long totalBytesRead = 0;
            while ((bytesRead = systemFile.read(buffer)) != -1) {

                if (totalBytesRead >= readFileLength)
                    break;

                long remainingBytesToRead = readFileLength - totalBytesRead;

                // Ensure that we don't read more bytes than needed
                if (bytesRead > remainingBytesToRead) {
                    bytesRead = (int) remainingBytesToRead;
                }

                outputStream.write(buffer, 0, bytesRead);

                totalBytesRead += bytesRead;
            }
        }
    }

    public void deleteFileFromFileSystem(String fileToDelete) throws IOException, URISyntaxException {
        //load map and offset
        loadIndexMapAndContentOffsetIfNeeded();

        if (!indexMap.containsKey(fileToDelete)) {
            System.out.println(String.format(PROVIDED_FILE_DOES_NOT_EXIST_IN_FILE_SYSTEM_MESSAGE, fileToDelete));
            return;
        }

        var fileMetaData = indexMap.get(fileToDelete);

        //2 separators + file name + file content:
        long bytesToDelete = 2L * System.lineSeparator().getBytes().length + fileMetaData.fileLength() + fileToDelete.getBytes().length;
        shiftFilesContentBackward(fileMetaData.startFilePosition() + bytesToDelete, bytesToDelete);

        removeFromIndexMapAndRecalculateIndexes(fileToDelete);
    }

    @Override
    public void updateFileInFileSystem(String destinationFile, File fileToUpdateWith) throws IOException, URISyntaxException {
        deleteFileFromFileSystem(destinationFile);
        saveFileToFileSystem(destinationFile, fileToUpdateWith);
    }

    @Override
    public void copyContent(File destinationFile, File fileToCopy) throws IOException {
        if (!fileToCopy.exists()) {
            return;
        }

        try (InputStream inputStream = new FileInputStream(fileToCopy);
             OutputStream outputStream = new FileOutputStream(destinationFile))
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);
        }
    }

    @Override
    public boolean haveSameContent(File expected, File actual) throws IOException {
        if (!expected.exists() || !actual.exists()) {
            return false;
        }

        if (expected.length() != actual.length()) {
            return false;
        }

        try (InputStream is1 = new FileInputStream(expected);
             InputStream is2 = new FileInputStream(actual)) {
            int byte1, byte2;
            do {
                byte1 = is1.read();
                byte2 = is2.read();
                if (byte1 != byte2) {
                    return false;
                }
            } while (byte1 != -1 && byte2 != -1);
        }

        return true;
    }

    private FileMetaData appendToEndOfSystemFile(String pathToFile, File fileToAppend) throws IOException, URISyntaxException {
        var resourceSystemFile = getSystemFilePath();

        long position = -1;

        try (InputStream inputStream = new FileInputStream(fileToAppend);
            RandomAccessFile systemFile = new RandomAccessFile(resourceSystemFile, "rw")) {

            position = Math.max(systemFile.length(), fileContentOffset);
            systemFile.seek(position);
            //2 line separators + pathToFile (the length of the string will be used in the delete method)
            String fileEntry = System.lineSeparator() + pathToFile + System.lineSeparator();
            systemFile.writeBytes(fileEntry);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1)
                systemFile.write(buffer, 0, bytesRead);
        }

        return new FileMetaData(position, fileToAppend.length());
    }

    /**
     * Metadata (index map + offset) are stored in the first part of the system file. Initially we allocate INITIAL_FILE_CONTENT_OFFSET size.
     * In case we reach that size we double the offset by x2, shift all the content and recalculate the map.
     * @throws IOException  If an I/O error occurs
     * @throws URISyntaxException  If URISyntaxException error occurs
     */
    private void saveIndexMapToFile() throws IOException, URISyntaxException {
        loadIndexMapAndContentOffsetIfNeeded();
        if (indexMap != null && !indexMap.isEmpty()) {

            byte[] metadataBytes = (fileContentOffset + OFFSET_SEPARATOR + FileSystemUtil.serializeMap(indexMap)).getBytes();

            //In case we reached the maximum of allocated space for metadata.
            // we double the allocated space to fit the metadata and shift the content.
            if (metadataBytes.length > fileContentOffset) {
                //increase the offset to 2x
                long previousOffset = fileContentOffset;
                fileContentOffset += fileContentOffset;

                //shift all content by fileContentOffset
                shiftFilesContentForward(previousOffset, previousOffset);

                //recalculate all indexes since we shifted the content
                for (Map.Entry<String, FileMetaData> entry : indexMap.entrySet()) {
                    long filePosition = entry.getValue().startFilePosition();
                    entry.setValue(new FileMetaData(filePosition + previousOffset, entry.getValue().fileLength()));
                }
            }

            //save new metadata
            metadataBytes = (fileContentOffset + OFFSET_SEPARATOR + FileSystemUtil.serializeMap(indexMap)).getBytes();
            var fileSystemPath = getSystemFilePath();

            try (RandomAccessFile systemFile = new RandomAccessFile(fileSystemPath, "rw")) {
                systemFile.seek(0);

                systemFile.write(metadataBytes);
            }
        }
    }

    /**
     * Loads (if didn't before) the index map and the map offset from the system file.
     * @throws IOException  If an I/O error occurs
     * @throws URISyntaxException  If URISyntaxException error occurs
     */
    private void loadIndexMapAndContentOffsetIfNeeded() throws IOException, URISyntaxException {
        //if it's not empty that means that the map was already loaded.
        if (indexMap == null) {
            //load from the system file
            var resourceSystemFile = getSystemFilePath();
            try (BufferedReader reader = new BufferedReader(new FileReader(resourceSystemFile))) {
                // Read the first line
                String firstLine = reader.readLine();

                if (firstLine != null) {
                    String[] lineArray = firstLine.split(OFFSET_SEPARATOR);
                    if (lineArray.length == 2) {
                        fileContentOffset = Long.parseLong(lineArray[0]);
                        indexMap = FileSystemUtil.deserializeMap(lineArray[1]);
                    }
                }

                if (indexMap == null) {
                    indexMap = new HashMap<>();
                }
            }
        }
        fileContentOffset = Math.max(fileContentOffset, INITIAL_FILE_CONTENT_OFFSET);
    }

    private void removeFromIndexMapAndRecalculateIndexes(String fileToRemove) throws IOException, URISyntaxException {
        loadIndexMapAndContentOffsetIfNeeded();

        //remove metadata
        deleteIndexMap();

        var deletedMetaData = indexMap.remove(fileToRemove);
        for (Map.Entry<String, FileMetaData> entry : indexMap.entrySet()) {
            //recalculate all indexes of files which are after the deleted file
            long filePosition = entry.getValue().startFilePosition();
            long deletedLength = deletedMetaData.fileLength() + fileToRemove.getBytes().length + 2L *System.lineSeparator().getBytes().length;
            if (filePosition > deletedMetaData.startFilePosition()) {
                entry.setValue(new FileMetaData(filePosition - deletedLength, entry.getValue().fileLength()));
            }
        }
        //save updated metadata
        saveIndexMapToFile();
    }

    private void deleteIndexMap() throws IOException, URISyntaxException {
        var systemFilePath = getSystemFilePath();
        try (RandomAccessFile systemFile = new RandomAccessFile(systemFilePath, "rw")) {
            systemFile.seek(0);

            //fill with empty bytes [0..fileContentOffset)
            for (int i = 0; i < fileContentOffset; i++) {
                systemFile.write(0);
            }
        }
    }

    private void shiftFilesContentForward(long shiftStartPosition, long shiftLength) throws IOException, URISyntaxException {
        var systemFilePath = getSystemFilePath();
        try (RandomAccessFile systemFile = new RandomAccessFile(systemFilePath, "rw")) {

            // Set the file pointer to the end of the file
            long currentPosition = systemFile.length();
            systemFile.seek(currentPosition);

            //todo
            byte[] buffer = new byte[2];
            int bytesRead;

            // Start from the end of the file and move content to the new position
            while (currentPosition > shiftStartPosition) {
                long readPosition = currentPosition - buffer.length;
                long writePosition = readPosition + shiftLength;

                // Read content from the old position
                systemFile.seek(readPosition);
                bytesRead = systemFile.read(buffer);

                // Write content to the new position
                systemFile.seek(writePosition);
                systemFile.write(buffer, 0, bytesRead);

                // Update the file length for the next iteration
                currentPosition = readPosition;
            }
        }
    }

    private void shiftFilesContentBackward(long shiftStartPosition, long shiftLength) throws IOException, URISyntaxException {
        var systemFilePath = getSystemFilePath();
        try (RandomAccessFile systemFile = new RandomAccessFile(systemFilePath, "rw")) {
            long currentPosition = shiftStartPosition;
            //long endPosition = currentPosition + shiftLength;
            long endPosition = systemFile.length();

            //todo
            byte[] buffer = new byte[2];
            int bytesRead;

            while (currentPosition < endPosition) {
                // Read content from the current position
                systemFile.seek(currentPosition);
                bytesRead = systemFile.read(buffer);

                // Calculate the new position to write the content
                long writePosition = currentPosition - shiftLength;

                // Write content to the new position
                systemFile.seek(writePosition);
                systemFile.write(buffer, 0, bytesRead);

                // Move to the next position
                currentPosition += bytesRead;
            }
            systemFile.setLength(systemFile.length() - shiftLength);
        }
    }

    private String getSystemFilePath() throws URISyntaxException {
        ClassLoader classLoader = ContentFileServiceImpl.class.getClassLoader();
        var resourceSystemFile = classLoader.getResource(FILE_SYSTEM_PATH);

        //somebody deleted the system file:
        if (resourceSystemFile == null) {
            throw new RuntimeException(String.format(INTERNAL_ERROR_SYSTEM_FILE_DOES_NOT_EXIST_MESSAGE, FILE_SYSTEM_PATH));
        }

        return Paths.get(resourceSystemFile.toURI()).toString();
    }

}