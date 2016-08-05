package com.example.gitaccountviewer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gitaccountviewer.models.Repos;
import com.example.gitaccountviewer.utils.ConnectManager;
import com.example.gitaccountviewer.utils.GitAPI;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Николай on 04.08.2016.
 */
public class AccountInfoActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    private TextView nameUserTextView;
    private TextView infoUserTextView;
    private SimpleDraweeView avatar;
    private RecyclerView recyclerView;
    private Repos[] repositories;
    private ProgressDialog progressDialog;
    private static AlertDialog.Builder builder;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//todo add checking ratate device

        Fresco.initialize(this);
        setContentView(R.layout.activity_account_info);
        intent = getIntent();
        findViews();
        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(AccountInfoActivity.this);
        setSupportActionBar(toolbar);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);
        coverImage.setImageResource(R.drawable.cover);
        progressDialog = new ProgressDialog(AccountInfoActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Senting request.");
        progressDialog.show();
        getRepositiries();
        builder = new AlertDialog.Builder(AccountInfoActivity.this);
        nameUserTextView.setText(intent.getStringExtra(MainActivity.USER_NAME));
        textviewTitle.setText(intent.getStringExtra(MainActivity.USER_NAME));
        infoUserTextView.setText("Email: " + intent.getStringExtra(MainActivity.USER_EMAIL));
    }

    private void findViews() {
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        coverImage = (ImageView) findViewById(R.id.imageview_placeholder);
        framelayoutTitle = (FrameLayout) findViewById(R.id.framelayout_title);
        linearlayoutTitle = (LinearLayout) findViewById(R.id.linearlayout_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textviewTitle = (TextView) findViewById(R.id.textview_title);
        avatar = (SimpleDraweeView) findViewById(R.id.avatar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        nameUserTextView = (TextView) findViewById(R.id.name_user);
        infoUserTextView = (TextView) findViewById(R.id.information_text_view);
    }

    void getRepositiries() {
        Retrofit client = new Retrofit.Builder()
                .baseUrl(ConnectManager.DOMAIN_NAME)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitAPI service = client.create(GitAPI.class);

        Call<Repos[]> call = service.getRepos(intent.getStringExtra(MainActivity.USER_NAME));

        call.enqueue(new Callback<Repos[]>() {
            @Override
            public void onResponse(Call<Repos[]> call, Response<Repos[]> response) {
                if (response.isSuccessful()) {

                    repositories = response.body();
                    avatar.setImageURI(Uri.parse(intent.getStringExtra(MainActivity.LINK_AVATAR)));

                    progressDialog.dismiss();
                    recyclerView.setLayoutManager(new LinearLayoutManager(AccountInfoActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(new RepositoriesAdapter());
                    recyclerView.setNestedScrollingEnabled(false);
                } else {
                    progressDialog.dismiss();
                    builder.setTitle("Error!")
                            .setMessage("Error request.")
                            .setCancelable(false)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }

            }

            @Override
            public void onFailure(Call<Repos[]> call, Throwable t) {
                builder.setTitle("Error!")
                        .setMessage("Error request.")
                        .setCancelable(false)
                        .setNegativeButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private class RepositoriesAdapter extends RecyclerView.Adapter<Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(AccountInfoActivity.this).inflate(R.layout.repositor_item, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

            holder.nameRepo.append(repositories[position].getName());
            if (repositories[position].getLanguage() != null) {
                holder.languageRepo.append(repositories[position].getLanguage());
            } else {
                holder.languageRepo.append("not hawe");
            }

        }

        @Override
        public int getItemCount() {
            return repositories.length;
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        TextView nameRepo;
        TextView languageRepo;


        public Holder(View itemView) {
            super(itemView);

            nameRepo = (TextView) itemView.findViewById(R.id.name_repos);
            languageRepo = (TextView) itemView.findViewById(R.id.language_repos);
        }
    }
}
