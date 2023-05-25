package com.example.expensemanager.Model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public MyFirebaseMessagingService(DatabaseReference mDatabase){
        this.mDatabase = mDatabase;
    }
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        saveTokenToDatabase(token);
    }
    public void saveTokenToDatabase(String token) {

        mDatabase.push().setValue(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            String title = notification.getTitle();
            String body = notification.getBody();

            // Xử lý thông báo tại đây
            // Ví dụ: hiển thị thông báo trong thanh thông báo của hệ thống
            showNotification(title, body);
        }
    }

    public void showNotification(String title, String body) {
        // Code để hiển thị thông báo trong thanh thông báo của hệ thống
        // ...
    }
}
