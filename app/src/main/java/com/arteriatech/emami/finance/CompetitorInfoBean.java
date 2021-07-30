package com.arteriatech.emami.finance;

/**
 * Created by e10604 on 29/4/2016.
 */
public class CompetitorInfoBean {


    public String getCompetitorName() {
        return competitorName;
    }

    public void setCompetitorName(String competitorName) {
        this.competitorName = competitorName;
    }

    public String getCompetitorGUID() {
        return competitorGUID;
    }

    public void setCompetitorGUID(String competitorGUID) {
        this.competitorGUID = competitorGUID;
    }

    String competitorName="";
    String competitorGUID="";

    public String getUpdatedOn() {
        return UpdatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        UpdatedOn = updatedOn;
    }

    private String UpdatedOn="";

    private  String MatGrp1Amount="";
    private  String MatGrp2Amount="";
    private  String MatGrp3Amount="";
    private  String MatGrp4Amount="";
    private  String SchemeAmount="";
    private  String Earnings="";
    private String Etag = "";
    private String ResourcePath = "";
    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }



    public String getResourcePath() {
        return ResourcePath;
    }

    public void setResourcePath(String resourcePath) {
        ResourcePath = resourcePath;
    }



    public String getEarnings() {
        return Earnings;
    }

    public void setEarnings(String earnings) {
        Earnings = earnings;
    }

    public String getMatGrp1Amount() {
        return MatGrp1Amount;
    }

    public void setMatGrp1Amount(String matGrp1Amount) {
        MatGrp1Amount = matGrp1Amount;
    }

    public String getMatGrp2Amount() {
        return MatGrp2Amount;
    }

    public void setMatGrp2Amount(String matGrp2Amount) {
        MatGrp2Amount = matGrp2Amount;
    }

    public String getMatGrp3Amount() {
        return MatGrp3Amount;
    }

    public void setMatGrp3Amount(String matGrp3Amount) {
        MatGrp3Amount = matGrp3Amount;
    }

    public String getMatGrp4Amount() {
        return MatGrp4Amount;
    }

    public void setMatGrp4Amount(String matGrp4Amount) {
        MatGrp4Amount = matGrp4Amount;
    }

    public String getSchemeAmount() {
        return SchemeAmount;
    }

    public void setSchemeAmount(String schemeAmount) {
        SchemeAmount = schemeAmount;
    }



}
