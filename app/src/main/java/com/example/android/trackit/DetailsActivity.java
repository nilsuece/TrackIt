package com.example.android.trackit;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.example.android.trackit.data.ProductContract.ProductEntry;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private Uri currentProductUri;

    private EditText nameEditText;

    private EditText quantityEditText;

    private EditText priceEditText;

    private EditText suppNameEditText;

    private EditText suppPhoneEditText;

    private boolean productHasChanged = false;


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle(R.string.add_new_product);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_product);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        nameEditText = (EditText) findViewById(R.id.name_edit);
        priceEditText = (EditText) findViewById(R.id.price_edit);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit);
        suppNameEditText = (EditText) findViewById(R.id.supp_name_edit);
        suppPhoneEditText = (EditText) findViewById(R.id.supp_phone_edit);
        Button dec_button = (Button) findViewById(R.id.dec_button);
        Button inc_button = (Button) findViewById(R.id.inc_button);

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        suppNameEditText.setOnTouchListener(touchListener);
        suppPhoneEditText.setOnTouchListener(touchListener);


        dec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = quantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    quantity = String.valueOf(0);
                }

                int quantityNumber = Integer.parseInt(quantity);

                if (quantityNumber == 0 || quantityNumber < 1) {
                    Toast.makeText(getApplicationContext(), R.string.out_of_stock, Toast.LENGTH_SHORT).show();
                } else {
                    quantityNumber = quantityNumber - 1;
                    quantityEditText.setText(String.valueOf(quantityNumber));
                }
            }
        });

        inc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = quantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    quantity = String.valueOf(0);
                }

                int quantityNumber = Integer.parseInt(quantity);

                quantityNumber = quantityNumber + 1;
                quantityEditText.setText(String.valueOf(quantityNumber));

            }
        });

        Button callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = suppPhoneEditText.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), R.string.no_phone_number, Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(callIntent);
                }
            }
        });
    }

    private boolean saveProduct() {

        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String suppNameString = suppNameEditText.getText().toString().trim();
        String suppPhoneString = suppPhoneEditText.getText().toString().trim();


        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(suppNameString) || TextUtils.isEmpty(suppPhoneString))

        {
            validateBeforeSave();
            return false;
        } else {

            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(ProductEntry.COLUMN_PRICE, priceString);
            values.put(ProductEntry.COLUMN_QUANTITY, quantityString);
            values.put(ProductEntry.COLUMN_SUPPLIER_NAME, suppNameString);
            values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, suppPhoneString);

            double price = 0.00;
            if (!TextUtils.isEmpty(priceString))

            {
                price = Double.parseDouble(priceString);
            }
            values.put(ProductEntry.COLUMN_PRICE, price);

            if (currentProductUri == null)

            {
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.update_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.update_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if(saveProduct()){
                    finish();
                    return true;
                } else {
                    return false;
                }
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener cancelButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                showUnsavedChangesDialog(cancelButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int suppNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int suppPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String suppName = cursor.getString(suppNameColumnIndex);
            final String suppPhone = cursor.getString(suppPhoneColumnIndex);


            nameEditText.setText(name);
            priceEditText.setText(price);
            quantityEditText.setText(quantity);
            suppNameEditText.setText(suppName);
            suppPhoneEditText.setText(suppPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        suppNameEditText.setText("");
        suppPhoneEditText.setText("");
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener cancelButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.cancel, cancelButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private boolean validateBeforeSave() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            nameEditText.setError(getText(R.string.name_error));
        } else if (TextUtils.isEmpty(priceEditText.getText().toString())) {
            priceEditText.setError(getText(R.string.price_error));
        } else if (TextUtils.isEmpty(quantityEditText.getText().toString())) {
            quantityEditText.setError(getText(R.string.quantity_error));
        } else if (TextUtils.isEmpty(suppNameEditText.getText().toString())) {
            suppNameEditText.setError(getText(R.string.supp_name_error));
        } else if (TextUtils.isEmpty(suppPhoneEditText.getText().toString())) {
            suppPhoneEditText.setError(getText(R.string.supp_phone_error));
        }
        return true;
    }


}
