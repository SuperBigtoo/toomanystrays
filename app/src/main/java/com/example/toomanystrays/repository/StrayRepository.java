package com.example.toomanystrays.repository;

import android.util.Log;

import androidx.annotation.NonNull;

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

public class StrayRepository {

    private static ArrayList<StrayAnimal> strayObj = new ArrayList<>();

    public static void GET_STRAY_BY_PIN(int id) throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/stray_animal?_where=(pin_id,eq,"+id+")")
                .build();
        Log.d("debug", "GETTING Stray Data at pin_id : "+id);
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
                    Log.d("debug", "GET Stray Data at pin_id : "+id+" is Successful :\n"+myResponse);
                    try {
                        if (!getStrayObj().isEmpty()) {
                            Log.d("debug", "Clear Stray Data Object");
                            getStrayObj().clear();
                        }
                        JSONArray myList = new JSONArray(myResponse);
                        for (int i = 0; i < myList.length(); i++) {
                            JSONObject data = myList.getJSONObject(i);
                            Log.d("debug", "JSONObject : "+i);
                            //Get data from JSONObject
                            int id = data.getInt("id");
                            String stray_name = data.getString("stray_name");
                            String details = data.getString("details");
                            String image_url = data.getString("image_url");
                            int pin_id = data.getInt("pin_id");
                            int category_id = data.getInt("category_id");

                            //Adding data to ArrayList
                            StrayAnimal strayAnimal
                                    = new StrayAnimal(id, stray_name, details, image_url, pin_id, category_id);

                            getStrayObj().add(strayAnimal);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("debug", "GET Stray Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static void POST_STRAY(String postBody, int pin_id) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, postBody);
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/stray_animal")
                .post(body)
                .build();
        Log.d("debug", "POSTING STRAY at Pin ID : "+pin_id);
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
                    Log.d("debug", "POSTING STRAY at Pin ID : "+pin_id+" is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "POSTING STRAY is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static void DELETE_STRAY_BY_ID(int stray_id) throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/stray_animal/"+stray_id)
                .delete()
                .build();
        Log.d("debug", "DELETING Stray Data at ID : "+stray_id);
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
                    Log.d("debug", "DELETE Stray Data at ID : "+stray_id+" is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "DELETE Stray Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static ArrayList<StrayAnimal> getStrayObj() { return strayObj; }
}
