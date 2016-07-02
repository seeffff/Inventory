package com.joedephillipo.inventory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> implements ListAdapter{


    public static class ViewHolder{
        TextView itemName;
        TextView itemQuantity;
        TextView itemPrice;
        Button quickSaleButton;
    }

    //Constructor
    public ItemAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        //Gets item at current position
        final Item item = getItem(position);
        final long id = item.getItemId();

        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_row, parent, false);

            //Initializes the views in the list item
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.item_name_textview);
            viewHolder.itemQuantity = (TextView) convertView.findViewById(R.id.item_quantity_textview);
            viewHolder.itemPrice = (TextView) convertView.findViewById(R.id.item_price_textview);
            viewHolder.quickSaleButton = (Button) convertView.findViewById(R.id.item_quick_sale);

            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Sets the values to the list item
        viewHolder.itemName.setText(item.getItemName());
        viewHolder.itemQuantity.setText(item.getItemQuantity());
        viewHolder.itemPrice.setText(item.getItemPrice());

        //Handles the quick sale button
        viewHolder.quickSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Gets old quantity and subtracts one from it
                String quantityToChange = item.getItemQuantity();
                int quantityOld = Integer.parseInt(quantityToChange);
                int newQuantity = quantityOld - 1;
                String quantityChanged = Integer.toString(newQuantity);

                //Initialize the database
                ItemDbAdapter dbAdapter = new ItemDbAdapter(getContext());

                //Open database, update the item and close it
                dbAdapter.open();
                dbAdapter.updateItem(id, quantityChanged);
                dbAdapter.close();

                //Refresh to show update
                Intent i = new Intent(getContext(), MainActivity.class);
                v.getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
