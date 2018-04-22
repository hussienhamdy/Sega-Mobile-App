package com.example.ussien.sega.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ussien.sega.Model.ApiClient;
import com.example.ussien.sega.Model.ApiInterface;
import com.example.ussien.sega.Model.LoginResult;
import com.example.ussien.sega.Model.Student;
import com.example.ussien.sega.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextInputEditText emailText = findViewById(R.id.input_email) ;
        final TextInputEditText passwordText = findViewById(R.id.input_password);
        Button loginButton = findViewById(R.id.btn_login);
        final TextView signupLink = findViewById(R.id.link_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail(emailText.getText().toString()) &&
                        validatePass(passwordText.getText().toString())) {
                    login(emailText,passwordText);
                }
                else
                {
                    if(!validateEmail(emailText.getText().toString()))
                    {
                        String error=getResources().getString(R.string.email_validation);
                        emailText.setError(error);

                    }
                    if(!validatePass(passwordText.getText().toString()))
                    {
                        String error=getResources().getString(R.string.password_validation);
                        passwordText.setError(error);
                    }
                }
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    private void login(final TextInputEditText emailText , final TextInputEditText passwordText) {
        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        final AlertDialog dialog = new SpotsDialog(this);
        dialog.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<LoginResult> call = apiInterface.login(email,password);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                dialog.dismiss();
                LoginResult loginResult = response.body();
                if(loginResult.getValid().equals("true"))
                {
                    saveUser(loginResult.getStudent());
                    Intent intent = new Intent(LoginActivity.this,GameListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    if(loginResult.getError().equals("email"))
                    {
                        String error = getResources().getString(R.string.login_email_error);
                        emailText.setError(error);
                        emailText.setText("");
                        passwordText.setText("");
                    }
                    else
                    {
                        String error = getResources().getString(R.string.login_password_error);
                        passwordText.setError(error);
                        passwordText.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                dialog.dismiss();
                showAlertDialogue();
            }
        });
    }

    private boolean validatePass (String password) {
        return !password.isEmpty();
    }

    private boolean validateEmail (String email) {
        return  !email.isEmpty();
    }

    public void saveUser(Student student)
    {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(student);
        editor.putString("student", json);
        editor.apply();
    }

    private void showAlertDialogue()
    {
        final AlertDialog.Builder dialogueBuilder = new AlertDialog.Builder(this);
        String msg = getResources().getString(R.string.alert_dialogue);
        dialogueBuilder.setMessage(msg);
        String positive = getResources().getString(R.string.alert_dialogue_ok);
        dialogueBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialogueBuilder.create();
        alertDialog.show();
    }

    // after clicking on the signup link and creating an account now signup activity finishes
    // and loginactivity is resumed so it have to finish also and start GameListActivity
    @Override
    protected void onResume() {
        super.onResume();
       if(loadStudent()!=null)
        {
            Intent intent = new Intent(LoginActivity.this,GameListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Student loadStudent()
    {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(sharedPref.getAll().size()==0)
            return null;
        Gson gson = new Gson();
        String json = sharedPref.getString("student", "");
        return gson.fromJson(json, Student.class);
    }
}
