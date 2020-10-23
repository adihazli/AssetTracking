package com.sato.satoats.LayoutAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sato.satoats.R;

import java.util.ArrayList;

public class AdapterFiveHeader extends RecyclerView.Adapter<AdapterFiveHeader.CustomHolder> {
    Context context;
    ArrayList<AdapterFiveModel> arrayModel;

    public AdapterFiveHeader() {
    }

    public AdapterFiveHeader(Context context, ArrayList<AdapterFiveModel> arrayModel) {
        this.context = context;
        this.arrayModel = arrayModel;
    }

    public class CustomHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.headerFiveItem1);
            textView2 = itemView.findViewById(R.id.headerFiveItem2);
            textView3 = itemView.findViewById(R.id.headerFiveItem3);
            textView4 = itemView.findViewById(R.id.headerFiveItem4);
            textView5 = itemView.findViewById(R.id.headerFiveItem5);
        }
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CustomHolder customHolder = null;

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_headerfive, viewGroup, false);
        customHolder = new CustomHolder(view);

        return customHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder customHolder, int position) {
        customHolder.textView1.setText(arrayModel.get(position).getLblText1());
        customHolder.textView2.setText(arrayModel.get(position).getLblText2());
        customHolder.textView3.setText(arrayModel.get(position).getLblText3());
        customHolder.textView4.setText(arrayModel.get(position).getLblText4());
        customHolder.textView5.setText(arrayModel.get(position).getLblText5());
    }

    @Override
    public int getItemCount() {
        return arrayModel.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
