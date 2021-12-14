package edu.upc.epsevg.damo.a08_christmas_defender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DialogManager {
    SharedPreferences prefs;
    Context context;

    public DialogManager(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void showLosePopup() {
        new AlertDialog.Builder(context)
                .setTitle("You lost!")
                .setMessage("Christmas is over, you made it")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        prefs.edit().putBoolean("isShown", false).apply();

                        // Return to main menu
                        ((Activity) context).finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
