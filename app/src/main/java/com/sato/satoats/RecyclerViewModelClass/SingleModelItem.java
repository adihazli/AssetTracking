package com.sato.satoats.RecyclerViewModelClass;

public class SingleModelItem {
    String strText;
    DynaTextModel textModel;
    int showToView;

    public static int toShow = 1;
    public static int toHide = 2;

    public SingleModelItem() {
    }

    public SingleModelItem(String strText) {
        this.strText = strText;
    }

    public SingleModelItem(String strText, DynaTextModel textModel) {
        this.strText = strText;
        this.textModel = textModel;
    }

    public SingleModelItem(String strText, int showToView) {
        this.strText = strText;
        this.showToView = showToView;
    }

    public SingleModelItem(String strText, DynaTextModel textModel, int showToView) {
        this.strText = strText;
        this.textModel = textModel;
        this.showToView = showToView;
    }

    public String getStrText() {
        return strText;
    }

    public void setStrText(String strText) {
        this.strText = strText;
    }

    public DynaTextModel getTextModel() {
        return textModel;
    }

    public void setTextModel(DynaTextModel textModel) {
        this.textModel = textModel;
    }

    public int getShowToView() {
        return showToView;
    }

    public void setShowToView(int showToView) {
        this.showToView = showToView;
    }
}
