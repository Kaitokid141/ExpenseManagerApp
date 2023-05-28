package com.example.expensemanager.Pin;

import android.app.Application;

import com.example.expensemanager.R;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

public class PinLock extends Application {
    @Override
    public void onCreate(){
       super.onCreate();
        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setTimeout(1000);
        lockManager.getAppLock().setLogoId(R.drawable.padlock);
    }
}
