package com.arteriatech.emami.mbo;

/**
 * Created by e10769 on 29-06-2017.
 */

public class ValueHelpBean {
    private String ID = "";
    private String Description = "";
    private String IsDefault = "";
    private String TypeValue = "";
    private String ParentID = "";
    private String displayData = "";
    private String PropName = "";


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(String isDefault) {
        IsDefault = isDefault;
    }

    public String getTypeValue() {
        return TypeValue;
    }

    public void setTypeValue(String typeValue) {
        TypeValue = typeValue;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    @Override
    public String toString() {
        return displayData.toString();
    }

    public String getDisplayData() {
        return displayData;
    }

    public void setDisplayData(String displayData) {
        this.displayData = displayData;
    }

    public String getPropName() {
        return PropName;
    }

    public void setPropName(String propName) {
        PropName = propName;
    }
}
