package com.arteriatech.emami.expense;

import java.io.Serializable;

/**
 * Created by e10769 on 07-03-2017.
 */

public class ExpenseImageBean implements Serializable {
    private String imagePath="";
    private String FileName="";
    private String imageExtensions="";
    private String DocumentMimeType="";
    private String DocumentSize="";
    private boolean isImageFromMedia=false;
    private boolean isNewImage=false;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getDocumentMimeType() {
        return DocumentMimeType;
    }

    public void setDocumentMimeType(String documentMimeType) {
        DocumentMimeType = documentMimeType;
    }

    public String getDocumentSize() {
        return DocumentSize;
    }

    public void setDocumentSize(String documentSize) {
        DocumentSize = documentSize;
    }

    public String getImageExtensions() {
        return imageExtensions;
    }

    public void setImageExtensions(String imageExtensions) {
        this.imageExtensions = imageExtensions;
    }

    public boolean isImageFromMedia() {
        return isImageFromMedia;
    }

    public void setImageFromMedia(boolean imageFromMedia) {
        isImageFromMedia = imageFromMedia;
    }

    public boolean isNewImage() {
        return isNewImage;
    }

    public void setNewImage(boolean newImage) {
        isNewImage = newImage;
    }
}
