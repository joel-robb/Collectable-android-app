package com.example.collectable;

public class AddItemField {
    String fieldName, datatype;

    public AddItemField(String fieldName, String datatype) {
        this.fieldName = fieldName;
        this.datatype = datatype;
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
