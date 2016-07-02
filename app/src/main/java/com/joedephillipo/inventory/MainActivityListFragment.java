package com.joedephillipo.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivityListFragment extends ListFragment{

    //Declare variables used
    private static final int CAMERA_REQUEST = 1888;
    private EditText itemName, itemQuantity, itemPrice;
    private Button addImage;
    private TextView initial;
    private ImageView image = null;
    private ArrayList<Item> items;
    private ItemAdapter itemAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //Initialize the database and retrieve items
        final ItemDbAdapter dbAdapter = new ItemDbAdapter(getActivity().getBaseContext());
        dbAdapter.open();
        items = dbAdapter.getAllItems();
        dbAdapter.close();

        //If there are no items in the list then a text view will be displayed with instructions
        if(items.size() == 0) {
            initial = (TextView) getActivity().findViewById(R.id.initial_text);
            initial.setText("To start adding items, click the Add new item button!");
        } else {

            //Set adapter
            itemAdapter = new ItemAdapter(getActivity(), items);
            setListAdapter(itemAdapter);

            //Create a divider between items
            getListView().setDivider(ContextCompat.getDrawable(getActivity(),
                    android.R.color.black));
            getListView().setDividerHeight(1);

            //Sets view
            registerForContextMenu(getListView());

        }
            //Initialize the add button
            Button addButton = (Button) getActivity().findViewById(R.id.add_button);

            //If the add button is clicked a dialog will display
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildConfirmDialog();
                }
            });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        //Gets the position of the item that was clicked
        Item item = (Item) getListAdapter().getItem(position);

        //Creates an intent to go to the item detail activity
        Intent intent = new Intent(getActivity(), ItemDetailActivity.class);

        //Passes information about the item to the item detail activity
        intent.putExtra(MainActivity.ITEM_ID_EXTRA, item.getItemId());
        intent.putExtra(MainActivity.ITEM_NAME_EXTRA, item.getItemName());
        intent.putExtra(MainActivity.ITEM_QUANTITY_EXTRA, item.getItemQuantity());
        intent.putExtra(MainActivity.ITEM_PRICE_EXTRA, item.getItemPrice());
        intent.putExtra("image", item.getItemImage());
        intent.putExtra("id", item.getItemId());

        //Starts the activity
        startActivity(intent);
    }

    private void buildConfirmDialog(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle("Item to add");

        Context context = confirmBuilder.getContext();
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Input for the item name
        itemName = new EditText(context);
        itemName.setHint("Item Name");
        layout.addView(itemName);

        //Input for the item quantity
        itemQuantity = new EditText(context);
        itemQuantity.setHint("Item Quantity");
        layout.addView(itemQuantity);

        //Input for the item price
        itemPrice = new EditText(context);
        itemPrice.setHint("Item Price");
        layout.addView(itemPrice);

        //Add image button
        addImage = new Button(context);
        addImage.setText("Add image");
        layout.addView(addImage);

        final LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(1000, 1000);
        image = new ImageView(context);
        image.setLayoutParams(layoutParams);

        confirmBuilder.setView(layout);

        //Handles user adding image
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image == null) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra("crop", "true");
                    takePicture.putExtra("outputX", 100);
                    takePicture.putExtra("outputY", 100);
                    takePicture.putExtra("scale", true);
                    takePicture.putExtra("return-data", true);
                    startActivityForResult(takePicture, CAMERA_REQUEST);
                    layout.addView(image);
                } else {
                    layout.removeView(image);
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra("crop", "true");
                    takePicture.putExtra("outputX", 100);
                    takePicture.putExtra("outputY", 100);
                    takePicture.putExtra("scale", true);
                    takePicture.putExtra("return-data", true);
                    startActivityForResult(takePicture, CAMERA_REQUEST);
                    layout.addView(image);
                }
            }
        });

        //The OK button
        confirmBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Gets the input information
                String itemNameStr = itemName.getText().toString();
                String itemQuantityStr = itemQuantity.getText().toString();
                String itemPriceStr = itemPrice.getText().toString();

                //Validates the information
                if(isNumeric(itemQuantityStr) == true && isNumeric(itemPriceStr) == true &&
                        itemQuantityStr.length() != 0 && itemNameStr.length() != 0
                        && itemPriceStr.length() != 0 && image.getDrawable() != null) {
                    ItemDbAdapter dbAdapter = new ItemDbAdapter(getActivity().getBaseContext());

                    Bitmap photo = ((BitmapDrawable)image.getDrawable()).getBitmap();

                    //Creates a new item to add
                    Item item = new Item(itemNameStr, itemQuantityStr, itemPriceStr, photo);

                    //Adds item to the database
                    dbAdapter.open();
                    dbAdapter.createTableItem(item);
                    dbAdapter.close();

                    //Refreshed the main activity
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                } else {

                    //Toast gets displayed if there was an error
                    Toast.makeText(getActivity(),"The values for quantity and/or price were not numeric",Toast.LENGTH_LONG).show();
                }
            }
        });

        //Negative button
        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        confirmBuilder.show();
    }

    //Method to test if a string can be converted to a numeric value
    public static boolean isNumeric(String str){
        try{
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }

    //Handle the add image intent
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);

            //Convert to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
    }
}
