package com.joedephillipo.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailActivity extends AppCompatActivity {

    //Declares variables
    private TextView itemName;
    private TextView itemQuantity;
    private TextView itemPrice;
    private EditText editQuantity;
    private Button quantityButton, deleteButton, orderButton;
    private ImageView itemImage;
    private long noteId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        //Initializes the views
        itemName = (TextView) findViewById(R.id.detail_name);
        itemQuantity = (TextView) findViewById(R.id.detail_quantity);
        itemPrice = (TextView) findViewById(R.id.detail_price);
        quantityButton = (Button) findViewById(R.id.edit_quantity_button);
        deleteButton = (Button) findViewById(R.id.delete_item_button);
        orderButton = (Button) findViewById(R.id.order_button);
        itemImage = (ImageView) findViewById(R.id.item_detail_image);

        //Creates an intent to get the extras
        Intent i = getIntent();
        noteId = i.getExtras().getLong(MainActivity.ITEM_ID_EXTRA, 0);

        //Sets the views with the proper values
        itemName.setText(i.getExtras().getString(MainActivity.ITEM_NAME_EXTRA));
        itemQuantity.setText(i.getExtras().getString(MainActivity.ITEM_QUANTITY_EXTRA));
        itemPrice.setText(i.getExtras().getString(MainActivity.ITEM_PRICE_EXTRA));

        //byteArray = getIntent().getByteArrayExtra("image");
        Bitmap image = i.getExtras().getParcelable("image");

        itemImage.setImageBitmap(image);

        //Delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDeleteDialog();
            }
        });

        //Order button
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creates an implicit intent to write an emai;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "New " + itemName.getText() + " Order");
                i.putExtra(Intent.EXTRA_TEXT   , "I would like to place an order for more " + itemName.getText());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {

                    //Toast that is displayed if no apps can handle it
                    Toast.makeText(getApplication(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button to update the quantity
        quantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildConfirmDialog();
            }
        });
    }

    private void buildConfirmDialog(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("New Quantity");

        final Context context = confirmBuilder.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Creates an input value
        editQuantity = new EditText(this);
        editQuantity.setHint("Quantity");
        layout.addView(editQuantity);

        confirmBuilder.setView(layout);

        //The OK button
        confirmBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Gets the input quantity
                String itemQuantityStr = editQuantity.getText().toString();

                //Validates the input value
                if (isNumeric(itemQuantityStr) == true && itemQuantityStr.length() != 0) {
                    ItemDbAdapter dbAdapter = new ItemDbAdapter(getApplication());

                    //Creates an intent to get the remaining values
                    Intent i = getIntent();

                    //Updates the item in the table
                    dbAdapter.open();
                    dbAdapter.updateItem(i.getExtras().getLong(MainActivity.ITEM_ID_EXTRA), itemQuantityStr);
                    dbAdapter.close();

                    //Intent to return to main activity
                    Intent k = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(k);
                } else {

                    //Toast that's displayed if the quantity value is wrong
                    Toast.makeText(getApplication(), "The quantity entered is ", Toast.LENGTH_LONG).show();
                }
            }
        });

        //The Cancel button
        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //Display the dialog
        confirmBuilder.show();
    }

    private void buildDeleteDialog(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("Are you sure?");
        confirmBuilder.setMessage("Are you sure you want to remove this item?");

        final Context context = confirmBuilder.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        confirmBuilder.setView(layout);

        //The OK button
        confirmBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ItemDbAdapter dbAdapter = new ItemDbAdapter(getBaseContext());

                //Deletes the value from the table
                dbAdapter.open();
                dbAdapter.deleteItem(noteId);
                dbAdapter.close();

                //Creates and starts an intent to go back to the main activity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
    });

        //The Cancel button
        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        //Display the dialog
        confirmBuilder.show();
    }

    //Tests to see if input value can be converted to a number
    public static boolean isNumeric(String str){
        try{
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }
}





