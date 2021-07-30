package com.arteriatech.emami.mbo;


/**
 * Created by e10763 on 3/2/2017.
 */

public class DocumentsBean {

    private String DocumentID;
    private String DocumentStore;
    private String Application;
    private String DocumentLink;
    private String DocumentMimeType = "";
    private String FileName;

    public String getDocumentMimeType() {
        return DocumentMimeType;
    }

    public void setDocumentMimeType(String documentMimeType) {
        DocumentMimeType = documentMimeType;
    }


    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    private String mediaLink = "";

    public DocumentsBean(String documentID) {
        super();
        this.DocumentID = documentID;
    }


    public String getDocumentID() {
        return DocumentID;
    }

    public void setDocumentID(String documentID) {
        DocumentID = documentID;
    }

    public String getDocumentStore() {
        return DocumentStore;
    }

    public void setDocumentStore(String documentStore) {
        DocumentStore = documentStore;
    }

    public String getApplication() {
        return Application;
    }

    public void setApplication(String application) {
        Application = application;
    }

    public String getDocumentLink() {
        return DocumentLink;
    }

    public void setDocumentLink(String documentLink) {
        DocumentLink = documentLink;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }


}
