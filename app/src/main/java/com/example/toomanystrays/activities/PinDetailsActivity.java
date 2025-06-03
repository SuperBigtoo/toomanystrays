package com.example.toomanystrays.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toomanystrays.MainScreen;
import com.example.toomanystrays.R;
import com.example.toomanystrays.adapter.CommentAdapter;
import com.example.toomanystrays.adapter.StrayAdapter;
import com.example.toomanystrays.models.Comment;
import com.example.toomanystrays.models.Pin;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

public class PinDetailsActivity extends AppCompatActivity {

    private Pin pinObj;
    private ArrayList<StrayAnimal> strayObj;
    private ArrayList<Comment> commentObj;
    private String body;

    private GoogleSignInAccount acct;

    public FloatingActionButton buttonDeletePin, buttonGotoPin, buttonBack, buttonSendComment, buttonSaveDetails;
    private RecyclerView tagStrayList, CommentList;
    private EditText textPinName, editTextPinDetails, editTextComment;

    private int pinId;
    private String access_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_details);

        Intent getIntent = getIntent();
        pinId = getIntent.getIntExtra("pinID", 0);
        access_from = getIntent.getStringExtra("access_from");

        //initial Pin Data
        acct = GoogleSignIn.getLastSignedInAccount(this);
        pinObj = new Pin();
        strayObj = new ArrayList<>();
        commentObj = new ArrayList<>();
        findDataObjAt(pinId); //pinObj

        //List Items
        tagStrayList = findViewById(R.id.listStray);
        CommentList = findViewById(R.id.listComment);

        //EditText
        textPinName = findViewById(R.id.textPinName);
        textPinName.setText(pinObj.getPin_name());
        editTextPinDetails = findViewById(R.id.textPinDetails);
        editTextPinDetails.setText(pinObj.getDetails());
        editTextComment = findViewById(R.id.editComment);

        //TextView
        TextView textPinLatitude = findViewById(R.id.textPinLatitude);
        textPinLatitude.setText(String.valueOf(pinObj.getLatitude()));
        TextView textPinLongitude = findViewById(R.id.textPinLongitude);
        textPinLongitude.setText(String.valueOf(pinObj.getLongitude()));
        TextView textPinDateCreated = findViewById(R.id.textPinDateCreated);
        textPinDateCreated.setText(pinObj.getCreated_time());

        //Button
        buttonDeletePin = findViewById(R.id.buttonDeletePin);
        buttonGotoPin = findViewById(R.id.buttonGotoLocation);
        buttonBack = findViewById(R.id.buttonPinDetailsBack);
        buttonSendComment = findViewById(R.id.buttonSendComment);
        buttonSaveDetails = findViewById(R.id.buttonSaveDetails);

        //SetOnClickListener
        buttonDeletePin.setOnClickListener(v -> {
            FetchData(1);
        });

        //go to location
        buttonGotoPin.setOnClickListener(v -> {
            FetchData(2);
            Intent intent = new Intent(PinDetailsActivity.this, MainScreen.class);
            intent.putExtra("pin_latitude", pinObj.getLatitude());
            intent.putExtra("pin_longitude", pinObj.getLongitude());
            intent.putExtra("Go_to_Location_Pin", true);
            finish();
            startActivity(intent);
        });

        //back
        buttonBack.setOnClickListener(v-> {
            FetchData(2);
        });

        //Send Comment
        buttonSendComment.setOnClickListener(v -> {
            if (!editTextComment.getText().toString().equals("")) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss");
                String formattedDateTime = datetimeFormat.format(calendar.getTime());
                body = "{\n" +
                        "    \"text\": \""+editTextComment.getText().toString()+"\",\n" +
                        "    \"created_time\": \""+formattedDateTime+"\",\n" +
                        "    \"pin_id\": \""+pinObj.getId()+"\",\n" +
                        "    \"username\": \""+UserRepository.getUser().getUsername()+"\",\n" +
                        "    \"user_id\": \""+UserRepository.getUser().getId()+"\"\n" +
                        "}";
                FetchData(4);
            } else {
                Toast.makeText(PinDetailsActivity.this, "No Text Comment to Send", Toast.LENGTH_SHORT).show();
            }
        });

        //Save Details
        buttonSaveDetails.setOnClickListener(v -> {
            body = "{\n" +
                    "    \"pin_name\": \""+textPinName.getText().toString()+"\",\n" +
                    "    \"details\": \""+editTextPinDetails.getText().toString()+"\"\n" +
                    "}";
            FetchData(3);
        });

        if (UserRepository.getUser().getId().equals(pinObj.getUser_id())) {
            buttonDeletePin.show();
            editTextPinDetails.setEnabled(true);
            textPinName.setEnabled(true);
            buttonSaveDetails.show();
        } else {
            buttonDeletePin.hide();
            editTextPinDetails.setEnabled(false);
            textPinName.setEnabled(false);
            buttonSaveDetails.hide();
        }

        //Set ListView
        setArrayAdapter();
    }

    private void setArrayAdapter() {
        tagStrayList.setLayoutManager(new LinearLayoutManager(this));
        tagStrayList.setAdapter(new StrayAdapter(getApplicationContext(), strayObj));

        CommentList.setLayoutManager(new LinearLayoutManager(this));
        CommentList.setAdapter(new CommentAdapter(getApplicationContext(), commentObj));
    }

    private void findDataObjAt(int pin_id) {
        for (int i = 0; i < PinRepository.getPinAll().size(); i++) {
            if (PinRepository.getPinAll().get(i).getId() == pin_id) {
                Log.d("debug", "Found PinObj at Pin ID = "+ pin_id);
                pinObj = PinRepository.getPinAll().get(i);
                break;
            }
        }

        Log.d("debug", "Adding strayObj at Pin ID = "+ pin_id);
        int count = 1;
        for (int i = 0; i < StrayRepository.getStrayObj().size(); i++) {
            if (StrayRepository.getStrayObj().get(i).getPin_id() == pin_id) {
                strayObj.add(StrayRepository.getStrayObj().get(i));
                Log.d("debug", "Adding "+count+" strayObj");
                count++;
            }
        }

        Log.d("debug", "Adding commentObj at Pin ID = "+ pin_id);
        count = 1;
        for (int i = 0; i < CommentRepository.getCommentObj().size(); i++) {
            if (CommentRepository.getCommentObj().get(i).getPin_id() == pin_id) {
                commentObj.add(CommentRepository.getCommentObj().get(i));
                Log.d("debug", "Adding "+count+" commentObj");
                count++;
            }
        }
    }

    private void FetchData(int c) {
        Log.d("debug", "PinDetailsActivity : Fetch Data Case : "+c+" is Started");

        Thread waitingThread = new Thread(() -> {
            waitForInitialize(c);
            switch (c) {
                case 1:
                case 2:
                    finish();
                    if (access_from.equals("MainScreen")) {
                        Log.d("debug", "PinDetailsActivity ---> MainScreen");
                        startActivity(new Intent(PinDetailsActivity.this, MainScreen.class));
                    } else {
                        Log.d("debug", "PinDetailsActivity ---> MyPinsListActivity");
                    }
                    break;

                case 3:
                case 4:
                    startActivity(getIntent());
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
        Log.d("debug", "PinDetailsActivity : Done Wait Thread");
        Log.d("debug", "PinDetailsActivity : Fetch Data Case : "+c+" is Finished");
    }

    public synchronized void InitializeDATA(int c) {
        // Perform some tasks before notifying
        // Fetch Data
        switch (c) {
            case 1:
                Object lock = new Object();
                Thread threadCase1 = new Thread(() -> {
                    synchronized (lock) {
                        try {
                            lock.wait();
                            PinRepository.DELETE_PIN_BY_ID(pinObj.getId());
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
                try {
                    for (int i = 0; i < strayObj.size(); i++) {
                        try {
                            StrayRepository.DELETE_STRAY_BY_ID(strayObj.get(i).getId());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    for (int i = 0; i < commentObj.size(); i++) {
                        try {
                            CommentRepository.DELETE_COMMENT_BY_ID(commentObj.get(i).getId());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lock) {
                    lock.notify(); // Notify the waiting thread
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

            case 3:
                Object lockCase3 = new Object();
                Thread threadCase3 = new Thread(() -> {
                    synchronized (lockCase3) {
                        try {
                            lockCase3.wait();
                            CategoryRepository.GET_CATEGORY();
                            PinRepository.GET_ALL_PINS();
                            StrayRepository.GET_STRAY_BY_PIN(pinObj.getId());
                            CommentRepository.GET_COMMENT_BY_PIN(pinObj.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                threadCase3.start();
                try {
                    PinRepository.UPDATE_PIN_BY_ID(body, pinObj.getId());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockCase3) {
                    lockCase3.notify(); // Notify the waiting thread
                }
                break;

            case 4:
                Object lockCase4 = new Object();
                Thread threadCase4 = new Thread(() -> {
                    synchronized (lockCase4) {
                        try {
                            lockCase4.wait();
                            CategoryRepository.GET_CATEGORY();
                            PinRepository.GET_ALL_PINS();
                            StrayRepository.GET_STRAY_BY_PIN(pinObj.getId());
                            CommentRepository.GET_COMMENT_BY_PIN(pinObj.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                threadCase4.start();
                try {
                    CommentRepository.POST_COMMENT(body,pinObj.getId(), UserRepository.getUser().getId());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockCase4) {
                    lockCase4.notify(); // Notify the waiting thread
                }
                break;
        }

        try {
            Log.d("debug", "PinDetailsActivity : Sleep Thread 2 sec");
            Thread.sleep(1000);
            Log.d("debug", "PinDetailsActivity : Thread wake up");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        flag = true;
        notify(); // Notifies waiting thread(s)
        Log.d("debug", "PinDetailsActivity : Done Notify Thread");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FetchData(2);
    }
}