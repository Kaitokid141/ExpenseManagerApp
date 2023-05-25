package com.example.expensemanager;

import android.app.Application;
import android.os.Bundle;

import com.github.omadahealth.lollipin.lib.managers.LockManager;

public class PinLock extends Application {
    @Override
    public void onCreate(){
       super.onCreate();
        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setTimeout(10000);
        lockManager.getAppLock().setLogoId(R.drawable.padlock);
    }
}
