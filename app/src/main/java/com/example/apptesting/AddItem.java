package com.example.apptesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItem extends AppCompatActivity {
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
}