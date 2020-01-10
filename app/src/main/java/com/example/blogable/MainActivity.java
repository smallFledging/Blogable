package com.example.blogable;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView logTitle;
    ProgressBar logBar;
    EditText logEmail, logPass;
    Button logBtn, logSignBtn;
    String emailStr, passStr;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        logTitle = findViewById(R.id.logTitle);
        logBar = findViewById(R.id.logBar);
        logEmail = findViewById(R.id.logEmail);
        logPass = findViewById(R.id.logPass);
        logBtn = findViewById(R.id.logBtn);
        logSignBtn = findViewById(R.id.logSignBtn);



        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress login bar is set to visible on login button
                logBar.setVisibility(View.VISIBLE);

                emailStr = logEmail.getText().toString();
                passStr = logPass.getText().toString();

                if (emailStr.isEmpty()) {
                    Toast("Please enter your email address.");
                    logBar.setVisibility(View.GONE);
                } else if (passStr.isEmpty()) {
                    Toast("Please enter your password");
                    logBar.setVisibility(View.GONE);
                } else {
                    logIn(emailStr, passStr);
                }
            }
        });

        logSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = logEmail.getText().toString();
                Intent logRegIntent = new Intent(MainActivity.this, SignUpActivity.class);
                //add the email string to intent so the email editText is filled in SignUp activity
                logRegIntent.putExtra("emailKey", email);
                startActivity(logRegIntent);
                finish();
                //destroy activity main and not exist in back stack

            }
        });

    }

    public void Toast(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
    }

    public void logIn(String emailStr, String passStr) {

        mAuth.signInWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                logBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast("Login Successful");
                    startActivity(new Intent(MainActivity.this, BlogActivity.class));
                } else {
                    Throwable taskException = task.getException();
                    if (taskException != null) {
                        Toast(task.getException().getMessage());
                    }
                }

            }
        });
    }

}
