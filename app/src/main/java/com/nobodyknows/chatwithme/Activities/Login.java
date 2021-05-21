package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.nobodyknows.chatwithme.Activities.Signup.CreateUser;
import com.nobodyknows.chatwithme.MainActivity;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.FirebaseService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private FirebaseService firebaseService;
    private EditText number;
    private Button continueButton;
    private Spinner countryList;
    private ArrayList<String> countries = new ArrayList<>();
    private ArrayList<String> codes = new ArrayList<>();
    String numToEnter = "",country = "( +91 ) India",countryCode = "91";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        if(getIntent().hasExtra("number")) {
            numToEnter = getIntent().getStringExtra("number");
            country = getIntent().getStringExtra("country");
            countryCode = getIntent().getStringExtra("countryCode");
        }
        firebaseService = new FirebaseService();
        init();
    }

    private void init() {
        number = findViewById(R.id.number);
        number.setText(numToEnter);
        countryList = findViewById(R.id.countryList);
        addCountryList();
        continueButton = findViewById(R.id.continuelogin);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = number.getText().toString();
                if(contact != null && contact.length() > 0 && contact.length() <=15) {
                    validContact(contact);
                } else {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile number.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String readJSON() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("country.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }

    private void addCountryList() {
        countries.clear();
        try {
            JSONArray jsonArray = new JSONArray(readJSON());
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                countries.add("( +"+jsonObject.getString("calling_code")+" ) "+jsonObject.getString("country"));
                codes.add(jsonObject.getString("calling_code"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryList.setAdapter(dataAdapter);
        countryList.setSelection(codes.indexOf(countryCode));
    }

    private void validContact(String number) {
        continueButton.setText("Checking");
        continueButton.setEnabled(false);
        firebaseService.readFromFireStore("Users").document(number).collection("AccountInfo").document("PersonalInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    int countryIndex  = countryList.getSelectedItemPosition();
                    if(task.getResult().exists()) {
                        Intent intent = new Intent(getApplicationContext(), LoginContinue.class);
                        intent.putExtra("number",number);
                        intent.putExtra("country",countries.get(countryIndex));
                        intent.putExtra("countryCode",codes.get(countryIndex));
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CreateUser.class);
                        intent.putExtra("number",number);
                        intent.putExtra("country",countries.get(countryIndex));
                        intent.putExtra("countryCode",codes.get(countryIndex));
                        startActivity(intent);
                        finish();
                    }
                } else {
                    continueButton.setText("Continue");
                    continueButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Unable to process your request",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}