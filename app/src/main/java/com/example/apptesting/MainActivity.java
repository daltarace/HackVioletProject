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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        //Start of code to fetch from DB
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refItems = database.getReference().child("items");
        Context currContext = this;

        System.out.println(refItems);
        refItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> itemList = new HashMap<>();

                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    Item it = childSnapshot.getValue(Item.class);
                    System.out.println(it.getExpirationDate());
                    itemList.put(it.getName(), it.getExpirationDate());
                }

                ArrayList<String> arrayList = new ArrayList<>();
                List<HashMap<String,String>> listItems = new ArrayList<>();

                SimpleAdapter simpleAdapter = new SimpleAdapter(currContext, listItems,
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //End of code to fetch from DB





//        itemList.put("Item 2", "Exp: 2 month");
//        itemList.put("Item 3", "Exp: 3 month");
//        itemList.put("Item 4", "Exp: 4 month");
//        itemList.put("Item 5", "Exp: 5 month");
//        itemList.put("Item 6", "Exp: 6 month");
//        itemList.put("Item 7", "Exp: 7 month");





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
                                Barcode.FORMAT_ALL_FORMATS)
.build();

        //start of barcode reader new

        //end of barcode reader new
    }
    //this is where the barcode reader used to be


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





}



//}


