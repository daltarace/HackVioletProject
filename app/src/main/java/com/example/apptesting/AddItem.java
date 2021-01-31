package com.example.apptesting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.id;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;


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




            //remove & replace scan code button upon replace operation but keep id same - work-in-progress
//            View scanCodeBtn = findViewById(R.id.scanBarcodeBtn);
//            ViewGroup parent = (ViewGroup)scanCodeBtn.getParent();
//            int scanBtnId = scanCodeBtn.getId();
//            int index = parent.indexOfChild(scanCodeBtn);
//            parent.removeView(scanCodeBtn);
//            scanCodeBtn = getLayoutInflater().inflate(R.layout.activity_add_item, parent, false);
//            parent.addView(scanCodeBtn, index);
//            scanCodeBtn.setId(scanBtnId);

        }
        super.onActivityResult(requestCode, resultCode, data);






    }


    public void scanBarcode(View view){
        dispatchTakePictureIntent();
    }


    private void writeNewProduct(String name, String expirationDate) {
        String key = itemsRef.push().getKey();
        itemsRef.child(key).setValue(new Item(name, expirationDate));
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addItemToFB(View view){
        EditText expirationDateEditText = (EditText) findViewById(R.id.editTextExpiration);
        EditText nameEditText = (EditText) findViewById(R.id.editName);
        EditText pkgDateEditText = (EditText) findViewById(R.id.editPkgDate);
        EditText moExpEditText = (EditText) findViewById(R.id.editMoExpNumber);
        String expirationDate = expirationDateEditText.getText().toString();
        String itemName = nameEditText.getText().toString();
        if(itemName.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please fill out item's name", Toast.LENGTH_SHORT).show();
        }else if(expirationDate.equals("") && pkgDateEditText.getText().toString().equals("") || expirationDate.equals("") && moExpEditText.getText().toString().equals("") ) {
            Toast.makeText(getApplicationContext(), "Please fill out the expiration information", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!expirationDate.equals("")) {
                writeNewProduct(itemName, expirationDate);
                Toast.makeText(getApplicationContext(), "Add Item successfully", Toast.LENGTH_SHORT).show();
            }else {
                if (!pkgDateEditText.getText().equals("") && !moExpEditText.equals("")) {
                }

                SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                Calendar c = Calendar.getInstance();
                try{
                    //Setting the date to the given date
                    c.setTime(sdf.parse(pkgDateEditText.getText().toString()));
                }catch(ParseException e){
                    Toast.makeText(getApplicationContext(), "Make sure input is correct", Toast.LENGTH_SHORT).show();
                }
                try {
                    c.add(Calendar.MONTH, Integer.valueOf(moExpEditText.getText().toString()));
                    String calcExpDate = sdf.format(c.getTime());
                    writeNewProduct(itemName, calcExpDate);
                    Toast.makeText(getApplicationContext(), "Add Item successfully", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Please fill out the expiration information", Toast.LENGTH_SHORT).show();
                }

            }

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


                        int barcodesize = barcodes.size();
                        String barcodeSizeTest = String.valueOf(barcodesize);

                        //TOAST HERE ONLY PRINTS 0 as the barcodes size, but 1 for qr code


                        for (Barcode barcode : barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();

                            
                            Toast.makeText(getApplicationContext(), rawValue , Toast.LENGTH_LONG).show();
                            fetchProductFromBarcode(String.valueOf(rawValue));




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
                                     int value = barcode.getValueType();

                                    Context context = getApplicationContext();
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, value, duration);
                                    toast.show();
                                            //getDisplayValue(); //Not very sure how to get the value

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

    public void getBarCode(View view){
        fetchProductFromBarcode("190514050377");
    }

    public void fetchProductFromBarcode(String barcode) {
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.apigenius.io/products/identifiers?upc=" + barcode+"&api_key=c92527de1c1e430096411a8a099d548b";
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG);
        TextView textView = (TextView) findViewById(R.id.editName);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            textView.setText(response.getJSONObject("items").getString("title"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        textView.setText(error.toString());
                        //Failure Callback

                    }
                }) {
            /** Passing some request headers* */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("ApiGenius_API_Key", "c92527de1c1e430096411a8a099d548b");
                return headers;
            }
        };
//API GENIUS KEY: c92527de1c1e430096411a8a099d548b
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }





}