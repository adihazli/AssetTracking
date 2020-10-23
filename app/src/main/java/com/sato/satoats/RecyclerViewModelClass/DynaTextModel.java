package com.sato.satoats.RecyclerViewModelClass;

public class DynaTextModel {
    Object textColor;
    Object textSize;

    Object paddingLeft;
    Object paddingRight;
    Object paddingTop;
    Object paddingBottom;

    Object textStyle;

    Object objWidth;
    Object objHeight;

    //Layout params
//    Object objlayoutParamWidth;
//    Object objlayoutParamHeight;

    //Object Class
    DynaLayoutParam layoutParam;
    DynaDrawableModel drawableModel;

    public DynaTextModel() {
    }

    public DynaTextModel(Object textColor, Object textSize) {
        this.textColor = textColor;
        this.textSize = textSize;
    }

    public DynaTextModel(Object paddingLeft, Object paddingRight, Object paddingTop, Object paddingBottom) {
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
    }

    public DynaTextModel(Object textColor, Object textSize, Object paddingLeft, Object paddingRight, Object paddingTop, Object paddingBottom) {
        this.textColor = textColor;
        this.textSize = textSize;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
    }

    public DynaTextModel(Object textColor, Object textSize, Object paddingLeft, Object paddingRight, Object paddingTop, Object paddingBottom, Object textStyle, Object objWidth, Object objHeight, DynaLayoutParam layoutParam) {
        this.textColor = textColor;
        this.textSize = textSize;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.textStyle = textStyle;
        this.objWidth = objWidth;
        this.objHeight = objHeight;
        this.layoutParam = layoutParam;
    }

    public DynaTextModel(Object textColor, Object textSize, Object paddingLeft, Object paddingRight, Object paddingTop, Object paddingBottom, Object textStyle, Object objWidth, Object objHeight, DynaLayoutParam layoutParam, DynaDrawableModel drawableModel) {
        this.textColor = textColor;
        this.textSize = textSize;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.textStyle = textStyle;
        this.objWidth = objWidth;
        this.objHeight = objHeight;
        this.layoutParam = layoutParam;
        this.drawableModel = drawableModel;
    }

    public Object getTextColor() {
        return textColor;
    }

    public void setTextColor(Object textColor) {
        this.textColor = textColor;
    }

    public Object getTextSize() {
        return textSize;
    }

    public void setTextSize(Object textSize) {
        this.textSize = textSize;
    }

    public Object getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(Object paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public Object getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(Object paddingRight) {
        this.paddingRight = paddingRight;
    }

    public Object getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(Object paddingTop) {
        this.paddingTop = paddingTop;
    }

    public Object getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(Object paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public Object getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(Object textStyle) {
        this.textStyle = textStyle;
    }

    public Object getObjWidth() {
        return objWidth;
    }

    public void setObjWidth(Object objWidth) {
        this.objWidth = objWidth;
    }

    public Object getObjHeight() {
        return objHeight;
    }

    public void setObjHeight(Object objHeight) {
        this.objHeight = objHeight;
    }

    public DynaLayoutParam getLayoutParam() {
        return layoutParam;
    }

    public void setLayoutParam(DynaLayoutParam layoutParam) {
        this.layoutParam = layoutParam;
    }

    public DynaDrawableModel getDrawableModel() {
        return drawableModel;
    }

    public void setDrawableModel(DynaDrawableModel drawableModel) {
        this.drawableModel = drawableModel;
    }
}
