package com.example.android.trackit;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.trackit.data.ProductContract.ProductEntry;
import com.example.android.trackit.data.ProductDbHelper;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private ProductDbHelper dbHelper;
    ProductCursorAdapter cursorAdapter;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ProductDbHelper(this);


        ListView productListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        cursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(cursorAdapter);


        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_new_product:
                insertProduct();
                return true;

            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;

            case R.id.action_insert_dummy_data:
                insertDummyProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
        };

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    private void insertDummyProduct() {

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Computer");
        values.put(ProductEntry.COLUMN_PRICE, 99);
        values.put(ProductEntry.COLUMN_QUANTITY, 10);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "TECH COMP");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, "9995556622");

        Uri updateUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

    }

    private void insertProduct() {
        Intent addNew = new Intent(MainActivity.this, DetailsActivity.class);
        startActivity(addNew);
    }


    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from product database");
    }

    public void saleProduct(long id, int quantity) {

        if (quantity >= 1) {
            quantity--;
            Uri updateUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_QUANTITY, quantity);
            int rowsUpdated = getContentResolver().update(
                    updateUri,
                    values,
                    null,
                    null);
            if (rowsUpdated == 1) {
                Toast.makeText(this, R.string.sale_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sale_unsuccessful, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_LONG).show();
        }
    }
}
