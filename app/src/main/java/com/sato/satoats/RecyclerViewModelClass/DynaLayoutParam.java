package com.sato.satoats.RecyclerViewModelClass;

public class DynaLayoutParam {

    Object objLayoutParamWidth;
    Object objLayoutParamHeight;

    Object objLayoutParamPosition;

    public DynaLayoutParam() {
    }

    public DynaLayoutParam(Object objLayoutParamWidth, Object objLayoutParamHeight) {
        this.objLayoutParamWidth = objLayoutParamWidth;
        this.objLayoutParamHeight = objLayoutParamHeight;
    }

    public Object getObjLayoutParamWidth() {
        return objLayoutParamWidth;
    }

    public void setObjLayoutParamWidth(Object objLayoutParamWidth) {
        this.objLayoutParamWidth = objLayoutParamWidth;
    }

    public Object getObjLayoutParamHeight() {
        return objLayoutParamHeight;
    }

    public void setObjLayoutParamHeight(Object objLayoutParamHeight) {
        this.objLayoutParamHeight = objLayoutParamHeight;
    }

}