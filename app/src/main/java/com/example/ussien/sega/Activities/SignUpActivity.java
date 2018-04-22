package com.example.ussien.sega.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.ussien.sega.Model.ApiClient;
import com.example.ussien.sega.Model.ApiInterface;
import com.example.ussien.sega.Model.SignUpResult;
import com.example.ussien.sega.Model.Student;
import com.example.ussien.sega.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final  TextInputEditText  emailText = findViewById(R.id.input_email);
        final TextInputEditText  passwordText =  findViewById(R.id.input_password);
        final TextInputEditText   reEnterPasswordText =  findViewById(R.id.input_reEnterPassword);
        final TextInputEditText   nickNameText =  findViewById(R.id.input_username);
        Button signupButton = findViewById(R.id.btn_signup);

        nickNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (nickNameText.getText().toString().isEmpty()) {
                        nickNameText.setError(getResources().getString(R.string.signup_userName_validation));
                    }
                    else
                        nickNameText.setError(null);
                }
            }
        });

        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!emailValid(emailText.getText().toString())) {
                        emailText.setError(getResources().getString(R.string.signup_email_validation));
                    }
                    else
                        emailText.setError(null);
                }
            }
        });

        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!passwordValid(passwordText.getText().toString())) {
                        if (passwordText.getText().toString().isEmpty())
                            passwordText.setError(getResources().getString(R.string.signup_password_validation1));
                        else
                            passwordText.setError(getResources().getString(R.string.signup_password_validation2));
                    }
                    else
                        passwordText.setError(null);
                }
            }
        });

        reEnterPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if (!rePassowordValid(reEnterPasswordText.getText().toString(),passwordText.getText().toString())) {
                        reEnterPasswordText.setError(getResources().getString(R.string.signup_repassword_validation));
                    }
                    else
                        reEnterPasswordText.setError(null);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailValid(emailText.getText().toString()) && passwordValid(passwordText.getText().toString()) &&
                        rePassowordValid(reEnterPasswordText.getText().toString(),passwordText.getText().toString())&&
                        !nickNameText.getText().toString().isEmpty())
                {

                    signUp(emailText,passwordText,nickNameText);
                }
                else
                {
                    if(!emailValid(emailText.getText().toString()))
                        emailText.setError(getResources().getString(R.string.signup_email_validation));
                    if(!passwordValid(passwordText.getText().toString()))
                    {
                        if(passwordText.getText().toString().isEmpty())
                            passwordText.setError(getResources().getString(R.string.signup_password_validation1));
                        else
                            passwordText.setError(getResources().getString(R.string.signup_password_validation2));
                    }
                    if(!rePassowordValid(reEnterPasswordText.getText().toString(),passwordText.getText().toString()))
                        reEnterPasswordText.setError(getResources().getString(R.string.signup_repassword_validation));
                    if(nickNameText.getText().toString().isEmpty())
                        nickNameText.setError(getResources().getString(R.string.signup_userName_validation));
                }
            }
        });
    }

    private boolean emailValid (String email) {
        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean passwordValid (String password) {
        return !password.isEmpty() && (password.length() >= 6 && password.length() <= 45);
    }

    private boolean rePassowordValid (String rePassword, String password) {
        return !rePassword.isEmpty() &&(rePassword.length() >= 6 && rePassword.length() <= 45) &&
                (rePassword.equals(password));
    }

    private void showAlertDialogue()
    {
        final android.app.AlertDialog.Builder dialogueBuilder = new android.app.AlertDialog.Builder(this);
        String msg = getResources().getString(R.string.alert_dialogue);
        dialogueBuilder.setMessage(msg);
        String positive = getResources().getString(R.string.alert_dialogue_ok);
        dialogueBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = dialogueBuilder.create();
        alertDialog.show();
    }
    private void saveUser(int userID, String userMail, String nickName){
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Student student = new Student("","",nickName,
                "https://secure.gravatar.com/avatar/3f226ac1c95796790b88a21bb7778fc8?s=100&d=mm",userID,userMail);
        Gson gson = new Gson();
        String json = gson.toJson(student);
        editor.putString("student", json);
        editor.apply();
    }

    private void signUp(final TextInputEditText emailText, TextInputEditText passwordText , TextInputEditText nickNameText)
    {
        final android.app.AlertDialog dialog = new SpotsDialog(this);
        dialog.show();
        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        final String nickName = nickNameText.getText().toString();
        ApiInterface   apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<SignUpResult> call = apiInterface.signUp(email,password,nickName);
        call.enqueue(new Callback<SignUpResult>() {
            @Override
            public void onResponse(Call<SignUpResult> call, Response<SignUpResult> response) {
                dialog.dismiss();
                SignUpResult signUpResult = response.body();
                if(signUpResult.getValid().equals("true"))
                {
                    saveUser(signUpResult.getUserID(),email,nickName);
                    finish();
                }
                else
                {
                    emailText.setText("");
                    emailText.setError(getResources().getString(R.string.signup_email_error));
                }
            }
            @Override
            public void onFailure(Call<SignUpResult> call, Throwable t) {
                dialog.dismiss();
                showAlertDialogue();
            }
        });
    }
}
