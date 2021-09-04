package com.example.yumm.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yumm.Cart;
import com.example.yumm.Database;
import com.example.yumm.Interface.ItemDeleteListener;
import com.example.yumm.Model.Order;
import com.example.yumm.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textCartName,textPrice;
    public ImageView imgCartClear;

    public ItemDeleteListener itemDeleteListener;


    public CartViewHolder(@NonNull View itemView, final CartAdapter cartAdapter) {
        super(itemView);
        textCartName = (TextView) itemView.findViewById(R.id.cart_item_name);
        textPrice = (TextView) itemView.findViewById(R.id.cart_item_Price);
        imgCartClear = (ImageView) itemView.findViewById(R.id.cart_item_clear);

        itemDeleteListener = (ItemDeleteListener) itemView.getContext();

        imgCartClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cartAdapter.removeItem(getAdapterPosition());
                //Intent restartCart = new Intent(view.getContext(), Cart.class);
                //view.getContext().startActivity(restartCart);
                //Toast.makeText(view.getContext(),"HI",Toast.LENGTH_SHORT).show();

                itemDeleteListener.onItemDeleted(getAdapterPosition());
            }
        });

    }

    @Override
    public void onClick(View view) {

    }
}

public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData;
    private Context context;

    public int totalPrice = 0;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
        for(Order order : listData)
            totalPrice += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_item,parent,false);
        return new CartViewHolder(itemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Locale locale = new Locale("en","IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.textPrice.setText(fmt.format(price));

        holder.textCartName.setText(listData.get(position).getFoodName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
/*
    public void removeItem(int position) {
        if(listData.size() > 0) {
            totalPrice = totalPrice - Integer.parseInt(listData.get(position).getPrice());
            new Database(context).removeItem(listData.get(position));
            listData.remove(position);
            //notifyDataSetChanged();
        }
    }
*/
    public int getPrice() {
        return totalPrice;
    }

    public Order getItem(int position) {
        return listData.get(position);
    }

}
