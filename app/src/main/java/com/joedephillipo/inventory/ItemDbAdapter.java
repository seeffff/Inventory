package com.joedephillipo.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;

public class ItemDbAdapter {

    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 1;
    public static final String ITEM_TABLE = "item";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_PIC = "picture";
    public static final String COLUMN_DATE = "date";

    //Array list of colums in the table
    public String[] allColumns = {COLUMN_ID, COLUMN_NAME, COLUMN_QUANTITY, COLUMN_PRICE, COLUMN_PIC, COLUMN_DATE};

    //String used to create the table
    public static final String CREATE_TABLE_ITEM = " create table " + ITEM_TABLE + " ( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_QUANTITY + " text not null, "
            + COLUMN_PRICE + " text not null, "
            + COLUMN_PIC + " blob not null, "
            + COLUMN_DATE + " ); ";
    private SQLiteDatabase sqlDB;
    private ItemDbHelper itemDbHelper;
    private Context context;

    public ItemDbAdapter(Context ctx) {
        context = ctx;
    }

    //Opens the database adapter
    public ItemDbAdapter open() throws android.database.SQLException {
        itemDbHelper = new ItemDbHelper(context);
        sqlDB = itemDbHelper.getWritableDatabase();
        return this;
    }

    //Closes the database adapter
    public void close() {
        itemDbHelper.close();
    }

    //Method used to create an item
    public void createTableItem(Item item){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getItemName());
        values.put(COLUMN_QUANTITY, item.getItemQuantity());
        values.put(COLUMN_PRICE, item.getItemPrice());
        values.put(COLUMN_PIC, Utility.getBytes(item.getItemImage()));

        sqlDB.insert(ITEM_TABLE, null, values);
    }

    //Method used to delete an item
    public long deleteItem(long idToDelete) {
        return sqlDB.delete(ITEM_TABLE, COLUMN_ID + " = " + idToDelete, null);
    }

    //Method used to update an item
    public long updateItem(Long idToUpdate, String newQuantity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQuantity);
        values.put(COLUMN_DATE, Calendar.getInstance().getTimeInMillis() + "");
        return sqlDB.update(ITEM_TABLE, values, COLUMN_ID + " = " + idToUpdate, null);
    }

    //Will return all items in the table as an array list
    public ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<Item>();
        Cursor cursor = sqlDB.query(ITEM_TABLE, allColumns, null, null, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            String name = (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            String quantity = (cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY)));
            String price = (cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)));
            Bitmap bitmap = Utility.getPhoto(cursor.getBlob(cursor.getColumnIndex(COLUMN_PIC)));
            Long id = (cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));

            Item item = new Item(name, quantity, price, bitmap, id);
            items.add(item);
        }

        cursor.close();
        return items;

    }

    private static class ItemDbHelper extends SQLiteOpenHelper {

        ItemDbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //Creates table
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ITEM);
        }

        //Upgrades table
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(ItemDbHelper.class.getName(), "upgrading database from version " + newVersion + "to" + oldVersion + ",which will destroy all old data");
            db.execSQL("Drop table if exist " + ITEM_TABLE);
            onCreate(db);
        }
    }
}