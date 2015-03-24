package com.confidant.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/3/17.
 */
public class FileUtil {

    public static int BUFFER_SIZE = 10240, MAX_FILE_SIZE = 5 * 1024 * 1024;

    public static void copyFile(InputStream in, String file) throws IOException {
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];
        FileOutputStream fos = new FileOutputStream(file);
        while ((size = in.read(buffer)) > 0) {
            fos.write(buffer, 0, size);
        }
        fos.flush();
        fos.close();
    }

    public static void uploadFile() throws IOException {

    }
}
