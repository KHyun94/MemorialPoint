package com.example.memorialpoint;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitService {

    @FormUrlEncoded
   @POST("loginUser.php")
    Call<List<Login_Data>> getLogin(@FieldMap Map<String, String> option);

    @FormUrlEncoded
    @POST("overlap_check.php")
    Call<String> getOverlap(@Field("id") String id);

   @FormUrlEncoded
   @POST("insertUser.php")
    Call<Data_User> setUserData(@FieldMap Map<String, String> option);

    @FormUrlEncoded
    @POST("user.php")
    Call<List<Data_User>> getUser(@Field("no") String no);

}
