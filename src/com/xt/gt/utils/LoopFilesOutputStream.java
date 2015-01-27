package com.xt.gt.utils;

import com.xt.core.utils.VarTemplate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is used to write files.
 *
 * @author Administrator
 */
public class LoopFilesOutputStream extends OutputStream {

    /**
     * The max count of files reserved.
     */
    private int maxFileCount = 5;
    
    /**
     * The max count of files reserved.
     */
    private int maxFileSize = 5 * 1024;
    private int index = 0;
    private String fileNamePattern = "temp-${index}.lf";
    private String fileDir = ".";
    private int byteCount;
    private FileOutputStream currentFile = null;

    @Override
    public void write(int b) throws IOException {
        if (byteCount == 0 && index == 0) {
            changeFile(0);
        }
        if (byteCount >= maxFileSize) {
            index++;
            changeFile(index);
            byteCount = 0;
        }
        byteCount++;
    }

    private void changeFile(int index) throws IOException {
        close();
        int i = (index % maxFileCount);
        String fileName = VarTemplate.format(fileNamePattern, String.valueOf(i));
        File file = new File(fileDir, fileName);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                // throw 
            }
        }
        currentFile = new FileOutputStream(file);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        if (currentFile != null) {
            currentFile.flush();
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (currentFile != null) {
            currentFile.close();
        }
    }
}
