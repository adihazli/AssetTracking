package com.sato.satoats.RecyclerViewModelClass;

public class RecyclerViewColData {
    boolean isSelected;
    String strText1;
    String strText2;
    String strText3;
    String strText4;

    RecyclerViewLayoutModel layoutModel;
    RecyclerViewTextModel textModel;

    public RecyclerViewColData() {
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3, RecyclerViewLayoutModel layoutModel) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
        this.layoutModel = layoutModel;
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3, RecyclerViewTextModel textModel) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
        this.textModel = textModel;
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3, RecyclerViewLayoutModel layoutModel, RecyclerViewTextModel textModel) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
        this.layoutModel = layoutModel;
        this.textModel = textModel;
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3, String strText4) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
        this.strText4 = strText4;
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3, String strText4, RecyclerViewLayoutModel layoutModel) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
        this.strText4 = strText4;
        this.layoutModel = layoutModel;
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3, String strText4, RecyclerViewTextModel textModel) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
        this.strText4 = strText4;
        this.textModel = textModel;
    }

    public RecyclerViewColData(boolean isSelected, String strText1, String strText2, String strText3, String strText4, RecyclerViewLayoutModel layoutModel, RecyclerViewTextModel textModel) {
        this.isSelected = isSelected;
        this.strText1 = strText1;
        this.strText2 = strText2;
        this.strText3 = strText3;
        this.strText4 = strText4;
        this.layoutModel = layoutModel;
        this.textModel = textModel;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStrText1() {
        return strText1;
    }

    public void setStrText1(String strText1) {
        this.strText1 = strText1;
    }

    public String getStrText2() {
        return strText2;
    }

    public void setStrText2(String strText2) {
        this.strText2 = strText2;
    }

    public String getStrText3() {
        return strText3;
    }

    public void setStrText3(String strText3) {
        this.strText3 = strText3;
    }

    public String getStrText4() {
        return strText4;
    }

    public void setStrText4(String strText4) {
        this.strText4 = strText4;
    }

    public RecyclerViewLayoutModel getLayoutModel() {
        return layoutModel;
    }

    public void setLayoutModel(RecyclerViewLayoutModel layoutModel) {
        this.layoutModel = layoutModel;
    }

    public RecyclerViewTextModel getTextModel() {
        return textModel;
    }

    public void setTextModel(RecyclerViewTextModel textModel) {
        this.textModel = textModel;
    }

}
