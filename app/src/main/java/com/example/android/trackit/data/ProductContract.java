package com.example.android.trackit.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class ProductContract {

    private ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.trackit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        //String
        public final static String COLUMN_PRODUCT_NAME = "name";
        //Double
        public final static String COLUMN_PRICE = "price";
        //Integer
        public final static String COLUMN_QUANTITY = "quantity";
        //String
        public final static String COLUMN_SUPPLIER_NAME = "supp_name";
        //String
        public final static String COLUMN_SUPPLIER_PHONE = "supp_phone";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);



        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
    }

}