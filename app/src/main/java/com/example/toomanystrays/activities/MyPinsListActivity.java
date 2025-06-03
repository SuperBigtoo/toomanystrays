package com.example.toomanystrays.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toomanystrays.MainScreen;
import com.example.toomanystrays.R;
import com.example.toomanystrays.models.Pin;
import com.example.toomanystrays.repository.CategoryRepository;
import com.example.toomanystrays.repository.CommentRepository;
import com.example.toomanystrays.repository.PinRepository;
import com.example.toomanystrays.repository.StrayRepository;
import com.example.toomanystrays.repository.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

public class MyPinsListActivity extends AppCompatActivity {

    private ListView myPinList;
    private ArrayList<Integer> myPinID;
    private ArrayList<String> myPinName;
    private ArrayList<Pin> myPinsObj;
    private GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypin_list);

        //ListView
        myPinList = findViewById(R.id.listPin);

        //Initial DATA
        acct = GoogleSignIn.getLastSignedInAccount(this);
        myPinsObj = new ArrayList<>();

        //button
        FloatingActionButton buttonBack = findViewById(R.id.button_mypinBack);
        buttonBack.setOnClickListener(v -> {
            FetchData(2, 0);
        });

        //selete item
        myPinList.setOnItemClickListener((adapterView, view, position, id) -> {
            Log.d("debug", "position : "+position+" | id : "+ myPinID.get(position)
                    + " | pin_name :"+ myPinName.get(position));
            //initial Stray & Comment Data
            FetchData(1, myPinID.get(position));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myPinsObj = PinRepository.getMyPin();
        initialData();
    }

    private void initialData() {
        //initial arraylists
        myPinID = new ArrayList<>();
        myPinName = new ArrayList<>();

        //set myPinID & myPinName
        setMyPinArrayList();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MyPinsListActivity.this
                , R.layout.pin_list_item, R.id.pinItem, myPinName);
        myPinList.setAdapter(arrayAdapter);
    }

    private void setMyPinArrayList() {
        for (int i = 0; i < myPinsObj.size(); i++) {
            int id = myPinsObj.get(i).getId();
            String pin_name = myPinsObj.get(i).getPin_name();

            myPinID.add(id);
            myPinName.add(pin_name);
        }
    }

    private void FetchData(int c, int pin_id) {
        Log.d("debug", "MyPinsListActivity : Fetch Data Case : "+c+" is Started");

        Thread waitingThread = new Thread(() -> {
            waitForInitialize(c);
            switch (c) {
                case 1:
                    Intent intentCase1 = new Intent(MyPinsListActivity.this, PinDetailsActivity.class);
                    intentCase1.putExtra("pinID", pin_id);
                    intentCase1.putExtra("access_from", "MyPinsListActivity");
                    startActivity(intentCase1);
                    break;

                case 2:
                    Intent intentCase2 = new Intent(MyPinsListActivity.this, MainScreen.class);
                    finish();
                    startActivity(intentCase2);
                    break;
            }
        });

        Thread notifyingThread = new Thread(() -> {
            // Perform some tasks before notifying
            InitializeDATA(c, pin_id);
        });

        waitingThread.start();
        notifyingThread.start();
    }

    private boolean flag = false;
    public synchronized void waitForInitialize(int c) {
        while (!flag) {
            try {
                wait(); // Thread waits until notified
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Method execution continues after the flag is set

        flag = false;
        Log.d("debug", "MyPinsListActivity : Done Wait Thread");
        Log.d("debug", "MyPinsListActivity : Fetch Data Case : "+c+" is Finished");
    }

    public synchronized void InitializeDATA(int c, int pin_id) {
        // Perform some tasks before notifying
        //Initialize & Fetch Data
        switch (c) {
            case 1:
                try {
                    CategoryRepository.GET_CATEGORY();
                    PinRepository.GET_ALL_PINS();
                    StrayRepository.GET_STRAY_BY_PIN(pin_id);
                    CommentRepository.GET_COMMENT_BY_PIN(pin_id);
                    Thread.sleep(1500);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 2:
                try {
                    UserRepository.FETCH_DATA_USER(acct);
                    PinRepository.GET_ALL_PINS();
                    PinRepository.GET_USER_PINS(UserRepository.getUser().getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        try {
            Log.d("debug", "MyPinsListActivity : Sleep Thread 3 sec");
            Thread.sleep(1000);
            Log.d("debug", "MyPinsListActivity : Thread wake up");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        flag = true;
        notify(); // Notifies waiting thread(s)
        Log.d("debug", "MyPinsListActivity : Done Notify Thread");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FetchData(2, 0);
    }
}