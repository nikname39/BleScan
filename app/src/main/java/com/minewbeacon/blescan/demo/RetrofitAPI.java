package com.minewbeacon.blescan.demo;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("/posts")
    Call<List<Post>> getData(@Query("userId") String id);

    @FormUrlEncoded
    @POST("/posts")
    Call<Post> postData(@FieldMap HashMap<String, Object> param);

}
