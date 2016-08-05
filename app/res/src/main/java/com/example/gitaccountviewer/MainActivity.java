package com.example.gitaccountviewer;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.gitaccountviewer.utils.GitAPI;
import com.example.gitaccountviewer.utils.GithubUser;
import com.example.gitaccountviewer.utils.Repos;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         String BASE_URL = "https://api.github.com" ;

        Retrofit client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitAPI service = client.create(GitAPI.class);
        Call<Repos[]> call = service.getRepos("JustAndroid");

        call.enqueue(new Callback<Repos[]>() {
            @Override
            public void onResponse(Call<Repos[]> call, Response<Repos[]> response) {
                if (response.isSuccessful()) {
                    // request successful (status code 200, 201)

                    System.out.println(response.body().length);
                } else {
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                    System.out.println("EEEEERRRROOOOORRR");
                }

            }

            @Override
            public void onFailure(Call<Repos[]> call, Throwable t) {
                System.out.println("EEEEERRRROOOOORRR222222222222222");
            }
        });
    }



}
