package com.example.ussien.sega.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ussien.sega.Model.ApiClient;
import com.example.ussien.sega.Model.ApiInterface;
import com.example.ussien.sega.Model.Student;
import com.example.ussien.sega.Model.TwitterClient;
import com.example.ussien.sega.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final  TextInputEditText fname =  (TextInputEditText) findViewById(R.id.fname_input);
        final  TextInputEditText lname =  (TextInputEditText)findViewById(R.id.lname_input);
        final  TextInputEditText email =  (TextInputEditText)findViewById(R.id.email_input);
        final  TextInputEditText phone =  (TextInputEditText)findViewById(R.id.phone_input);
        final  TextInputEditText userName=(TextInputEditText)findViewById(R.id.username_input);
        final Button save = (Button)findViewById(R.id.save_button);
        final SimpleDraweeView simpleDraweeView = (SimpleDraweeView)findViewById(R.id.profile_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

        getProfileInfo(fname,lname,email,phone,userName,simpleDraweeView);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FNameValid(fname.getText().toString()) && LNameValid(lname.getText().toString())
                        && MobNumberValid(phone.getText().toString())) {
                    String firstName = fname.getText().toString();
                    String lastName  = lname.getText().toString();
                    String nickName  = userName.getText().toString();
                    String mobNumber = phone.getText().toString();
                    saveData(firstName,lastName,nickName,mobNumber);
                }
            }
        });


        ImageView imageView =  findViewById(R.id.submit);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (FNameValid(fname.getText().toString())) {
                    fname.setError(null);
                } else {
                    fname.setError(getResources().getString(R.string.profile_fname_error));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (LNameValid(lname.getText().toString())) {
                    lname.setError(null);
                } else {
                    lname.setError(getResources().getString(R.string.profile_lname_error));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (MobNumberValid(phone.getText().toString())) {
                    phone.setError(null);
                } else {
                    phone.setError(getResources().getString(R.string.profile_phone_error));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode==SELECT_PICTURE && resultCode==RESULT_OK && imageReturnedIntent!=null) {
            Uri selectedImage = imageReturnedIntent.getData();
            String imagepath = getRealPathFromURIPath(selectedImage);
            saveProfilePhoto(imagepath);
        }
    }

    private String getRealPathFromURIPath(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public boolean FNameValid(String FName) {
        for (int i = 0; i < FName.length(); i++) {
            if (Character.isDigit(FName.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean LNameValid(String LName) {
        for (int i = 0; i < LName.length(); i++) {
            if (Character.isDigit(LName.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean MobNumberValid(String MobNumber) {
        return !(MobNumber.length() > 11);
    }

    private void getProfileInfo(TextInputEditText fname, TextInputEditText lname,
                                TextInputEditText email, TextInputEditText phone,
                                TextInputEditText nickname, SimpleDraweeView simpleDraweeView)
    {
        Student student = loadStudent();
        fname.setText(student.getFirstName());
        lname.setText(student.getLastName());
        email.setText(student.getEmail());
        email.setEnabled(false);
        phone.setText(student.getPhone());
        nickname.setText(student.getNickName());
        Uri uri = Uri.parse(student.getProfilePicture());
        simpleDraweeView.setImageURI(uri);
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

    private void saveProfilePhoto(final String imagePath)
    {
        final Student student = loadStudent();
        final int userID = student.getStudentID();

        final File file = new File(imagePath);

        RequestParams params = new RequestParams();
        try {
            params.put("profilePicture",file);
            params.put("userID",userID);
        } catch(FileNotFoundException e) {}
        TwitterClient.post("saveProfilePicture",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(ProfileActivity.this,response.getString("url"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(ProfileActivity.this,"failure",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveData(final String  fname, final String lname, final String nickName, final String phone)
    {
        final Student student = loadStudent();
        int userID = student.getStudentID();
        final AlertDialog dialog = new SpotsDialog(this);
        dialog.show();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.saveProfile(userID,fname,lname,nickName,phone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                student.setFirstName(fname);
                student.setLastName(lname);
                student.setNickName(nickName);
                student.setPhone(phone);
                saveUser(student);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showAlertDialogue();
            }
        });
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
                Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
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

}
