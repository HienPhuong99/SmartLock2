package com.lyoko.smartlock.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.lyoko.smartlock.Activities.AuthenticationActivity;
import com.lyoko.smartlock.R;


public class SignUpDialog extends AppCompatDialogFragment {


    public TextView tvSDT;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.sign_in_dialog, null);
        tvSDT = view.findViewById(R.id.tv_register_dialog);
        Bundle bundle = getArguments();
        final String uid = bundle.getString("phoneNum");
        tvSDT.setText(uid);
        builder.setView(view)
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                        intent.putExtra("UID", uid);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }
}
