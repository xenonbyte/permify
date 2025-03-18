package com.xenonbyte.permify.sample.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.xenonbyte.permify.PermissionRationale;
import com.xenonbyte.permify.PermissionRationaleHandler;
import com.xenonbyte.permify.PermissionRequest;
import com.xenonbyte.permify.sample.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sms_perm_btn).setOnClickListener(v ->
                // Simple permission request example (directly requesting permission)
                PermissionRequest.with(MainActivity.this)
                        .addPermissions(Manifest.permission.READ_SMS)
                        .onResult((success, grantPerms, denyPerms, denyForeverPerms) -> {
                            String msg = success ? "SMS permission granted" : "SMS permission denied";
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        })
                        .request());
        findViewById(R.id.location_perm_btn).setOnClickListener(v ->
                // Permission request with rationale UI (shows a dialog if the user initially denies the request)
                PermissionRequest.with(MainActivity.this)
                        .addPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .onResult(new PermissionRationale() {
                            @Override
                            public void showRationaleUI(@NonNull Context context, @NonNull PermissionRationaleHandler callback) {
                                // Show a rationale dialog to explain why this permission is needed
                                new AlertDialog.Builder(context)
                                        .setMessage("This permission is required to provide location services.")
                                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                callback.onDenied();
                                            }
                                        })
                                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                callback.onAccepted();
                                            }
                                        })
                                        .show();
                            }

                            @Override
                            public void onResult(boolean success, @NonNull List<String> grantPerms, @NonNull List<String> denyPerms, @NonNull List<String> denyForeverPerms) {
                                String msg = success ? "Location permission granted" : "Location permission denied";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .request());
    }
}
