package com.example.unigate;

import static com.example.unigate.LoginActivity.connect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.biometric.BiometricPrompt;

import com.example.unigate.models.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {

    public static Door door;
    Button scan_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showBiometricPrompt();

        scan_btn = findViewById(R.id.scanner);


        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("Scan a QR CODE");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentIntegrator.initiateScan();
            }
        });


    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Аутентификация с помощью отпечатка пальца")
                .setNegativeButtonText("Отмена")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                // Аутентификация прошла успешно, продолжаем работу приложения
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Аутентификация не удалась, попробуйте еще раз
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String contents = intentResult.getContents();
            if (contents != null) {
                // Показать диалоговое окно
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Вы действительно хотите открыть дверь с этим кодом: " + contents);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String doorNumber = contents; // номер двери
                        Door door = new Door();
                        door.setLocation(doorNumber);
                        PackageData packageData = new PackageData();
                        packageData.setOperationType("OPEN_DOOR_MOBILE");
                        packageData.setDoor(door);
                        packageData.setUser(LoginActivity.user);
                        connect(MainActivity.this, packageData);
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}