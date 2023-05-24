package com.example.expensemanager.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.example.expensemanager.HomeActivity;
import com.example.expensemanager.R;
import com.google.firebase.auth.FirebaseAuth;

public class PinActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPassword;
    private Button loginButton;
    private TextView mForgotPassword;
    private TextView mSignup;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;

    private int counter = 5;
    private long lockTimeMillis = 0L;
    private PinManager mPinManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PFLockScreenFragment fragment = new PFLockScreenFragment();
        PFFLockScreenConfiguration.Builder builder = new PFFLockScreenConfiguration.Builder(this)
                .setMode(PFFLockScreenConfiguration.MODE_CREATE);
        fragment.setConfiguration(builder.build());
        fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
            @Override
            public void onCodeCreated(String encodedCode) {
                //TODO: save somewhere;
            }

            @Override
            public void onNewCodeValidationFailed() {

            }
        });
        mAuth = FirebaseAuth.getInstance();
        mPinManager = new PinManager(this);

//        if (mAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//            finish();
//        } else
            if (!mPinManager.isPinSet()) {
            // Nếu PIN chưa được tạo, chuyển đến màn hình tạo PIN
            setContentView(R.layout.pin_create_layout);
            setupPinCreateLayout();
        } else {
            // Nếu PIN đã được tạo, chuyển đến màn hình xác thực PIN
            setContentView(R.layout.pin_authentication_layout);
            setupPinAuthenticationLayout();
        }

        mDialog = new ProgressDialog(this);
    }

    private void setupPinCreateLayout() {
        EditText pinEditText = findViewById(R.id.pin_create_edittext);
        Button createButton = findViewById(R.id.pin_create_button);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEditText.getText().toString().trim();

                if (TextUtils.isEmpty(pin)) {
                    Toast.makeText(getApplicationContext(), "Please enter a PIN", Toast.LENGTH_SHORT).show();
                    return;
                }

                mPinManager.savePin(pin);
                Toast.makeText(getApplicationContext(), "PIN created successfully", Toast.LENGTH_SHORT).show();

                // Chuyển đến màn hình xác thực PIN
                setContentView(R.layout.pin_authentication_layout);
                setupPinAuthenticationLayout();
            }
        });
    }

    private void setupPinAuthenticationLayout() {
        EditText pinEditText = findViewById(R.id.pinEditText);
        Button authenticateButton = findViewById(R.id.submitButton);

        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEditText.getText().toString().trim();

                if (TextUtils.isEmpty(pin)) {
                    Toast.makeText(getApplicationContext(), "Please enter your PIN", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPinManager.checkPin(pin)) {
                    Toast.makeText(getApplicationContext(), "PIN authentication successful", Toast.LENGTH_SHORT).show();
                    finish();
                    // Chuyển đến màn hình HomeActivity
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    counter--;
                    if (counter == 0) {
                        // Khóa tài khoản trong 3 phút
                        lockTimeMillis = System.currentTimeMillis() + 3 * 60 * 1000;
                        counter = 5;
                        Toast.makeText(getApplicationContext(), "Your account has been locked. Please try again later.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect PIN! You have " + counter + " attempts left.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}