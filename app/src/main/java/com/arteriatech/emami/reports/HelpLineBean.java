package com.arteriatech.emami.reports;

/**
 * Created by e10526 on 15-04-2016.
 */
public class HelpLineBean {
    private  String TextCategoryID="";
    private String TextCategoryTypeID="";
    private String TextCategoryDesc="";
    private String TextCategoryTypeDesc="";

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    private String Text="";


    public String getTextCategoryTypeDesc() {
        return TextCategoryTypeDesc;
    }

    public void setTextCategoryTypeDesc(String textCategoryTypeDesc) {
        TextCategoryTypeDesc = textCategoryTypeDesc;
    }

    public String getTextCategoryDesc() {
        return TextCategoryDesc;
    }

    public void setTextCategoryDesc(String textCategoryDesc) {
        TextCategoryDesc = textCategoryDesc;
    }

    public String getTextCategoryTypeID() {
        return TextCategoryTypeID;
    }

    public void setTextCategoryTypeID(String textCategoryTypeID) {
        TextCategoryTypeID = textCategoryTypeID;
    }

    public String getTextCategoryID() {
        return TextCategoryID;
    }

    public void setTextCategoryID(String textCategoryID) {
        TextCategoryID = textCategoryID;
    }



}
