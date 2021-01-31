package com.example.apptesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.id;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}