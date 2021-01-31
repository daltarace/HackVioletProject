package com.example.apptesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.id;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import android.widget.ImageView;


public class AddItem extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    List<Item> itemList;
    private DatabaseReference mDatabase;
    private DatabaseReference itemsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemList = new ArrayList<Item>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        itemsRef = FirebaseDatabase.getInstance().getReference().child("items");


    }

    //from given code in android developers website
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView = (ImageView)findViewById(R.id.barcodeImg);
            imageView.setImageBitmap(imageBitmap);

            imageFromBitmap(imageBitmap); //check this here

            scanBarcodes(image); //CHECK THIS HERE
            //the variable 'result' has barcode info

            //Context context = getApplicationContext();
            //int duration = Toast.LENGTH_SHORT;

            //Toast toast = Toast.makeText(context, "WORKS", duration);
            //toast.show();



        }
        super.onActivityResult(requestCode, resultCode, data);






    }


    public void scanBarcode(View view){
        dispatchTakePictureIntent();
    }


    private void writeNewProduct(String name, String exprirationDate) {
        String key = itemsRef.push().getKey();
        itemsRef.child(key).setValue(new Item(name, exprirationDate));
    }

    private void isExpired(String dateYear) {
        try {
            new SimpleDateFormat("MM/yyyy").parse(dateYear).compareTo(new Date());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment(view.getId());
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void addItemToFB(View view){
        EditText expirationDateEditText = (EditText) findViewById(R.id.editTextExpiration);
        EditText nameEditText = (EditText) findViewById(R.id.editName);
        String expirationDate = expirationDateEditText.getText().toString();
        String itemName = nameEditText.getText().toString();
        if(itemName.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please fill out item's name", Toast.LENGTH_SHORT).show();
        }else if(expirationDate.equals("")) {

            Toast.makeText(getApplicationContext(), "Please fill out expiration date", Toast.LENGTH_SHORT).show();
        }
        else{
            writeNewProduct(itemName, expirationDate);
            Toast.makeText(getApplicationContext(), "Add Item successfully", Toast.LENGTH_SHORT).show();
        }
    }



    //start of barcode reader

    InputImage image;


    private void imageFromBitmap(Bitmap bitmap) {
        int rotationDegree = 0;
        // [START image_from_bitmap]
        image = InputImage.fromBitmap(bitmap, rotationDegree);
        // [END image_from_bitmap]
    }

    private void scanBarcodes(InputImage image) {
        //SECOND WORKS WAS HERE



        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_ALL_FORMATS

                        )

                        .build();

        //}

        BarcodeScanner scanner = BarcodeScanning.getClient();

        Task<List<Barcode>> result = scanner.process(image) //used to be local variable check
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {

                        //THIRD TOAST WORKED HERE
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_barcodes]
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_SHORT;

                        int barcodesize = barcodes.size();
                        String barcodeSizeTest = String.valueOf(barcodesize);
                        Toast toast = Toast.makeText(context, barcodeSizeTest , duration);
                        toast.show();

                        for (Barcode barcode : barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();




                            //TOAST FOURTH NOT WORK HERE
                            // See API reference for complete list of supported types
                            switch (valueType) {

                                case Barcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    break;
                                case Barcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    break;
                                case Barcode.TYPE_PRODUCT:
                                    String value = barcode.getDisplayValue(); //Not very sure how to get the value

                                    //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT);

                                    break;
                            }
                        }
                        // [END get_barcodes]
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
        // [END run_detector]
    }

    //end of barcode reader






}