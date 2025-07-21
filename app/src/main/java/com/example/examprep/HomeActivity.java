package com.example.examprep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.view.Window;
import androidx.core.content.ContextCompat;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
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
    private LinearLayout subjectContainer;
    private HashMap<Integer, List<String>> semesterSubjects;

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
}