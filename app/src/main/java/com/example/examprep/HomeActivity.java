package com.example.examprep;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.content.Intent;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import android.os.Bundle;
import android.os.Build;
import android.view.Window;
import androidx.core.content.ContextCompat;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private LinearLayout editProfileActions;
    private LinearLayout changeProfilePhotoActions;
    private TextView changeProfilePhotoText;
    private TextView editProfileText;
    private LinearLayout subjectContainer;
    private HashMap<Integer, List<String>> semesterSubjects;
    private DrawerLayout drawerLayout;
    private ImageView profileImage , drawerProfileImage;
    private Button logoutButton;
    private TextView changeNameText;
    private Button takePhotoBtn , chooseGalleryBtn;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_PICK = 101;
    private static final int REQUEST_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.deep_purple_custom));
        }
        Log.d(TAG, "Before setContentView");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        profileImage = findViewById(R.id.profileImage);
        drawerProfileImage = findViewById(R.id.drawerProfileImage);

        String savedUri = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("ProfileImageUri", null);

        if(savedUri != null){
            Uri uri = Uri.parse(savedUri);
            profileImage.setImageURI(uri);
            drawerProfileImage.setImageURI(uri);
        }else{

        String base64 = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("ProfileImageBitmap", null);

        if(base64 != null){
            byte[] bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
            Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap);
            drawerProfileImage.setImageBitmap(bitmap);
        }
        }
        logoutButton = findViewById(R.id.logoutButton);
        editProfileText = findViewById(R.id.editProfileText);
        editProfileActions = findViewById(R.id.editProfileActions);

        changeProfilePhotoText = findViewById(R.id.changeProfilePhotoText);
        changeProfilePhotoActions = findViewById(R.id.changeProfilePhotoActions);

        changeNameText = findViewById(R.id.changeNameText);
        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        chooseGalleryBtn = findViewById(R.id.chooseGalleryBtn);



        profileImage.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        editProfileText.setOnClickListener(v -> {
            if(editProfileActions.getVisibility() == View.VISIBLE){
                editProfileActions.setVisibility(View.GONE);
            } else{
                editProfileActions.setVisibility(View.VISIBLE);
            }
        });

        changeProfilePhotoText.setOnClickListener(v ->{
            if(changeProfilePhotoActions.getVisibility() == View.VISIBLE){
                changeProfilePhotoActions.setVisibility(View.GONE);
            }else{
                changeProfilePhotoActions.setVisibility(View.VISIBLE);
            }
        });

        changeNameText.setOnClickListener(v ->{
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Change Username");

            final android.widget.EditText input = new android.widget.EditText(HomeActivity.this);
            builder.setView(input);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newName = input.getText().toString().trim();
                if(!newName.isEmpty()){
                    TextView userNameView = findViewById(R.id.userName);
                    userNameView.setText(newName);

                    getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().putString("Username", newName).apply();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        takePhotoBtn.setOnClickListener(v ->{
            if(checkAndRequestPermissions()){
                openCamera();
            }

        });

        chooseGalleryBtn.setOnClickListener(v ->{
            if(checkAndRequestPermissions()){
                openGallery();
            }
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PhoneAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });




        Log.d(TAG, "After setContentView");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "Finding subjectContainer");
        subjectContainer = findViewById(R.id.subjectContainer);
        Log.d(TAG, "subjectContainer found");

        ImageView bellIcon = findViewById(R.id.notificationBell);
        bellIcon.setOnClickListener(v ->{
            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        semesterSubjects = new HashMap<>();
        semesterSubjects.put(1, List.of("Physics", "Chemistry", "Maths", "English", "BME"));
        semesterSubjects.put(2, List.of("Chemistry", "Engineering Drawing", "BEE", "Civil", "Mathematics II"));
        semesterSubjects.put(3, List.of("Probability", "OOPS", "DSA", "Technical Communication", "Artificial Intelligence"));
        semesterSubjects.put(4, List.of("Discrete Maths", "Machine Learning", "Software Engineering", "ADA", "COA"));
        semesterSubjects.put(5, List.of("NLP", "Operating System", "Deep Learning", "DBMS"));
        semesterSubjects.put(6, List.of("Computer Network", "Theory of Computation", "Cloud Computing", "Pattern Recognition"));
        semesterSubjects.put(7, List.of("Project 1", "Internship", "Seminar"));
        semesterSubjects.put(8, List.of("Project 2", "Internship II"));

        for (int i = 1; i <= 8; i++) {
            int semester = i;
            int btnId = getResources().getIdentifier("sem" + i, "id", getPackageName());
            Button btn = findViewById(btnId);
            btn.setOnClickListener(v -> showSubjectsForSemester(semester));
        }

        showSubjectsForSemester(3);

        String savedName = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("Username", "User Name");
        TextView userNameView = findViewById(R.id.userName);
        userNameView.setText(savedName);


    }


    private void showSubjectsForSemester(int semester){
        subjectContainer.removeAllViews();
        List<String> subjects = semesterSubjects.get(semester);

        if(subjects != null){
            for(String subject : subjects){

                View view = getLayoutInflater().inflate(R.layout.item_subject_row, subjectContainer, false);
                TextView title = view.findViewById(R.id.subjectTitle);
                Button notes = view.findViewById(R.id.notesBtn);
                Button pyq = view.findViewById(R.id.pyqBtn);

                title.setText(subject);

                notes.setOnClickListener(v ->{
                    //TODO: Open Notes Activity
                });

                pyq.setOnClickListener(v ->{
                    //TODO : Open PYQ Activity
                });
                subjectContainer.addView(view);
            }
        }

    }

    private boolean checkAndRequestPermissions(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            return false;
        }
        return true;
    }
    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null){
            if(requestCode == REQUEST_IMAGE_CAPTURE){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap)extras.get("data");
                profileImage.setImageBitmap(imageBitmap);
                drawerProfileImage.setImageBitmap(imageBitmap);

                saveImageToPrefs(imageBitmap);

                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().remove("ProfileImageUri").apply();

            }else if(requestCode == REQUEST_IMAGE_PICK){
                Uri selectedImageUri = data.getData();
                profileImage.setImageURI(selectedImageUri);
                drawerProfileImage.setImageURI(selectedImageUri);

                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().putString("ProfileImageUri", selectedImageUri.toString()).remove("ProfileImageBitmap").apply();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveImageToPrefs(Bitmap bitmap){
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String encoded = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);

        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().putString("ProfileImageBitmap", encoded).apply();
    }
}