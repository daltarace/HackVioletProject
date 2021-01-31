package com.example.apptesting;

import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.barhopper.deeplearning.BarcodeDetectorClientOptions;
import com.google.mlkit.vision.barcode.Barcode;
//import com.google.android.libraries.barhopper.Barcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.util.SparseIntArray;
import android.view.Surface;

import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.content.Context.CAMERA_SERVICE;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);


        HashMap<String, String> itemList = new HashMap<>();
        itemList.put("Item 1", "Exp: 1 month");
        itemList.put("Item 2", "Exp: 2 month");
        itemList.put("Item 3", "Exp: 3 month");
        itemList.put("Item 4", "Exp: 4 month");
        itemList.put("Item 5", "Exp: 5 month");
        itemList.put("Item 6", "Exp: 6 month");
        itemList.put("Item 7", "Exp: 7 month");
        ArrayList<String> arrayList = new ArrayList<>();

        List<HashMap<String,String>> listItems = new ArrayList<>();

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                R.layout.list_item,
                new String[]{"firstLine", "secondLine"},
                new int[]{R.id.itemHeader, R.id.itemSub});

        Iterator it = itemList.entrySet().iterator();
        while(it.hasNext()){
            HashMap<String,String> resultMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultMap.put("firstLine", pair.getKey().toString());
            resultMap.put("secondLine", pair.getValue().toString());
            listItems.add(resultMap);
        }
        listView.setAdapter(simpleAdapter);

//not done
//        listView.setAdapter(arrayAdapter);
    }

    InputImage image;

    private void imageFromBitmap(Bitmap bitmap) {
        int rotationDegree = 0;
        // [START image_from_bitmap]
        image = InputImage.fromBitmap(bitmap, rotationDegree);
        // [END image_from_bitmap]
    }

    private void scanBarcodes(InputImage image) {

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_ALL_FORMATS
                        )
                        .build();

        //}

        BarcodeScanner scanner = BarcodeScanning.getClient();

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_barcodes]
                        for (Barcode barcode : barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
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
                                    Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT);
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


    //end barcode
    public void goToAddForm(View view) {
        Intent intent = new Intent(this, AddItem.class);
        startActivity(intent);
    }


    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        TextView editText = (TextView) findViewById(R.id.textView4);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }


    public void showAbout(View view) {
        Log.d(LOG_TAG, "Button clicked!");
        Intent intent = new Intent(this, SecondAboutActivity.class);
        startActivity(intent);

    }

    public void getBarCode(View view){
        fetchProductFromBarcode("190514050377");
    }

    public void fetchProductFromBarcode(String barcode) {
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.apigenius.io/products/identifiers?upc=" + barcode+"&api_key=c92527de1c1e430096411a8a099d548b";
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG);
        TextView textView = (TextView) findViewById(R.id.textView4);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        textView.setText(response.toString());
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