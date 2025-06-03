package com.example.toomanystrays.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.toomanystrays.MainScreen;
import com.example.toomanystrays.R;
import com.example.toomanystrays.repository.CategoryRepository;
import com.example.toomanystrays.repository.CommentRepository;
import com.example.toomanystrays.repository.PinRepository;
import com.example.toomanystrays.repository.StrayRepository;
import com.example.toomanystrays.repository.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;


/** @noinspection deprecation*/
public class SignIn_Activity extends AppCompatActivity {
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GoogleSignInAccount acct;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //set Image Logo
        ShapeableImageView logo = findViewById(R.id.imageLogo);
        logo.setBackgroundResource(R.drawable.logo_wakeup);

        //Google Auth
        FloatingActionButton googleSignin = findViewById(R.id.buttonSignin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
        acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct != null) {
            navigateToMainScreen();
        }

        googleSignin.setOnClickListener(view -> signIn());
    }

    @SuppressWarnings("deprecation")
    private void signIn() {
        Intent intent_signIn = gsc.getSignInIntent();
        startActivityForResult(intent_signIn, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                acct = GoogleSignIn.getLastSignedInAccount(this);
                UserRepository.CHECK_SIGN_IN(acct);
                navigateToMainScreen();
            }  catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void navigateToMainScreen() {
        FetchData();
    }

    private void FetchData() {
        Log.d("debug", "SignInActivity : Fetch Date At UserID : "+acct.getId()+" is Started");

        Thread waitingThread = new Thread(() -> {
            waitForInitialize();
            finish();
            Intent intent = new Intent(SignIn_Activity.this, MainScreen.class);
            startActivity(intent);
        });

        Thread notifyingThread = new Thread(() -> {
            // Perform some tasks before notifying
            InitializeDATA();
        });

        waitingThread.start();
        notifyingThread.start();
    }

    public synchronized void waitForInitialize() {
        while (!flag) {
            try {
                wait(); // Thread waits until notified
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        flag = false;
        Log.d("debug", "SignInActivity : Done Wait Thread");
        Log.d("debug", "SignInActivity : Fetch Date At UserID : "+acct.getId()+" is Finished");
    }

    public synchronized void InitializeDATA() {
        // Perform some tasks before notifying
        //Initialize & Fetch Data
        try {
            UserRepository.FETCH_DATA_USER(acct);
            PinRepository.GET_ALL_PINS();
            Log.d("debug", "SignInActivity : Sleep Thread 3 sec");
            Thread.sleep(1000);
            Log.d("debug", "SignInActivity : Thread wake up");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        flag = true;
        notify(); // Notifies waiting thread(s)
        Log.d("debug", "SignInActivity : Done Notify Thread");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}