package com.mpascal.programmaker.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.mpascal.programmaker.R;

import javax.annotation.Nullable;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private boolean isShowing = false;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
        isShowing = true;
    }

    public void dismissLoadingDialog() {
        if (isShowing) {
            dialog.dismiss();
        }
    }
}
