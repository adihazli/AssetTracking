package com.sato.satoats.RecyclerViewModelClass;

public class RecyclerViewLayoutModel {

    public static int TYPE_WIDTH = 0;
    //only use if not in scrollview
    public static int TYPE_WEIGHT = 1;

    String strType;
    Object layout1;
    Object layout2;
    Object layout3;
    Object layout4;

    public RecyclerViewLayoutModel() {
    }

    public RecyclerViewLayoutModel(String strType, Object layout1, Object layout2, Object layout3) {
        this.strType = strType;
        this.layout1 = layout1;
        this.layout2 = layout2;
        this.layout3 = layout3;
    }

    public RecyclerViewLayoutModel(String strType, Object layout1, Object layout2, Object layout3, Object layout4) {
        this.strType = strType;
        this.layout1 = layout1;
        this.layout2 = layout2;
        this.layout3 = layout3;
        this.layout4 = layout4;
    }

    public RecyclerViewLayoutModel(Object layout1, Object layout2, Object layout3, Object layout4) {
        this.layout1 = layout1;
        this.layout2 = layout2;
        this.layout3 = layout3;
        this.layout4 = layout4;
    }

    public RecyclerViewLayoutModel(Object layout1, Object layout2, Object layout3) {
        this.layout1 = layout1;
        this.layout2 = layout2;
        this.layout3 = layout3;
    }

    public String getStrType() {
        return strType;
    }

    public void setStrType(String strType) {
        this.strType = strType;
    }

    public Object getLayout1() {
        return layout1;
    }

    public void setLayout1(Object layout1) {
        this.layout1 = layout1;
    }

    public Object getLayout2() {
        return layout2;
    }

    public void setLayout2(Object layout2) {
        this.layout2 = layout2;
    }

    public Object getLayout3() {
        return layout3;
    }

    public void setLayout3(Object layout3) {
        this.layout3 = layout3;
    }

    public Object getLayout4() {
        return layout4;
    }

    public void setLayout4(Object layout4) {
        this.layout4 = layout4;
    }
}
