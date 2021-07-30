package com.arteriatech.emami.mbo;

import java.io.InputStream;

/**
 * Created by e10526 on 13-07-2016.
 */
public class MerchandisingBean {


    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    private String ImagePath = "";

    private static MerchandisingBean instance = null;

    public static MerchandisingBean getInstance() {
        if (instance == null) {
            instance = new MerchandisingBean();
        }
        return instance;
    }

    private String MerchReviewGUID = "";
    private String MerchReviewType = "";
    private String MerchReviewTypeDesc = "";
    private String ImageSize = "";
    private String MerchReviewDate = "";
    private String Remarks = "";
    private String Etag = "";
    private InputStream imgInputStream;

    public InputStream getImgInputStream() {
        return imgInputStream;
    }

    public void setImgInputStream(InputStream imgInputStream) {
        this.imgInputStream = imgInputStream;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    private String mediaLink = "";

    public String getDocumentStore() {
        return DocumentStore;
    }

    public void setDocumentStore(String documentStore) {
        DocumentStore = documentStore;
    }

    private String DocumentStore = "";

    public String getMerchReviewImgGUID() {
        return MerchReviewImgGUID;
    }

    public void setMerchReviewImgGUID(String merchReviewImgGUID) {
        MerchReviewImgGUID = merchReviewImgGUID;
    }

    private String MerchReviewImgGUID = "";

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    private String Image = "";

    public String getResourcePath() {
        return ResourcePath;
    }

    public void setResourcePath(String resourcePath) {
        ResourcePath = resourcePath;
    }

    private String ResourcePath = "";

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }


    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }


    public String getMerchReviewDate() {
        return MerchReviewDate;
    }

    public void setMerchReviewDate(String merchReviewDate) {
        MerchReviewDate = merchReviewDate;
    }

    public String getMerchReviewGUID() {
        return MerchReviewGUID;
    }

    public void setMerchReviewGUID(String merchReviewGUID) {
        MerchReviewGUID = merchReviewGUID;
    }

    public String getMerchReviewType() {
        return MerchReviewType;
    }

    public void setMerchReviewType(String merchReviewType) {
        MerchReviewType = merchReviewType;
    }

    public String getMerchReviewTypeDesc() {
        return MerchReviewTypeDesc;
    }

    public void setMerchReviewTypeDesc(String merchReviewTypeDesc) {
        MerchReviewTypeDesc = merchReviewTypeDesc;
    }

    public String getImageSize() {
        return ImageSize;
    }

    public void setImageSize(String imageSize) {
        ImageSize = imageSize;
    }


}
