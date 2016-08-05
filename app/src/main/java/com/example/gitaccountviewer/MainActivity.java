package com.example.gitaccountviewer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.gitaccountviewer.models.GithubUser;
import com.example.gitaccountviewer.utils.ConnectManager;
import com.example.gitaccountviewer.utils.GitAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    public static final String LINK_AVATAR = "ava";
    public static final String ENTERED_USER_NAME = "entered_name";
    public static final String USER_NAME = "name";
    public static final String USER_GIT_NAME = "git_name";
    public static final String USER_EMAIL = "email";
    public static final String USER_COMPANY = "company";
    //todo Add all information
    private static AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        builder = new AlertDialog.Builder(MainActivity.this);
    }

    private void findViews() {
        editText = (EditText) findViewById(R.id.text_view_search);
    }

    private void searching() {

        Retrofit client = new Retrofit.Builder()
                .baseUrl(ConnectManager.DOMAIN_NAME)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitAPI service = client.create(GitAPI.class);
        String s2 = editText.getText().toString();
        System.out.println(s2);
        Call<GithubUser> call = service.getUser(s2);
        call.enqueue(new Callback<GithubUser>() {
            @Override
            public void onResponse(Call<GithubUser> call, Response<GithubUser> response) {
                if (response.isSuccessful()) {

                    Intent intent = new Intent(MainActivity.this, AccountInfoActivity.class);

    intent.putExtra(LINK_AVATAR, response.body().getAvatarUrl());

    if(response.body().getName() != null){
        intent.putExtra(USER_NAME, response.body().getName());
    }else{
        intent.putExtra(USER_NAME, response.body().getLogin());

    }

                    intent.putExtra(ENTERED_USER_NAME, editText.getText().toString());
//                    intent.putExtra(USER_GIT_NAME, response.body().getName());
                    if(response.body().getCompany() != null) {
                        intent.putExtra(USER_COMPANY, response.body().getCompany());
                    }
                    else{
                        intent.putExtra(USER_COMPANY, "Not have company");
                    }
                    if(response.body().getCompany() != null) {
                        intent.putExtra(USER_EMAIL, response.body().getEmail());
                    }
                    else{
                        intent.putExtra(USER_EMAIL, "Not have email");
                    }
                    progressDialog.hide();
                    startActivity(intent);
                } else {
                    progressDialog.hide();
                    showInfoDialog("Error!", "User not fount.", "Ok!");
                }

            }

            @Override
            public void onFailure(Call<GithubUser> call, Throwable t) {
                progressDialog.hide();
                showInfoDialog("Error!", "The request failed.", "Ok!");
            }
        });

    }

    public void searchAccount(View view) {
        if (ConnectManager.isConnected(getApplicationContext())) {
            searching();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Senting request.");
            progressDialog.show();
        } else {

            showInfoDialog("Error!", "Check internet connection.", "Ok!");
        }

    }

    private void showInfoDialog(String title, String message, String buttonText) {

        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(buttonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
