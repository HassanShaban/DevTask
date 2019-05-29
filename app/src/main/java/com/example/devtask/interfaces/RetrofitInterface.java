package com.example.devtask.interfaces;

import com.example.devtask.models.TaskModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {


    @GET("user/repos")
    Call<ArrayList<TaskModel>> getData(@Query("access_token") String access_token , @Query("page") int page , @Query("per_page") int per_page );

}
