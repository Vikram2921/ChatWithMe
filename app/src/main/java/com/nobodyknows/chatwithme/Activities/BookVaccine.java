package com.nobodyknows.chatwithme.Activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codepath.asynchttpclient.AbsCallback;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.google.type.DateTime;
import com.nobodyknows.chatwithme.Activities.Dashboard.Adapters.VaccineRecyclerViewAdapter;
import com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces.VaccineSelectListener;
import com.nobodyknows.chatwithme.DTOS.VaccineCenter;
import com.nobodyknows.chatwithme.DTOS.VaccineSessions;
import com.nobodyknows.chatwithme.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;


public class BookVaccine extends AppCompatActivity {

    private String stateFetchUrl = "";
    private String districFetchUrl = "https://cdn-api.co-vin.in/api/v2/admin/location/districts/";
    private String scheduleURL = "https://cdn-api.co-vin.in/api/v2/appointment/schedule";
    private CountDownTimer countDownTimer;
    private ArrayList<VaccineCenter> centers = new ArrayList<>();
    private RecyclerView recyclerView;
    private VaccineRecyclerViewAdapter recyclerViewAdapter;
    private int minAgeLimitFilter = 18;
    private String apiKey = "shRPbnXUdj472zXHJEYeg1Oz3TlfmvFt3xeNBZhV";
    private int dosenumber = 1;
    private String benefeciaryId = "90809695362020";  //VIKRAM SINGH RAWAT
    private AsyncHttpClient client = new AsyncHttpClient();
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIwNWMzOTUxZi1lYTI4LTRmNzYtODc2Zi1jYjZjYTU1YmQzM2UiLCJ1c2VyX2lkIjoiMDVjMzk1MWYtZWEyOC00Zjc2LTg3NmYtY2I2Y2E1NWJkMzNlIiwidXNlcl90eXBlIjoiQkVORUZJQ0lBUlkiLCJtb2JpbGVfbnVtYmVyIjo3MDE0NTUwMjk4LCJiZW5lZmljaWFyeV9yZWZlcmVuY2VfaWQiOjkwODA5Njk1MzYyMDIwLCJzZWNyZXRfa2V5IjoiYjVjYWIxNjctNzk3Ny00ZGYxLTgwMjctYTYzYWExNDRmMDRlIiwidWEiOiJNb3ppbGxhLzUuMCAoV2luZG93cyBOVCAxMC4wOyBXaW42NDsgeDY0KSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvOTAuMC40NDMwLjIxMiBTYWZhcmkvNTM3LjM2IEVkZy85MC4wLjgxOC42NiIsImRhdGVfbW9kaWZpZWQiOiIyMDIxLTA1LTIzVDEzOjIwOjU0LjY1M1oiLCJpYXQiOjE2MjE3NzYwNTQsImV4cCI6MTYyMTc3Njk1NH0.zR2kzPTmNVJ2rlip1rO38dLXElfs8MQxgDqvdtpZgiI";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vaccine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Book COVID-19 Vaccine");
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    private void init() {
        recyclerView = findViewById(R.id.vaccineList);
        recyclerViewAdapter = new VaccineRecyclerViewAdapter(getApplicationContext(), centers, new VaccineSelectListener() {
            @Override
            public void onSchedule(VaccineSessions sessions,String slot) {
                try {
                    schedule(sessions,slot);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        layoutManager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        addTimer();
       //readDummy();
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
        try {
            sync();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Log.d("TAGRESPONSE", "readResponse: ."+response);
            JSONObject object = new JSONObject(response);
            JSONArray centersList = object.getJSONArray("centers");
            if(centersList.length() > 0) {
                for(int i=0;i<centersList.length();i++) {
                    convertToVaccine(centersList.getJSONObject(i));
                }
            }
            if(countDownTimer != null) {
                countDownTimer.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sync() throws IOException {
        String url = getAvailabilityUrl("507");
        centers.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        StringRequest stringRequest = new StringRequest(url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                readResponse(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
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
                JSONArray slots = array.getJSONObject(i).getJSONArray("slots");
                for(int j=0;j<slots.length();j++) {
                    vaccineSessions.addSlot(slots.getString(j));
                }
                center.addSession(vaccineSessions);
                totalAvailable += vaccineSessions.getAvailableCapacity();
            }
        }
        if(totalAvailable >0) {
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        String date = simpleDateFormat.format(tomorrow);
        Log.d("TAGRESPONSE", "getAvailabilityUrl: "+date);
        return "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id="+districid+"&date="+date;
    }

    private void schedule(VaccineSessions sessions,String slot) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dose",1);
        jsonObject.put("session_id",sessions.getSessionId());
        jsonObject.put("slot",slot);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(benefeciaryId);
        jsonObject.put("beneficiaries",jsonArray);
        RequestHeaders requestHeaders = new RequestHeaders();
        requestHeaders.put("Authorization","Bearer "+token);
        client.post(scheduleURL,requestHeaders, null,jsonObject.toString(), new AbsCallback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("TAGLOG", "onResponse: "+response.toString());
               // Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}