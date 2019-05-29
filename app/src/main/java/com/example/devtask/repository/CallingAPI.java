package com.example.devtask.repository;

import com.example.devtask.interfaces.PassDataInterface;
import com.example.devtask.interfaces.RetrofitInterface;
import com.example.devtask.models.TaskModel;
import com.example.devtask.utils.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CallingAPI {

     PassDataInterface passDataInterface;
     RetrofitInterface retrofitInterface;
     Retrofit retrofit;

    public CallingAPI(PassDataInterface passDataInterface) {
        this.passDataInterface = passDataInterface;
    }



    Call<ArrayList<TaskModel>> taskDataCall;
    ArrayList<TaskModel> taskData;
    public void getData(String accessToken, int page, int per_page) {

        retrofit = RetrofitClient.getClient("https://api.github.com");
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        taskDataCall = retrofitInterface.getData(accessToken, page, per_page);
        taskDataCall.enqueue(new Callback<ArrayList<TaskModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TaskModel>> call, Response<ArrayList<TaskModel>> response) {

                if (!call.isCanceled()) {
                    if (response.isSuccessful()) {

                        taskData = response.body();
                        passDataInterface.passData(taskData , "success");

                    } else passDataInterface.passData(taskData , "unsuccess");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TaskModel>> call, Throwable t) {

                if (!call.isCanceled()) passDataInterface.passData(taskData , "failed");
            }
        });
    }


}
