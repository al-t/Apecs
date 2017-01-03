package com.apecs.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PorterDuff;

import com.apecs.app.data.DbContract.ProducersEntry;
import com.apecs.app.data.DbContract.CategoriesEntry;
import com.apecs.app.data.DbContract.GoodsEntry;
import com.apecs.app.data.DbContract.ModelsEntry;
import com.apecs.app.data.DbContract.PropertiesEntry;
import com.apecs.app.data.DbContract.GoodsPropsEntry;

/**
 * Created by Алексей on 03.01.2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "apecs.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold producers.
        final String SQL_CREATE_PRODUCERS_TABLE = "CREATE TABLE " + ProducersEntry.TABLE_NAME + " " +
                "(" +
                ProducersEntry._ID + " INTEGER PRIMARY KEY," +
                ProducersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                " );";

        // Create a table to hold categories.
        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CategoriesEntry.TABLE_NAME +
                " " +
                "(" +
                CategoriesEntry._ID + " INTEGER PRIMARY KEY," +
                CategoriesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                CategoriesEntry.COLUMN_PARENT_ID + " INTEGER NOT NULL, " +
                " );";

        // Create a table to hold models.
        final String SQL_CREATE_MODELS_TABLE = "CREATE TABLE " + ModelsEntry.TABLE_NAME +
                " " +
                "(" +
                ModelsEntry._ID + " INTEGER PRIMARY KEY," +
                ModelsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ModelsEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ModelsEntry.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +
                ModelsEntry.COLUMN_PRODUCER_ID + " INTEGER NOT NULL, " +

                // Set up the category id column as a foreign key to categories table.
                " FOREIGN KEY (" + ModelsEntry.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoriesEntry.TABLE_NAME + " (" + CategoriesEntry._ID + "), " +

                // Set up the producer id column as a foreign key to categories table.
                " FOREIGN KEY (" + ModelsEntry.COLUMN_PRODUCER_ID + ") REFERENCES " +
                ProducersEntry.TABLE_NAME + " (" + ProducersEntry._ID + "), " +
                " );";

        // Create a table to hold goods.
        final String SQL_CREATE_GOODS_TABLE = "CREATE TABLE " + GoodsEntry.TABLE_NAME +
                " " +
                "(" +
                GoodsEntry._ID + " INTEGER PRIMARY KEY," +
                GoodsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                GoodsEntry.COLUMN_PRICE + " FLOAT, " +
                GoodsEntry.COLUMN_ITEM_NUMBER + " TEXT," +
                GoodsEntry.COLUMN_AMOUNT + " INT," +
                GoodsEntry.COLUMN_MINIBOX + " INT," +
                GoodsEntry.COLUMN_IMG + " TEXT," +
                GoodsEntry.COLUMN_NOVELTY + " INT," +
                GoodsEntry.COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +
                GoodsEntry.COLUMN_PRODUCER_ID + " INTEGER NOT NULL, " +
                GoodsEntry.COLUMN_MODEL_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + GoodsEntry.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoriesEntry.TABLE_NAME + " (" + CategoriesEntry._ID + "), " +

                " FOREIGN KEY (" + GoodsEntry.COLUMN_PRODUCER_ID + ") REFERENCES " +
                ProducersEntry.TABLE_NAME + " (" + ProducersEntry._ID + "), " +

                " FOREIGN KEY (" + GoodsEntry.COLUMN_MODEL_ID + ") REFERENCES " +
                ModelsEntry.TABLE_NAME + " (" + ModelsEntry._ID + "), " +
                " );";

        // Create a table to hold properties.
        final String SQL_CREATE_PROPERTIES_TABLE = "CREATE TABLE " + PropertiesEntry.TABLE_NAME +
                " " +
                "(" +
                PropertiesEntry._ID + " INTEGER PRIMARY KEY," +
                PropertiesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PropertiesEntry.COLUMN_TYPE + " TEXT, " +
                " );";

        // Create a table to hold goods.
        final String SQL_CREATE_GOODS_PROPERTIES_TABLE = "CREATE TABLE " + GoodsPropsEntry.TABLE_NAME +
                " " +
                "(" +
                GoodsPropsEntry.COLUMN_GOODS_ID + " INTEGER PRIMARY KEY," +
                GoodsPropsEntry.COLUMN_PROPERTY_ID + " INTEGER PRIMARY KEY," +
                GoodsPropsEntry.COLUMN_VALUE + " TEXT ," +

                " FOREIGN KEY (" + GoodsPropsEntry.COLUMN_GOODS_ID + ") REFERENCES " +
                GoodsEntry.TABLE_NAME + " (" + CategoriesEntry._ID + "), " +

                " FOREIGN KEY (" + GoodsPropsEntry.COLUMN_PROPERTY_ID + ") REFERENCES " +
                PropertiesEntry.TABLE_NAME + " (" + ProducersEntry._ID + "), " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MODELS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GOODS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PROPERTIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GOODS_PROPERTIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProducersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoriesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ModelsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GoodsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PropertiesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GoodsPropsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
