package com.example.expensemanager.Pin;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.R;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

public class CustomPinActivity extends AppLockActivity {
    @Override
    public void showForgotDialog() {
        TextView forgotView = findViewById(R.id.pin_code_forgot_textview);
        forgotView.setText("KHÔNG THỂ ĐỔI!");
    }

    @Override
    public void onPinFailure(int attempts) {
        Toast.makeText(this,"Wrong!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinSuccess(int attempts) {
        Toast.makeText(this,"Correct Pin",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getPinLength() {
        return 4;
    }
}
