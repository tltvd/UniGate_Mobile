package com.example.unigate;

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

import androidx.biometric.BiometricPrompt; // импортируем класс BiometricPrompt из androidx.biometric пакета

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    Button scan_btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showBiometricPrompt();

        scan_btn = findViewById(R.id.scanner);
        textView = findViewById(R.id.text);

        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setOrientationLocked(false); // изменить на false
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
                        String serverAddress = "192.168.88.95"; // адрес сервера
                        int serverPort = 3489; // порт сервера

                        try {
                            // Соединяемся с сервером
                            Socket socket = new Socket(serverAddress, serverPort);

                            // Отправляем сообщение на сервер
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println(doorNumber);

                            // Закрываем соединение
                            socket.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

                textView.setText(contents);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}