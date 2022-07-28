package com.example.collectable;

public class CollectionFieldItem {
    boolean removable;
    String fieldName, datatype;

    public CollectionFieldItem(boolean removable, String fieldName, String datatype) {
        this.removable = removable;
        this.fieldName = fieldName;
        this.datatype = datatype;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
}
