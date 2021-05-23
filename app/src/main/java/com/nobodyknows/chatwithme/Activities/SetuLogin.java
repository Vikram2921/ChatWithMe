package com.nobodyknows.chatwithme.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.nobodyknows.chatwithme.R;
import com.nobodyknows.chatwithme.services.MessageMaker;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class SetuLogin extends AppCompatActivity {

    private Button send,verify;
    private EditText number,otp;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private String txnid= "";
    private String apiKey = "shRPbnXUdj472zXHJEYeg1Oz3TlfmvFt3xeNBZhV";
    protected OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setu_login);
        client = new OkHttpClient();
        init();
    }

    private void init() {
        send = findViewById(R.id.send);
        verify = findViewById(R.id.verify);
        otp = findViewById(R.id.otp);
        number = findViewById(R.id.number);
        number.setText(MessageMaker.getMyNumber());
        Log.d("TAGSHA", "init: "+sha256("740720"));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberText = number.getText().toString();
                if(numberText != null && numberText.length() > 0) {
                    try {
                        sendOTP(numberText);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpText = otp.getText().toString();
                if(otpText != null && otpText.length() > 0) {
                    try {
                        verifyOtp(otpText);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private void verifyOtp(String otpText) throws IOException, JSONException {
        String url = "https://cdn-api.co-vin.in/api/v2/auth/validateMobileOtp";
        String otpHex = sha256(otpText);
        String json = "";
        JSONObject object = new JSONObject();
        object.put("otp",otpHex);
        object.put("txnId",txnid);
        json = object.toString();
        RequestBody requestBody = RequestBody.create(JSON,json);
        Request request= new Request.Builder()
                .url(url)
                .post(requestBody).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
        System.out.println(response.body().string());
    }

    private void sendOTP(String numberText) throws IOException, JSONException {
        String url = "https://cdn-api.co-vin.in/api/v2/auth/generateMobileOTP";
        String json = "";
        JSONObject object = new JSONObject();
        object.put("mobile",numberText);
        json = object.toString();
        RequestBody requestBody = RequestBody.create(JSON,json);
        Request request= new Request.Builder()
                .url(url)
                .post(requestBody).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
        System.out.println(response.body().string());
    }
}