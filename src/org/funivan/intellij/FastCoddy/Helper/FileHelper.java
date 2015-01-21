package org.funivan.intellij.FastCoddy.Helper;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: funivan
 * Date: 4/4/14
 */
public class FileHelper {

    /**
     * Helper function for reading file
     */
    @Nullable
    public static String getFileContent(String filename) {
        String content = null;
        File file = new File(filename);

        if (!file.exists()) {
            return null;
        }

        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
