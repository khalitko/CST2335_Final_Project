package com.example.cst2335_finalproject.cst2335_final_project.Food;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335_finalproject.cst2335_final_project.R;

import java.util.ArrayList;


public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {

    ArrayList<String> label = new ArrayList<>();
    ArrayList<String> calorieValue = new ArrayList<>();
    ArrayList<String> fatValue = new ArrayList<>();
    ArrayList<String> carbValue = new ArrayList<>();
    Context context;

    public FoodAdapter(Context context, ArrayList<String> label, ArrayList<String> calorieValue, ArrayList<String> fatValue, ArrayList<String> carbValue) {
        this.context = context;
        this.label = label;
        this.calorieValue = calorieValue;
        this.fatValue = fatValue;
        this.carbValue = carbValue;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_info, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.foodLabel.setText(label.get(position));
        holder.caloriesV.setText(calorieValue.get(position));
        holder.fatV.setText(fatValue.get(position));
        holder.carbsV.setText(carbValue.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, label.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return label.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView foodLabel, caloriesV, fatV, carbsV;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            foodLabel = (TextView) itemView.findViewById(R.id.foodLabel);
            caloriesV = (TextView) itemView.findViewById(R.id.caloriesV);
            fatV = (TextView) itemView.findViewById(R.id.fatV);
            carbsV = (TextView) itemView.findViewById(R.id.carbsV);

        }
    }
}