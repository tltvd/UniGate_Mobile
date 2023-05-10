package com.example.unigate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unigate.models.PackageData;
import com.example.unigate.models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    public static User user;
    private static final String SHARED_PREFS_FILE = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.IINEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty()) {
                    // Проверка на пустое поле usernameEditText
                    Toast.makeText(LoginActivity.this, "Введите имя пользователя", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    // Проверка на пустое поле пароля
                    Toast.makeText(LoginActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
                } else if (isUserLoggedIn()) {
                    // User is already logged in, skip the login process
                    Toast.makeText(LoginActivity.this, "User is already logged in", Toast.LENGTH_SHORT).show();
                    redirectToMainActivity();
                } else if (isValidCredentials(username, password)) {
                    // Валидные учетные данные, выполняем вход
                    try {
                        loginUser(username, password);
                        redirectToMainActivity();
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Неверные учетные данные, отображаем сообщение об ошибке
                    Toast.makeText(LoginActivity.this, "Неверные учетные данные", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidCredentials(String username, String password) {
        // Проверяем, что учетные данные валидны (например, сравниваем с базой данных или хранилищем)
        // Здесь вы можете реализовать свою логику проверки учетных данных

        // В примере просто проверяем, что поля не пустые
        return !username.isEmpty() && !password.isEmpty();
    }

    private void loginUser(String username, String password) throws NoSuchAlgorithmException {
        User user_check = new User();
        user_check.setUsername(username);
        Security security = new Security();

        user_check.setPassword(Security.hashPassword(password, "8fa985e47a9d6f1bd3bbb75427442f6b"));
        PackageData pd = new PackageData("SIGN_IN_MOBILE", user_check);
        connect(LoginActivity.this, pd);

        // Save username and password to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        // Check if the user is already logged in by checking SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        // Return true if both username and password are not empty
        return !username.isEmpty() && !password.isEmpty();
    }

    public void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static void connect(Activity activity, PackageData pd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("192.168.1.123", 3489);
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                    if (pd.getOperationType().equals("SIGN_IN_MOBILE")) {
                        outputStream.writeObject(pd);
                        PackageData infoFromServer = (PackageData) inputStream.readObject();
                        user = infoFromServer.getUser();
                    } else if (pd.getOperationType().equals("OPEN_DOOR_MOBILE")) {
                        outputStream.writeObject(pd);
                        PackageData infoFromServer = (PackageData) inputStream.readObject();
                        MainActivity.door = infoFromServer.getDoor();
                    }

                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Error!")
                                    .setMessage("Problem connecting to the server!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Handle the error or display an appropriate message
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}






