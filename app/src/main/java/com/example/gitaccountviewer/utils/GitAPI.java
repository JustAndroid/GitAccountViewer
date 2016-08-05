package com.example.gitaccountviewer.utils;

import com.example.gitaccountviewer.models.GithubUser;
import com.example.gitaccountviewer.models.Repos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Николай on 03.08.2016.
 */

public interface GitAPI {

    @GET("/users/{username}")
    Call<GithubUser> getUser(@Path("username") String username);

    @GET("/users/{username}/repos")
    Call<Repos[]> getRepos(@Path("username") String username);


}

