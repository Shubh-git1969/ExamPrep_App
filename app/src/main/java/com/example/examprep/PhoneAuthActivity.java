package com.example.examprep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseException;
import androidx.annotation.NonNull;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        mAuth = FirebaseAuth.getInstance();

        EditText phoneInput = findViewById(R.id.phoneInput);
        Button sendBtn = findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim();
            if(phone.isEmpty()){
                phoneInput.setError("Enter phone number");
                return;
            }
            sendVerificationCode(phone);
        });
    }

    private void sendVerificationCode(String phone){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential){
                        signInWithCredential(credential);
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e){
                        Toast.makeText(PhoneAuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token){
                        super.onCodeSent(verificationId, token);
                        codeSent = verificationId;
                        Intent intent = new Intent(PhoneAuthActivity.this, VerifyActivity.class);
                        intent.putExtra("verificationId" , verificationId);
                        startActivity(intent);
                    }


                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void signInWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        Intent intent  = new Intent(PhoneAuthActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(this, "Login successfull!", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(this, " Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
