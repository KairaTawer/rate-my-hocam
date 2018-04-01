package kz.sdu.kairatawer.ratemyhocam.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import kz.sdu.kairatawer.ratemyhocam.R;

public class AuthDialog {

    public void showDialog(Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.auth_dialog);

        TextView text = dialog.findViewById(R.id.textView_error);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.button_ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}