package com.sato.satoats.RecyclerViewModelClass;

public class DynaDrawableModel {

    Object textStrokeColor;
    Object textSolidColor;

    public DynaDrawableModel() {
    }

    public DynaDrawableModel(Object textStrokeColor, Object textSolidColor) {
        this.textStrokeColor = textStrokeColor;
        this.textSolidColor = textSolidColor;
    }

    public Object getTextStrokeColor() {
        return textStrokeColor;
    }

    public void setTextStrokeColor(Object textStrokeColor) {
        this.textStrokeColor = textStrokeColor;
    }

    public Object getTextSolidColor() {
        return textSolidColor;
    }

    public void setTextSolidColor(Object textSolidColor) {
        this.textSolidColor = textSolidColor;
    }

}