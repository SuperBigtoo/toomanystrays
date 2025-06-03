package com.example.toomanystrays.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toomanystrays.MainScreen;
import com.example.toomanystrays.R;
import com.example.toomanystrays.adapter.CommentAdapter;
import com.example.toomanystrays.adapter.StrayAdapter;
import com.example.toomanystrays.models.StrayAnimal;
import com.example.toomanystrays.repository.CategoryRepository;
import com.example.toomanystrays.repository.CommentRepository;
import com.example.toomanystrays.repository.PinRepository;
import com.example.toomanystrays.repository.StrayRepository;
import com.example.toomanystrays.repository.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreatePinActivity extends AppCompatActivity {

    private EditText editNewPinName, editNewPinDetails;
    private FloatingActionButton buttonCreatePinBack, buttonInsertStray, buttonInsertNewPin;
    private RecyclerView listNewStray;
    public static ArrayList<StrayAnimal> newStrayObj;

    private GoogleSignInAccount acct;
    private String body;
    private double last_latitude, last_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        Log.d("debug", "on Create");

        Intent getIntent = getIntent();
        last_latitude = getIntent.getDoubleExtra("last_latitude", 0);
        last_longitude = getIntent.getDoubleExtra("last_longitude", 0);

        acct = GoogleSignIn.getLastSignedInAccount(this);

        listNewStray = findViewById(R.id.listInsertNewStray);

        //EditText
        editNewPinName = findViewById(R.id.editNewPinName);
        editNewPinDetails = findViewById(R.id.editNewPinDetails);

        //button
        buttonCreatePinBack = findViewById(R.id.buttonCreatePinBack);
        buttonInsertStray = findViewById(R.id.buttonInsertStray);
        buttonInsertNewPin = findViewById(R.id.buttonInsertNewPin);

        //SetOnClickListener
        buttonCreatePinBack.setOnClickListener(v -> {
            FetchData(1);
        });

        buttonInsertStray.setOnClickListener(v -> {
            Intent intentCase2 = new Intent(CreatePinActivity.this, NewStrayActivity.class);
            startActivity(intentCase2);
        });

        buttonInsertNewPin.setOnClickListener(v -> {
            if (newStrayObj.isEmpty()) {
                Toast.makeText(CreatePinActivity.this, "Please insert new Stray before putting the pin", Toast.LENGTH_SHORT).show();
            } else {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss");
                String formattedDateTime = datetimeFormat.format(calendar.getTime());

                body = "{\n" +
                        "    \"pin_name\": \""+ editNewPinName.getText().toString() +"\",\n" +
                        "    \"details\": \""+ editNewPinDetails.getText().toString() +"\",\n" +
                        "    \"latitude\": \""+ last_latitude +"\",\n" +
                        "    \"longitude\": \""+ last_longitude +"\",\n" +
                        "    \"created_time\": \""+ formattedDateTime +"\",\n" +
                        "    \"user_id\": \""+UserRepository.getUser().getId()+"\"\n" +
                        "}";
                FetchData(2);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "on Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "on Resume");
        setArrayAdapter();
    }

    private void setArrayAdapter() {
        if (newStrayObj == null) {
            newStrayObj = new ArrayList<>();
        } else {
            listNewStray.setLayoutManager(new LinearLayoutManager(this));
            listNewStray.setAdapter(new StrayAdapter(getApplicationContext(), newStrayObj));
        }
    }

    private void FetchData(int c) {
        Log.d("debug", "CreatePinActivity : Fetch Data Case : "+c+" is Started");

        Thread waitingThread = new Thread(() -> {
            waitForInitialize(c);
            newStrayObj.clear();
            switch (c) {
                case 1:
                case 2:
                    Intent intent = new Intent(CreatePinActivity.this, MainScreen.class);
                    finish();
                    startActivity(intent);
                    break;
            }
        });

        Thread notifyingThread = new Thread(() -> {
            // Perform some tasks before notifying
            InitializeDATA(c);
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
        Log.d("debug", "CreatePinActivity : Done Wait Thread");
        Log.d("debug", "CreatePinActivity : Fetch Data Case : "+c+" is Finished");
    }

    public synchronized void InitializeDATA(int c) {
        // Perform some tasks before notifying
        // Fetch Data
        switch (c) {
            case 1:
                try {
                    UserRepository.FETCH_DATA_USER(acct);
                    PinRepository.GET_ALL_PINS();
                    PinRepository.GET_USER_PINS(UserRepository.getUser().getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 2:
                Object lock = new Object();
                Thread threadCase1 = new Thread(() -> {
                    synchronized (lock) {
                        try {
                            lock.wait();
                            UserRepository.FETCH_DATA_USER(acct);
                            PinRepository.GET_ALL_PINS();
                            PinRepository.GET_USER_PINS(UserRepository.getUser().getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                threadCase1.start();
                PinRepository.POST_PIN(body, UserRepository.getUser().getId());
                synchronized (lock) {
                    lock.notify(); // Notify the waiting thread
                }
                break;
        }
        try {
            Log.d("debug", "CreatePinActivity : Sleep Thread 1 sec");
            Thread.sleep(1000);
            Log.d("debug", "CreatePinActivity : Thread wake up");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        flag = true;
        notify(); // Notifies waiting thread(s)
        Log.d("debug", "CreatePinActivity : Done Notify Thread");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FetchData(1);
    }
}