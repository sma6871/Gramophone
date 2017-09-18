package com.rahnema.gramophone.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.onurciner.toastox.ToastOX;
import com.rahnema.gramophone.enums.MessageType;

import butterknife.ButterKnife;

/**
 * @author Payam1991 on 9/18/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.bind(this);
        onViewReady(savedInstanceState, getIntent());
    }

    public void onViewReady(@Nullable Bundle savedInstanceState, Intent intent) {
        //To be used by child activities
    }

    // Show toast Message by string id from resources
    public void showMessage(MessageType type, int messageResourceId) {
        showMessage(type, getString(messageResourceId));
    }

    /**
     * Show toast message
     *
     * @param message: message to show
     * @param type: type of message, contains: info, success, error and warning
     */

    public void showMessage(MessageType type, String message) {
        switch (type) {

            case Success:
                ToastOX.ok(this, message);
                break;
            case Error:
                ToastOX.error(this, message);
                break;
            case Info:
                ToastOX.info(this, message);
                break;
            case Warning:
                ToastOX.warning(this, message);
                break;
        }
    }

    /**
     * Abstract Methods
     */
    protected abstract int getContentView();

}
