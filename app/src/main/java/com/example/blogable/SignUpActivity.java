package com.example.blogable;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    TextView signTitle;
    ProgressBar signBar;
    EditText signUser, signEmail, signPass, signPass2;
    Button signBtn;
    FirebaseAuth mAuth;
    FirebaseUser userProfile;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signTitle = findViewById(R.id.signTitle);
        signBar = findViewById(R.id.signBar);
        signUser = findViewById(R.id.signUser);
        signEmail = findViewById(R.id.signEmail);
        signPass = findViewById(R.id.signPass);
        signPass2 = findViewById(R.id.signPass2);
        signBtn = findViewById(R.id.signBtn);

        mAuth = FirebaseAuth.getInstance();
        userProfile = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        //get string email from mainActivity
        String loginString = getIntent().getStringExtra("emailKey");
        signEmail.setText(loginString);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCheck();
            }
        });
    }

    public void Toast(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
    }

    private void inputCheck() {
        //should limit username string myself to certain characters and limit length!!!
        String userStr = signUser.getText().toString();
        String emailStr = signEmail.getText().toString();
        String passStr = signPass.getText().toString();
        String passStr2 = signPass2.getText().toString();

        //if one editText is empty then provide toast message, remind user to fill all fields
        if(userStr.isEmpty() || emailStr.isEmpty() || passStr.isEmpty() || passStr2.isEmpty()){
            Toast("Please enter username, email, password and password confirmation");
        }
        //if password strings do not match, tell user to ensure they are the same
        else if(!passStr.equals(passStr2)) {
            Toast("Please ensure password and confirmation password are the same");
        }
        else{
            checkFirebase(userStr, emailStr, passStr);
        }
    }

    //is it better to save username as child node of User, then set uid under username node? probably
    //then I can have my dataSnapshot iterate through just Names instead of all children of User node
    //or I can create new Username node and iterate through that instead
    private void checkFirebase(final String user, final String email, final String pass){
        final DatabaseReference username = database.getReference("Username");
        //at Username node add event listener
        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if Username node has no children, then create User
                if(dataSnapshot.getChildrenCount() == 0){
                    createUser(user, email, pass);
                }
                else {
                    //if username exists as child node of Username
                    if (dataSnapshot.child(user).exists()) {
                        //provide toast message and do not create User
                        Toast("Username is taken. Please choose another.");
                    } else {
                        //if user does not match Name value in db, create user
                        createUser(user, email, pass);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast("Error registering.");
            }
        });
    }

    public void createUser(final String name, String email, String password){
        final DatabaseReference users = database.getReference("Users");
        final DatabaseReference usernameList = database.getReference("Username");
        //create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if user created
                if(task.isSuccessful()){
                    //hide progress bar
                    signBar.setVisibility(View.GONE);
                    //update userProfile with username
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    userProfile.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.i("TAG", "User profile updated");
                            }
                            else{
                                Log.i("TAG", "Not updated");
                            }
                        }
                    });

                    //save uid as child node of User and user string as child of uid node
                    users.child(userProfile.getUid()).child("Name").setValue(name);
                    //save username inside Username node
                    usernameList.child(name).setValue(userProfile.getUid());
                    //create intent and save the username string to it, called nameKey
                    Intent blogIntent = new Intent(SignUpActivity.this, BlogActivity.class);
                    blogIntent.putExtra("nameKey", name);
                    startActivity(blogIntent);
                }
                else{
                    if(task.getException() == null){
                        Toast("Error with registration.");
                    }
                    Toast(task.getException().getMessage()); //uses fireBase error messages
                }
            }
        });
    }


//    private void signUp(final String user, final String email, final String pass){
//        final DatabaseReference users = database.getReference("Users");
//        //at User node add event listener
//        users.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //if User node has no children, then create User
//                if(dataSnapshot.getChildrenCount() == 0){
//                    createUser(user, email, pass);
//                }
//                else {
//                    //for children of User node, get children (includes other nodes that may not be Name, e.g. Email)
//                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                        //get child value of User node where child node is "Name"
//                        //save value as nameInDB
//                        String nameInDB = childSnapshot.child("Name").getValue().toString();
//                        //if nameInDB value equals user string
//                        if (nameInDB.equals(user)) {
//                            //provide toast message and do not create User
//                            Toast("Username is taken. Please choose another.");
//                        } else {
//                            //if user does not match Name value in db, create user
//                            createUser(user, email, pass);
//                            //not very efficient, can use childSnapshot.child("emily525").exists()
//                            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                            //save Username's with child node of uid instead
//                        }
//                    }
//                }
//            }

}
