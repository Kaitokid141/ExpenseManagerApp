package com.example.expensemanager;
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

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Date;
public class MainActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPassword;
    private Button loginButton;
    private TextView mForgotPassword;
    private TextView mSignup;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;

    private int counter = 5;
    private long lockTimeMillis = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        mDialog = new ProgressDialog(this);
        login();
    }

<<<<<<< HEAD
    private void login(){
        mEmail=findViewById(R.id.email_login);
        mPassword=findViewById(R.id.password_login);
        loginButton=findViewById(R.id.btn_login);
        mForgotPassword=findViewById(R.id.forgot_password);
        mSignup=findViewById(R.id.signup);

=======
    // Login
    private void login(){
        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);
        loginButton = findViewById(R.id.btn_login);
        mForgotPassword = findViewById(R.id.forgot_password);
        mSignup = findViewById(R.id.signup);
>>>>>>> 302152d96640eff829562d1e9f8c6c8682348741
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email cannot be empty. Please enter a valid Email id");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password cannot be empty.");
                    return;
                }
<<<<<<< HEAD

                // Check if the account is locked
                if(lockTimeMillis > System.currentTimeMillis()) {
                    Toast.makeText(getApplicationContext(),"Your account has been locked. Please try again later.",Toast.LENGTH_SHORT).show();
                    return;
                }

=======
>>>>>>> 302152d96640eff829562d1e9f8c6c8682348741
                mDialog.setMessage("Processing");
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Login Successful!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                        else{
                            mDialog.dismiss();
                            counter--;
                            if (counter == 0) {
                                // Lock the account for 3 minutes
                                lockTimeMillis = System.currentTimeMillis() + 3 * 60 * 1000;
                                counter = 5;
                                Toast.makeText(getApplicationContext(),"Your account has been locked. Please try again later.",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),"Login Failed! You have " + counter + " attempts left.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        // SignUp activity
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        // Reset password activity
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResetPasswordActivity.class));
            }
        });

    }
}