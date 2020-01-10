package com.example.blogable;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlogActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser userProfile;
    FirebaseDatabase database;
    Dialog popAddPost;

    EditText popUpInput;
    ImageView popUpPostBtn;
    ProgressBar popUpProgBar;

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    DatabaseReference dbPostRef;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        mAuth = FirebaseAuth.getInstance();
        userProfile = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        dbPostRef = database.getReference("Posts");

        TextView text = findViewById(R.id.blogText);
        Button btn = findViewById(R.id.postBtn);

        popUp();

        //if no user identified logout
        if(userProfile == null){
            startActivity(new Intent(BlogActivity.this, MainActivity.class));
        }
        else {
            //when signing up the username has not been set yet, only after logging in once
            //so get name from username editText
            if(userProfile.getDisplayName()==null){
                String getSignUpName = getIntent().getStringExtra("nameKey");
                String blogIntro = ("Hello " + getSignUpName + "!");
                text.setText(blogIntro);
            }
            else {
                //get name from db, will need to when logging in as it only uses email anyway
                String blogIntro = ("Hello " + userProfile.getDisplayName() + "!");
                text.setText(blogIntro);
            }
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });


        postRecyclerView = findViewById(R.id.postRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRecyclerView.setLayoutManager(layoutManager); //IT ORIGINALLY SAYS GET ACTIVITY BUT IT ISN'T A FRAGMENT
        postRecyclerView.setHasFixedSize(true);
        //video 9, 17:35

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onStart(){
        super.onStart();
        dbPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                }

                postAdapter = new PostAdapter( BlogActivity.this, postList); //get application context instead!!!!
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Toast(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
    }

    private void popUp(){
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        Objects.requireNonNull(popAddPost.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popUpInput = popAddPost.findViewById(R.id.popUpInput);
        popUpPostBtn = popAddPost.findViewById(R.id.popUpMsgBtn);
        popUpProgBar = popAddPost.findViewById(R.id.popupProgBar);

        popUpPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String popupText = popUpInput.getText().toString();

                if(popupText.isEmpty() ){
                    Toast("Please enter post message.");
                    popUpPostBtn.setVisibility(View.VISIBLE);
                    popUpProgBar.setVisibility(View.INVISIBLE);

                }
                else{
                    popUpPostBtn.setVisibility(View.INVISIBLE);
                    popUpProgBar.setVisibility(View.VISIBLE);

                    Post post = new Post(popupText, userProfile.getDisplayName());
                    addPost(post);

                }


            }
        });

    }

    private void addPost(Post post) {

        DatabaseReference posts = database.getReference("Posts").push();
        String key = posts.getKey();
        post.setPostKey(key);

        posts.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast("Post added successfully.");
                popUpProgBar.setVisibility(View.INVISIBLE);
                popUpPostBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();

            }
        });

    }
}


