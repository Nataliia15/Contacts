package com.example.mycontactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private FloatingActionButton addBut;
    private EditText nameET,numberET;
    private int PERMISSION_REQUEST_CODE_READ_CONTACTS = 1;
    private int PERMISSION_REQUEST_CODE_WRRITE_CONTACTS = 2;
    private boolean isGranted=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.list);
        addBut=findViewById(R.id.floatingActionButton);
        nameET=findViewById(R.id.nameET);
        numberET=findViewById(R.id.numberET);

        getContactUserReadPermission();



       // getContacts();
        addBut.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getContactUserWritePermission();

            }
        });

    }

        public void getContacts(){
       // if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
         //   ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},0);}

        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        String[] from={ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
        int[]to={R.id.textViewName,R.id.textViewNumber,R.id.imageView};
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.my_item,cursor,from,to);
        listView.setAdapter(adapter);
    }
    public void insertContact(){

        String name=nameET.getText().toString();
        String number=numberET.getText().toString();
        Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
        long rowContactId = generateNewContactId();
        insertContactDisplayName(addContactsUri, rowContactId, name);
        insertContactPhoneNumber(addContactsUri, rowContactId, number);
        nameET.setText("");
        numberET.setText("");
        Toast.makeText(this,name+ "was inserted!",Toast.LENGTH_SHORT);





    }
    public long generateNewContactId(){
        ContentValues contentValues=new ContentValues();
        Uri rawContactUri=getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI,contentValues);
        long newId= ContentUris.parseId(rawContactUri);

        return newId;
    }
    private void insertContactDisplayName(Uri addContactsUri, long rawContactId, String displayName)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);
        getContentResolver().insert(addContactsUri, contentValues);
    }
    private void insertContactPhoneNumber(Uri addContactsUri, long rawContactId, String phoneNumber) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(addContactsUri,contentValues);
    }
    public void getContactUserReadPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission granted!",Toast.LENGTH_SHORT).show();
            getContacts();

        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_CODE_READ_CONTACTS);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_CODE_READ_CONTACTS);
            }

        }

    }
    public void getContactUserWritePermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission WRIte granted!",Toast.LENGTH_SHORT).show();
            insertContact();

        }else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_CONTACTS},PERMISSION_REQUEST_CODE_WRRITE_CONTACTS);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_CONTACTS},PERMISSION_REQUEST_CODE_WRRITE_CONTACTS);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_READ_CONTACTS) {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission is Granted!",Toast.LENGTH_SHORT).show();
                getContacts();
            }else{
                Toast.makeText(this,"Permission Dinied!",Toast.LENGTH_SHORT).show();
        }

    }else if(requestCode ==PERMISSION_REQUEST_CODE_WRRITE_CONTACTS){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission WRITE_Contact is  Granted!",Toast.LENGTH_SHORT).show();
                insertContact();

}else{
                Toast.makeText(this,"Permission WRItE ConTact is Dinied!",Toast.LENGTH_SHORT).show();
            }
        }}

}