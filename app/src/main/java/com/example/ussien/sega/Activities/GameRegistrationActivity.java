package com.example.ussien.sega.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ussien.sega.Model.ApiClient;
import com.example.ussien.sega.Model.ApiInterface;
import com.example.ussien.sega.Model.GameRegisterResult;
import com.example.ussien.sega.Model.Student;
import com.example.ussien.sega.R;
import com.google.gson.Gson;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class GameRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_registration);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

        final EditText editText = findViewById(R.id.game_register_text);
        Button button = findViewById(R.id.game_register_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length()==0)
                    editText.setError(getResources().getString(R.string.game_register_empty));
                else
                {
                    int studentID = loadStudent().getStudentID();
                    register(editText,studentID);
                }
            }
        });

    }
    private void register(final EditText editText, int userID)
    {
        final AlertDialog dialog = new SpotsDialog(this);
        dialog.show();
        String code = editText.getText().toString();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<GameRegisterResult> call = apiInterface.registerGame(code,userID);
        call.enqueue(new Callback<GameRegisterResult>() {
            @Override
            public void onResponse(Call<GameRegisterResult> call, Response<GameRegisterResult> response) {
                dialog.dismiss();
                GameRegisterResult gameRegisterResult = response.body();
                if(gameRegisterResult.getValid().equals("true"))
                {
                    String text = getResources().getString(R.string.register_toast);
                    Toast toast = Toast.makeText(GameRegistrationActivity.this,text,LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    if(gameRegisterResult.getError().equals("not found"))
                        editText.setError(getResources().getString(R.string.game_register_notfound));
                    else if (gameRegisterResult.getError().equals("already registered"))
                        editText.setError(getResources().getString(R.string.game_register_alreadyRegistered));
                }
            }

            @Override
            public void onFailure(Call<GameRegisterResult> call, Throwable t) {
                dialog.dismiss();
                showAlertDialogue();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            case R.id.sign_out:
                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                sharedPref.edit().clear().commit();
                Intent intent = new Intent(GameRegistrationActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
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
}
