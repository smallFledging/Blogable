package com.example.blogable;
import com.google.firebase.database.ServerValue;

public class Post {

    private String postKey;
    private String message;
    private String username;
    private Object timeStamp;


    public Post(String message, String username) {
        this.message = message;
        this.username = username;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Post(){

    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

}
