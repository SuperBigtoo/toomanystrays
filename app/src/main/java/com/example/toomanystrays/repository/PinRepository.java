package com.example.toomanystrays.repository;

import static com.example.toomanystrays.activities.CreatePinActivity.newStrayObj;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.toomanystrays.models.Pin;
import com.example.toomanystrays.models.StrayAnimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PinRepository {

    private static ArrayList<Pin> pinAll = new ArrayList<>();
    private static ArrayList<Pin> myPin = new ArrayList<>();

    public static void GET_ALL_PINS() throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/pin")
                .build();
        Log.d("debug", "GETTING All Pins Data");
        clientCheck.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String myResponse = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("debug", "GET All Pins Data is Successful :\n"+myResponse);
                    try {
                        if (!getPinAll().isEmpty()) {
                            Log.d("debug", "Clear Pin Data Object");
                            getPinAll().clear();
                        }
                        JSONArray myList = new JSONArray(myResponse);
                        for (int i = 0; i < myList.length(); i++) {
                            JSONObject data = myList.getJSONObject(i);
                            Log.d("debug", "JSONObject : "+i);
                            //Get data from JSONObject
                            int id = data.getInt("id");
                            String pin_name = data.getString("pin_name");
                            String details = data.getString("details");
                            double latitude = data.getDouble("latitude");
                            double longitude = data.getDouble("longitude");
                            String created_time = data.getString("created_time");
                            String user_id = data.getString("user_id");

                            //Adding data to ArrayList
                            Pin pin = new Pin();
                            pin.setId(id);
                            pin.setPin_name(pin_name);
                            pin.setDetails(details);
                            pin.setLatitude(latitude);
                            pin.setLongitude(longitude);
                            pin.setCreated_time(created_time);
                            pin.setUser_id(user_id);
                            getPinAll().add(pin);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("debug", "GET All Pins Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static void GET_USER_PINS(String id) throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/pin?_where=(user_id,eq,"+id+")")
                .build();
        Log.d("debug", "GETTING User Pins Data at ID : "+id);
        clientCheck.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String myResponse = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("debug", "GET User Pins Data at ID : "+id+" is Successful :\n"+myResponse);
                    try {
                        if (!getMyPin().isEmpty()) {
                            Log.d("debug", "Clear Pin Data Object");
                            getMyPin().clear();
                        }
                        JSONArray myList = new JSONArray(myResponse);
                        for (int i = 0; i < myList.length(); i++) {
                            JSONObject data = myList.getJSONObject(i);
                            Log.d("debug", "JSONObject : "+i);
                            //Get data from JSONObject
                            int id = data.getInt("id");
                            String pin_name = data.getString("pin_name");
                            String details = data.getString("details");
                            double latitude = data.getDouble("latitude");
                            double longitude = data.getDouble("longitude");
                            String created_time = data.getString("created_time");
                            String user_id = data.getString("user_id");

                            //Adding data to ArrayList
                            Pin pin = new Pin();
                            pin.setId(id);
                            pin.setPin_name(pin_name);
                            pin.setDetails(details);
                            pin.setLatitude(latitude);
                            pin.setLongitude(longitude);
                            pin.setCreated_time(created_time);
                            pin.setUser_id(user_id);
                            getMyPin().add(pin);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("debug", "GET User Pins Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static void DELETE_PIN_BY_ID(int pin_id) throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/pin/"+pin_id)
                .delete()
                .build();
        Log.d("debug", "DELETING Pin Data at ID : "+pin_id);
        clientCheck.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String myResponse = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("debug", "DELETE Pin Data at ID : "+pin_id+" is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "DELETE Pin Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static void UPDATE_PIN_BY_ID(String patchBody, int pin_id) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, patchBody);
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/pin/"+pin_id)
                .patch(body)
                .build();
        Log.d("debug", "UPDATING Pin Data at Pin ID : "+pin_id);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String myResponse = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("debug", "UPDATE Pin Data at Pin ID : "+pin_id+" is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "UPDATE Pin Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static void POST_PIN(String postBodyPin, String user_id) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, postBodyPin);
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/pin")
                .post(body)
                .build();
        Log.d("debug", "POSTING a new Pin Data by User ID : "+user_id);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String myResponse = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("debug", "POST a new Pin Data by User ID : "+user_id+"is Successful :\n"+myResponse);

                    //Insert Stray data
                    try {
                        JSONObject data = new JSONObject(myResponse);
                        int pin_id = data.getInt("insertId");

                        for (int i = 0; i < newStrayObj.size(); i++) {

                            String postStrayBody = "{\n" +
                                    "    \"stray_name\": \""+newStrayObj.get(i).getStray_name()+"\",\n" +
                                    "    \"details\": \""+newStrayObj.get(i).getDetails()+"\",\n" +
                                    "    \"image_url\": \""+newStrayObj.get(i).getImage_url()+"\",\n" +
                                    "    \"pin_id\": \""+pin_id+"\",\n" +
                                    "    \"category_id\": \""+newStrayObj.get(i).getCategory_id()+"\"\n" +
                                    "}";
                            StrayRepository.POST_STRAY(postStrayBody, pin_id);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    Log.d("debug", "POST a new Pin Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static ArrayList<Pin> getPinAll() { return pinAll; }
    public static ArrayList<Pin> getMyPin() {
        return myPin;
    }
}
