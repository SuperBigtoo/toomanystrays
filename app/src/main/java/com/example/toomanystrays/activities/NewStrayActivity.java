package com.example.toomanystrays.activities;

import static com.example.toomanystrays.activities.CreatePinActivity.newStrayObj;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.toomanystrays.R;
import com.example.toomanystrays.models.StrayAnimal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NewStrayActivity extends AppCompatActivity {

    private RadioButton radioButton ;
    private EditText editNewStrayName, editNewStrayDetails;
    private ShapeableImageView imageView;
    private Bitmap bitmap;
    private StrayAnimal newStrayAnimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stray);

        //initial new Stray obj
        newStrayAnimal = new StrayAnimal(0, null, null, null,0, 0);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(((group, checkedId) -> {
            radioButton = findViewById(checkedId);
            if (checkedId == R.id.radio_button_1) {
                newStrayAnimal.setCategory_id(1);
            } else if (checkedId == R.id.radio_button_2) {
                newStrayAnimal.setCategory_id(2);
            }
        }));

        //editText
        editNewStrayName = findViewById(R.id.editNewStrayName);
        editNewStrayDetails = findViewById(R.id.editNewStrayDetails);

        //Image
        imageView = findViewById(R.id.imageNewStray);
        imageView.setBackgroundResource(R.drawable.insert_pic);

        //button
        FloatingActionButton buttonInsertStrayBack = findViewById(R.id.buttonInsertStrayBack);
        FloatingActionButton buttonInsertNewStray = findViewById(R.id.buttonInsertNewStray);

        //SetOnClickListener
        buttonInsertStrayBack.setOnClickListener(v -> {
            finish();
        });

        buttonInsertNewStray.setOnClickListener(v -> {
            if (newStrayAnimal.getCategory_id() != 0) {
                String newStrayName = editNewStrayName.getText().toString();
                String newStrayDetails = editNewStrayDetails.getText().toString();

                newStrayAnimal.setStray_name(newStrayName);
                newStrayAnimal.setDetails(newStrayDetails);

                if (bitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    final String base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP);
                    Log.d("Bytes Image", " Bytes : \n"+base64Image);
                    newStrayAnimal.setImage_url(base64Image);
                }

                newStrayObj.add(newStrayAnimal);
                finish();
            } else {
                Toast.makeText(NewStrayActivity.this, "Please Select Category Stray Tag Before Ya Press Insert", Toast.LENGTH_LONG).show();
            }
        });

        //Upload Image From Device
        ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}