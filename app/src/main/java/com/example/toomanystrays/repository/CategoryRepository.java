package com.example.toomanystrays.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.toomanystrays.models.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryRepository {

    private static ArrayList<Category> category = new ArrayList<>();;

    public static void GET_CATEGORY() throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/category")
                .build();

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
                    Log.d("debug", "GET Category Data :\n"+myResponse);
                    try {
                        JSONArray myList = new JSONArray(myResponse);
                        for (int i = 0; i < myList.length(); i++) {
                            JSONObject data = myList.getJSONObject(i);

                            //Get data from JSONObject
                            int id = data.getInt("id");
                            String category_name = data.getString("category_name");

                            //Adding data to ArrayList
                            Category category = new Category();
                            category.setId(id);
                            category.setCategory_name(category_name);
                            getCategory().add(category);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("debug", "GET Category Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static ArrayList<Category> getCategory() {
        return category;
    }
}
