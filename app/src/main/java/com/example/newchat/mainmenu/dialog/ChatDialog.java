package com.example.newchat.mainmenu.dialog;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChatDialog extends AlertDialog {

    private EditText editTextDialog;
    private Button buttonDialog;

    protected ChatDialog(@NonNull Context context) {
        super(context);
    }

    protected ChatDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ChatDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
