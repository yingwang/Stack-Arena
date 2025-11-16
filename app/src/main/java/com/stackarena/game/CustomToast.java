package com.stackarena.game;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    public static void show(Context context, String message, int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_message);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        // Shorten duration: SHORT = 1 second instead of 2
        toast.setDuration(duration == Toast.LENGTH_LONG ? Toast.LENGTH_SHORT : Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showShort(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT); // Even "long" is now short
    }
}
