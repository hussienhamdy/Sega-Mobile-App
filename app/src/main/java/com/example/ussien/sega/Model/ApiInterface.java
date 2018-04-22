package com.example.ussien.sega.Model;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("loadGames")
    Call<ArrayList<Game>> loadGames(@Query("userID") int userID);

    @POST("signUp")
    Call<SignUpResult> signUp(@Query("email") String email, @Query("password") String password, @Query("nickName")String nickName);

    @POST("signIn")
    Call<LoginResult> login(@Query("email") String email, @Query("password") String password);

    @POST("registerGame")
    Call<GameRegisterResult> registerGame(@Query("gameCode")String gameCode,@Query("userID") int userID);

    @POST("saveProfilePicture")
    @Multipart
    Call<ResponseBody> saveProfilePicture(@Query("userID") int userID,
                                          @Part MultipartBody.Part profilePicture,@Part("profilePicture") RequestBody name);

    @POST("loadNews")
    Call<ArrayList<News>> loadNews(@Query("gameID") int gameID);

    @POST("scoreboard")
    Call<ArrayList<ScoreBoard>> loadscoreBoard(@Query("gameID") int gameID);


    @POST("saveProfile")
    Call<ResponseBody> saveProfile(@Query("userID") int userID, @Query("firstName") String fname,
                                   @Query("lastName") String lname, @Query("nickName") String nickName,
                                   @Query("phone") String phone);
}
