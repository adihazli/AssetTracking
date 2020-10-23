package com.sato.satoats.RecyclerViewModelClass;

public class RecyclerViewTextModel {

    int txtColor;
    int txtSize;
    String[] txtStyle;
    int txtStrokeColor;
    int txtSolidColor;

    public RecyclerViewTextModel() {
    }

    public RecyclerViewTextModel(int txtColor, int txtSize) {
        this.txtColor = txtColor;
        this.txtSize = txtSize;
    }

    public RecyclerViewTextModel(int txtColor, int txtSize, int txtStrokeColor, int txtSolidColor) {
        this.txtColor = txtColor;
        this.txtSize = txtSize;
        this.txtStrokeColor = txtStrokeColor;
        this.txtSolidColor = txtSolidColor;
    }

    public RecyclerViewTextModel(int txtColor, int txtSize, String[] txtStyle, int txtStrokeColor, int txtSolidColor) {
        this.txtColor = txtColor;
        this.txtSize = txtSize;
        this.txtStyle = txtStyle;
        this.txtStrokeColor = txtStrokeColor;
        this.txtSolidColor = txtSolidColor;
    }

    public int getTxtColor() {
        return txtColor;
    }

    public void setTxtColor(int txtColor) {
        this.txtColor = txtColor;
    }

    public int getTxtSize() {
        return txtSize;
    }

    public void setTxtSize(int txtSize) {
        this.txtSize = txtSize;
    }

    public String[] getTxtStyle() {
        return txtStyle;
    }

    public void setTxtStyle(String[] txtStyle) {
        this.txtStyle = txtStyle;
    }

    public int getTxtStrokeColor() {
        return txtStrokeColor;
    }

    public void setTxtStrokeColor(int txtStrokeColor) {
        this.txtStrokeColor = txtStrokeColor;
    }

    public int getTxtSolidColor() {
        return txtSolidColor;
    }

    public void setTxtSolidColor(int txtSolidColor) {
        this.txtSolidColor = txtSolidColor;
    }
}
