package com.example.toomanystrays.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.toomanystrays.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserRepository {
    private static User user = new User();

    private static boolean foundAccID = false;
    public static void CHECK_SIGN_IN(GoogleSignInAccount acct) {
        if(acct != null) {
            String id = acct.getId();
            String username = acct.getDisplayName();
            String email = acct.getEmail();
            String postBody = "{\n" +
                    "    \"id\": \""+id+"\",\n" +
                    "    \"username\": \""+username+"\",\n" +
                    "    \"email\": \""+email+"\"\n" +
                    "}";
            getUser().setUsername(id);
            getUser().setUsername(username);
            getUser().setEmail(email);
            Log.d("debug", "Check AccID");
            Log.d("debug", "id acc :"+id);
            Log.d("debug", "username :"+username);
            Log.d("debug", "email :"+email);
            try {
                CHECK_ACC_ID(postBody, id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void FETCH_DATA_USER(GoogleSignInAccount acct) throws IOException {
        if (acct != null) {
            String id = acct.getId();
            String username = acct.getDisplayName();
            String email = acct.getEmail();
            String patchBody = "{\n" +
                    "    \"username\": \""+username+"\",\n" +
                    "    \"email\": \""+email+"\"\n" +
                    "}";
            getUser().setId(id);
            getUser().setUsername(username);
            getUser().setEmail(email);
            Log.d("debug", "Update on id acc :"+id);
            Log.d("debug", "username :"+username);
            Log.d("debug", "email :"+email);
            UPDATE_USER(patchBody, id);
        }
    }

    private static void CHECK_ACC_ID(String postBody, String id) throws IOException {
        OkHttpClient clientCheck = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/users?_where=(id,eq,"+id+")")
                .build();
        Log.d("debug", "Checking AccID");
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
                    try {
                        JSONArray myList = new JSONArray(myResponse);
                        for (int i = 0; i < myList.length(); i++) {
                            JSONObject data = myList.getJSONObject(i);
                            if (data != null) {
                                if (data.getString("id").equals(id)) {
                                    foundAccID = true;
                                    Log.d("debug", "Found AccID : "+data.getString("id"));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (!foundAccID) {
                        Log.d("debug", "AccID Not Found");
                        SIGN_UP(postBody);
                    }
                }
            }
        });
    }

    private static void SIGN_UP(String postBody) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, postBody);
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/users")
                .post(body)
                .build();
        Log.d("debug", "Signing Up");
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
                    Log.d("debug", "Sign Up is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "Sign Up is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    private static void UPDATE_USER(String patchBody, String id) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, patchBody);
        Request request = new Request.Builder()
                .url("http://192.168.1.47:8000/api/users/"+id)
                .patch(body)
                .build();
        Log.d("debug", "UPDATING User Data");
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
                    Log.d("debug", "UPDATE User Data is Successful :\n"+myResponse);
                } else {
                    Log.d("debug", "UPDATE User Data is not Daijoubu :\n"+myResponse);
                }
            }
        });
    }

    public static User getUser() {
        return user;
    }
}
