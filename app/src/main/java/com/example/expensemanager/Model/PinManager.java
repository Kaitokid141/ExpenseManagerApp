package com.example.expensemanager.Model;



import android.content.Context;
import android.content.SharedPreferences;

public class PinManager {
    private static final String PIN_PREFS_NAME = "PinPreferences";
    private static final String PIN_KEY = "pin_key";

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public PinManager(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PIN_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void savePin(String pin) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PIN_KEY, pin);
        editor.apply();
    }

    public String getPin() {
        return mSharedPreferences.getString(PIN_KEY, "");
    }

    public boolean isPinSet() {
        return !getPin().isEmpty();
    }

    public boolean checkPin(String pin) {
        return getPin().equals(pin);
    }
}