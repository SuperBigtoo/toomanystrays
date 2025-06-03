package com.example.toomanystrays.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.toomanystrays.models.Comment;
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

public class CommentRepository {

    private static ArrayList<Comment> commentObj = new ArrayList<>();

    public static void GET_COMMENT_BY_PIN(int id) throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/comment?_where=(pin_id,eq,"+id+")")
                .build();
        Log.d("debug", "GETTING Comment Data at pin_id : "+id);
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
                    Log.d("debug", "GET Comment Data at pin_id : "+id+" is Successful :\n"+myResponse);
                    try {
                        if (!getCommentObj().isEmpty()) {
                            Log.d("debug", "Clear Comment Data Object");
                            getCommentObj().clear();
                        }
                        JSONArray myList = new JSONArray(myResponse);
                        for (int i = 0; i < myList.length(); i++) {
                            JSONObject data = myList.getJSONObject(i);
                            Log.d("debug", "JSONObject : "+i);
                            //Get data from JSONObject
                            int id = data.getInt("id");
                            String text = data.getString("text");
                            String created_time = data.getString("created_time");
                            int pin_id = data.getInt("pin_id");
                            String username = data.getString("username");
                            String user_id = data.getString("user_id");

                            //Adding data to ArrayList
                            Comment comment = new Comment(id, pin_id, text, created_time, username, user_id);

                            getCommentObj().add(comment);
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

    public static void POST_COMMENT(String postBody, int pin_id, String user_id) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, postBody);
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/comment")
                .post(body)
                .build();
        Log.d("debug", "POSTING COMMENT at Pin ID : "+ pin_id +" | from User ID : "+ user_id);
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
                    Log.d("debug", "POSTING COMMENT at Pin ID : "+pin_id+" | from User ID : "+ user_id +" is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "POSTING COMMENT is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static void DELETE_COMMENT_BY_ID(int comment_id) throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/comment/"+comment_id)
                .delete()
                .build();
        Log.d("debug", "DELETING Comment Data at ID : "+comment_id);
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
                    Log.d("debug", "DELETE Comment Data at ID : "+comment_id+" is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "DELETE Comment Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static ArrayList<Comment> getCommentObj() { return commentObj; }
}
