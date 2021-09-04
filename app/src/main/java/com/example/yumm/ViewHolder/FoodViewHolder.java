package com.example.yumm.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.yumm.Database;
import com.example.yumm.Interface.ItemClickListener;
import com.example.yumm.Model.Food;
import com.example.yumm.Model.Order;
import com.example.yumm.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name,food_price;

    private ItemClickListener itemClickListener;

    Button mButton;
    ElegantNumberButton mNumberBtn;

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name = (TextView) itemView.findViewById(R.id.food_name);
        food_price = (TextView) itemView.findViewById(R.id.food_price);

        mButton = (Button) itemView.findViewById(R.id.add_button);
        mNumberBtn = (ElegantNumberButton) itemView.findViewById(R.id.number_button);

        itemView.setOnClickListener(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(view.getContext()).addToCart(new Order(
                        String.valueOf(getAdapterPosition()),
                        food_name.getText().toString(),
                        mNumberBtn.getNumber(),
                        food_price.getText().toString(),
                        ""
                ));
                Toast.makeText(view.getContext(),"Added to cart",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setItemClickListener(ItemClickListener  itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

}
