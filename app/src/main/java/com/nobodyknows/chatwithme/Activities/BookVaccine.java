package com.nobodyknows.chatwithme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nobodyknows.chatwithme.Activities.Dashboard.Adapters.VaccineRecyclerViewAdapter;
import com.nobodyknows.chatwithme.DTOS.VaccineCenter;
import com.nobodyknows.chatwithme.DTOS.VaccineSessions;
import com.nobodyknows.chatwithme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class BookVaccine extends AppCompatActivity {

    private String stateFetchUrl = "";
    private String districFetchUrl = "https://cdn-api.co-vin.in/api/v2/admin/location/districts/";
    private RequestQueue requestQueue;
    private CountDownTimer countDownTimer;
    private ArrayList<VaccineCenter> centers = new ArrayList<>();
    private RecyclerView recyclerView;
    private VaccineRecyclerViewAdapter recyclerViewAdapter;
    private String jwttoken = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTYyMTY5NzM4NiwiaWF0IjoxNjIxNjk3Mzg2fQ.wqk_3TcdXeOZoKSKXDuFuhdH8SUm8gq70IhsFQ_5YBs";
    private String webUrl = "https://www.nobodyknows.com/setu_callback";
    private String apiKey = "shRPbnXUdj472zXHJEYeg1Oz3TlfmvFt3xeNBZhV";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vaccine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Book COVID-19 Vaccine");
        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.vaccineList);
        recyclerViewAdapter = new VaccineRecyclerViewAdapter(getApplicationContext(),centers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        layoutManager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        addTimer();
    }

    private void readDummy() {
        readResponse(readJSON());
    }

    private String readJSON() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("DummyVaccine.json");
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


    private void addTimer() {
        sync();
        countDownTimer  = new CountDownTimer(10000, 20) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    sync();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        };
    }

    private void readResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONArray centersList = object.getJSONArray("centers");
            if(centersList.length() > 0) {
                for(int i=0;i<centersList.length();i++) {
                    convertToVaccine(centersList.getJSONObject(i));
                }
            } else {
                Toast.makeText(getApplicationContext(),"Not Started Yet.",Toast.LENGTH_SHORT).show();
            }
            if(countDownTimer != null) {
                countDownTimer.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sync() {
        String url = getAvailabilityUrl("507");
        centers.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAGREC", "onResponse: "+response);
                readResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void convertToVaccine(JSONObject jsonObject) throws JSONException {
        VaccineCenter center = new VaccineCenter();
        JSONArray array = jsonObject.getJSONArray("sessions");
        VaccineSessions vaccineSessions;
        int totalAvailable = 0;
        for(int i=0;i<array.length();i++) {
            if(array.getJSONObject(i).getInt("available_capacity") > 0) {
                vaccineSessions = new VaccineSessions();
                vaccineSessions.setSessionId(array.getJSONObject(i).getString("session_id"));
                vaccineSessions.setDate(array.getJSONObject(i).getString("date"));
                vaccineSessions.setAvailableCapacity(array.getJSONObject(i).getInt("available_capacity"));
                vaccineSessions.setMinAgeLimit(array.getJSONObject(i).getInt("min_age_limit"));
                vaccineSessions.setVaccine(array.getJSONObject(i).getString("vaccine"));
                vaccineSessions.setAvailableDose1(array.getJSONObject(i).getInt("available_capacity_dose1"));
                vaccineSessions.setAvailableDose2(array.getJSONObject(i).getInt("available_capacity_dose2"));
                center.addSession(vaccineSessions);
                totalAvailable += vaccineSessions.getAvailableCapacity();
            }
        }
        if(totalAvailable > 0) {
            center.setCenterId(jsonObject.getString("center_id"));
            center.setName(jsonObject.getString("name"));
            center.setAddress(jsonObject.getString("address"));
            center.setStateName(jsonObject.getString("state_name"));
            center.setDistrictName(jsonObject.getString("district_name"));
            center.setBlockName(jsonObject.getString("block_name"));
            center.setTotalAvailable(totalAvailable);
            center.setPincode(jsonObject.getString("pincode"));
            center.setLat(jsonObject.getInt("lat"));
            center.setLon(jsonObject.getInt("long"));
            center.setFrom(jsonObject.getString("from"));
            center.setTo(jsonObject.getString("to"));
            center.setFeeType(jsonObject.getString("fee_type"));
            centers.add(center);
            recyclerViewAdapter.notifyItemInserted(centers.size() -1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private String getAvailabilityUrl(String districid) {
        return "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id="+districid+"&date="+new Date().toString();
    }
}