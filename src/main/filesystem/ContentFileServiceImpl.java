package filesystem;

import java.io.*;

public class ContentFileServiceImpl implements ContentFileService{
    private static final int BUFFER_SIZE = 4096;

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
}
