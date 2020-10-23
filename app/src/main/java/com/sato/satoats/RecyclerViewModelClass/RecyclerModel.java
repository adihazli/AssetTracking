package com.sato.satoats.RecyclerViewModelClass;

import java.util.ArrayList;

public class RecyclerModel {
    ArrayList<String> arrayList;

    DynaTextModel textModel;

    public RecyclerModel(ArrayList arrayList) {
        this.arrayList = arrayList;
    }

    public RecyclerModel(ArrayList<String> arrayList, DynaTextModel textModel) {
        this.arrayList = arrayList;
        this.textModel = textModel;
    }

    public ArrayList getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public DynaTextModel getTextModel() {
        return textModel;
    }

    public void setTextModel(DynaTextModel textModel) {
        this.textModel = textModel;
    }

}
