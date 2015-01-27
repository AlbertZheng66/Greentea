/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.proxy.impl.http.json;

import java.io.InputStream;

/**
 *
 * @author albert
 */
public class UploadedFile {

    private String fieldName;
    private String fileName;
    private String contentType;
    private long sizeInBytes;
    private InputStream inputStream;

    public UploadedFile(String fieldName, String fileName, String contentType, long sizeInBytes) {
        this.fieldName = fieldName;
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeInBytes = sizeInBytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}

