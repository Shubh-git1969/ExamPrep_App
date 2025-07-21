package com.example.examprep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyActivity extends AppCompatActivity {
    private String verificationId;

    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        verificationId = getIntent().getStringExtra("verificationId");

        EditText codeInput = findViewById(R.id.codeInput);
        Button verifyBtn = findViewById(R.id.verifyBtn);

        verifyBtn.setOnClickListener(v ->{
            String code = codeInput.getText().toString().trim();
            if(code.isEmpty()){
                codeInput.setError("Enter OTP");
                return;
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(task ->{
                        if(task.isSuccessful()){
                            Intent intent = new Intent(VerifyActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
