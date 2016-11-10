package org.funivan.intellij.FastCoddy.Helper;


import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Ivan Scherbak <dev@funivan>
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
