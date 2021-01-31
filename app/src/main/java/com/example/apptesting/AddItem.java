package com.example.apptesting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.id;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class AddItem extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
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
}