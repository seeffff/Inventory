package com.joedephillipo.inventory;

import android.graphics.Bitmap;

import java.sql.Blob;

public class Item {

    //Variable contained in the objext
    private String mItemName;
    private String mItemQuantity;
    private String mItemPrice;
    private Bitmap mImage;
    private long mItemId, mItemDateCreated;

    //Constructor
    public Item(String name, String quantity, String price, Bitmap image){
        mItemName = name;
        mItemQuantity = quantity;
        mItemPrice = price;
        mImage = image;
    }

    //Constructor for the database
    public Item(String name, String quantity, String price, Bitmap image, long id){
        mItemName = name;
        mItemQuantity = quantity;
        mItemPrice = price;
        mItemId = id;
        mImage = image;
    }

    //Gets the items name
    public String getItemName(){
        return mItemName;
    }

    //Gets the items quantity
    public String getItemQuantity(){
        return mItemQuantity;
    }

    //Gets the items price
    public String getItemPrice(){
        return mItemPrice;
    }

    public Bitmap getItemImage(){
        return mImage;
    }

    //Gets the items id
    public long getItemId(){
        return mItemId;
    }

    //Gets the date the item was created
    public long getItemDateCreated(){
        return mItemDateCreated;
    }

}
