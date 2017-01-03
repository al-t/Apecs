package com.apecs.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Алексей on 03.01.2017.
 */

public class ApecsProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int PRODUCERS = 100;
    static final int PRODUCERS_IN_CATEGORY = 101;
    static final int CATEGORIES_IN_PARENT_CATEGORY = 200;
    static final int MODELS_IN_CATEGORY_AND_PRODUCER = 300;
    static final int GOODS_IN_CATEGORY_AND_PRODUCER_AND_MODEL = 400;
    static final int GOODS_ITEM = 401;
    static final int GOODS_PROPERTIES = 500;

    private static final SQLiteQueryBuilder sGoodsWithProducersNamesQueryBuilder;
    private static final SQLiteQueryBuilder sGoodsWithModelsNamesQueryBuilder;

    static {
        sGoodsWithProducersNamesQueryBuilder = new SQLiteQueryBuilder();
        sGoodsWithModelsNamesQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //goods INNER JOIN producers ON goods.producer_id = producers._id
        sGoodsWithProducersNamesQueryBuilder.setTables(
                DbContract.GoodsEntry.TABLE_NAME + " INNER JOIN " +
                        DbContract.ProducersEntry.TABLE_NAME +
                        " ON " + DbContract.GoodsEntry.TABLE_NAME +
                        "." + DbContract.GoodsEntry.COLUMN_CATEGORY_ID +
                        " = " + DbContract.ProducersEntry.TABLE_NAME +
                        "." + DbContract.ProducersEntry._ID);

        //This is an inner join which looks like
        //goods INNER JOIN models ON goods.models_id = models._id
        sGoodsWithModelsNamesQueryBuilder.setTables(DbContract.GoodsEntry.TABLE_NAME + " INNER " +
                "JOIN " + DbContract.ModelsEntry.TABLE_NAME + " ON " + DbContract.GoodsEntry
                .TABLE_NAME + "." + DbContract.GoodsEntry.COLUMN_MODEL_ID + " = " + DbContract
                .ModelsEntry.TABLE_NAME + "." + DbContract.ModelsEntry._ID);
    }



    // goods.category_id = ?
    private static final String sGoodsByCategorySelection = DbContract.GoodsEntry.TABLE_NAME
            + "." +DbContract.GoodsEntry.COLUMN_CATEGORY_ID + " = ? ";

    // categories.parent_id = ?
    private static final String sCategoriesInParentCategorySelection = DbContract.CategoriesEntry
            .TABLE_NAME + "." + DbContract.CategoriesEntry.COLUMN_PARENT_ID + " = ? ";

    // goods.category_id = ? and goods.producer_id = ?
    private static final String sGoodsByCategoryProducerSeleection = sGoodsByCategorySelection +
            " AND " + DbContract.GoodsEntry.TABLE_NAME + "."
            + DbContract.GoodsEntry.COLUMN_PRODUCER_ID + " = ? ";

    // goods.category_id = ? and goods.producer_id = ? and goods.model_id = ?
    private static final String sGoodsByCategoryProducerModelSelection =
            sGoodsByCategoryProducerSeleection + " AND " + DbContract.GoodsEntry.TABLE_NAME + "."
                    + DbContract.GoodsEntry.COLUMN_MODEL_ID + " = ? ";


    private Cursor getProducersInCategory(Uri uri, String[] projection, String sortOrder) {
        // Uri: producers/#
        int categoryId = Integer.parseInt(uri.getPathSegments().get(1));

        String selection = sGoodsByCategorySelection;
        String[] selectionArgs = new String[]{Integer.toString(categoryId)};

        return sGoodsWithProducersNamesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    // надо переделать, чтобы показывать только те категории, где есть товары
    private Cursor getCategoriesInParentCategory(Uri uri, String[] projection, String sortOrder) {
        // Uri: categories/#
        int categoryId = Integer.parseInt(uri.getPathSegments().get(1));

        String selection = sCategoriesInParentCategorySelection;
        String[] selectionArgs = new String[]{Integer.toString(categoryId)};

        return mOpenHelper.getReadableDatabase().query(
                DbContract.CategoriesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getModelsInCategoryAndProducer(Uri uri, String[] projection, String sortOrder) {
        // Uri: models/#/#
        String categoryId = uri.getPathSegments().get(1);
        String producerId = uri.getPathSegments().get(2);

        String selection = sGoodsByCategoryProducerSeleection;
        String[] selectionArgs = new String[]{categoryId, producerId};

        return sGoodsWithModelsNamesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getGoodsByCategoryProducerModel(Uri uri, String[] projection, String sortOrder) {
        // Uri: goods/#/#/#
        String categoryId = uri.getPathSegments().get(1);
        String producerId = uri.getPathSegments().get(2);
        String modelId = uri.getPathSegments().get(3);

        String selection = sGoodsByCategoryProducerModelSelection;
        String[] selectionArgs = new String[]{categoryId, producerId, modelId};

        return mOpenHelper.getReadableDatabase().query(
                DbContract.GoodsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DbContract.PATH_PRODUCERS, PRODUCERS);
        matcher.addURI(authority, DbContract.PATH_PRODUCERS + "/#", PRODUCERS_IN_CATEGORY);
        matcher.addURI(authority, DbContract.PATH_CATEGORIES + "/#", CATEGORIES_IN_PARENT_CATEGORY);
        matcher.addURI(authority, DbContract.PATH_MODELS + "/#/#", MODELS_IN_CATEGORY_AND_PRODUCER);
        matcher.addURI(authority, DbContract.PATH_GOODS + "/#/#/#",
                GOODS_IN_CATEGORY_AND_PRODUCER_AND_MODEL);
        matcher.addURI(authority, DbContract.PATH_GOODS + "/#", GOODS_ITEM);
        matcher.addURI(authority, DbContract.PATH_GOODS_PROPS + "/#", GOODS_PROPERTIES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "producers"
            case PRODUCERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DbContract.ProducersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "producers/#"
            case PRODUCERS_IN_CATEGORY: {
                retCursor = getProducersInCategory(uri, projection, sortOrder);
                break;
            }
            // "categories/#"
            case CATEGORIES_IN_PARENT_CATEGORY: {
                retCursor = getCategoriesInParentCategory(uri, projection, sortOrder);
                break;
            }
            // "models/#/#"
            case MODELS_IN_CATEGORY_AND_PRODUCER: {
                retCursor = getModelsInCategoryAndProducer(uri, projection, sortOrder);
            }
            case GOODS_IN_CATEGORY_AND_PRODUCER_AND_MODEL: {
                retCursor = getGoodsByCategoryProducerModel(uri, projection, sortOrder);
            }
        }

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case PRODUCERS:
                return DbContract.ProducersEntry.CONTENT_ITEM_TYPE;
            case PRODUCERS_IN_CATEGORY:
                return DbContract.ProducersEntry.CONTENT_ITEM_TYPE;
            case CATEGORIES_IN_PARENT_CATEGORY:
                return DbContract.CategoriesEntry.CONTENT_TYPE;
            case MODELS_IN_CATEGORY_AND_PRODUCER:
                return DbContract.ModelsEntry.CONTENT_TYPE;
            case GOODS_ITEM:
                return DbContract.GoodsEntry.CONTENT_TYPE;
            case GOODS_IN_CATEGORY_AND_PRODUCER_AND_MODEL:
                return DbContract.GoodsEntry.CONTENT_TYPE;
            case GOODS_PROPERTIES:
                return DbContract.GoodsPropsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
