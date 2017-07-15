package com.angeloparenteapp.upcomingmovies.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angeloparenteapp.upcomingmovies.Adapter.CastAdapter;
import com.angeloparenteapp.upcomingmovies.MyClasses.Cast;
import com.angeloparenteapp.upcomingmovies.MyClasses.Utils;
import com.angeloparenteapp.upcomingmovies.R;
import com.bumptech.glide.Glide;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    int id;
    String imageUrl = "https://image.tmdb.org/t/p/w300";
    String videoUrl;
    String castUrl;
    boolean isMovie;
    String videoKey;
    boolean isFullVideoUrl;
    String releaseDate;
    String backdropPath = "";
    String title;
    String photo;

    RequestQueue queue;
    private static final String TAG = "QueueTag";

    ArrayList<Cast> castArrayList = new ArrayList<>();

    RecyclerView castList;
    CastAdapter castAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout layoutTitle = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setSelected(true);

        ImageView imageView = (ImageView) findViewById(R.id.toolbar_background);

        Intent intent = getIntent();

        title = intent.getStringExtra("title");
        photo = intent.getStringExtra("photo");
        backdropPath = intent.getStringExtra("backdrop");
        final String overview = intent.getStringExtra("overview");
        id = intent.getIntExtra("key", 0);
        isMovie = intent.getBooleanExtra("is_movie", true);
        releaseDate = intent.getStringExtra("release_date");

        if (isMovie) {
            videoUrl = "https://api.themoviedb.org/3/movie/" +
                    id +
                    "/videos?api_key=63eedc968f7dbca3af4fe9b1c47fb761&language=en-US";
        } else {
            videoUrl = "https://api.themoviedb.org/3/tv/" +
                    id +
                    "/videos?api_key=63eedc968f7dbca3af4fe9b1c47fb761&language=en-US";
        }

        castList = (RecyclerView) findViewById(R.id.cast_list);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(DetailActivity.this, 2);
        castList.setLayoutManager(gridLayoutManager);

        castAdapter = new CastAdapter(getApplicationContext(), castArrayList);
        castList.setAdapter(castAdapter);

        fetchCast();
        castAdapter.notifyDataSetChanged();

        layoutTitle.setTitle(title);
        Utils.setLayoutViewCustomFont(getApplicationContext(), layoutTitle);

        if (backdropPath == null || (backdropPath.equals("null")) || (backdropPath.equals(""))) {
            Glide.with(getApplicationContext()).load(imageUrl + photo).into(imageView);
        } else {
            Glide.with(this).load(imageUrl + backdropPath).into(imageView);
        }

        TextView textView = (TextView) findViewById(R.id.overview);
        textView.setText(overview);
        Utils.setTextViewCustomFont(getApplicationContext(), textView);

        TextView trailerTitle = (TextView) findViewById(R.id.trailer_title);
        Utils.setTextViewCustomFont(getApplicationContext(), trailerTitle);

        TextView castTitle = (TextView) findViewById(R.id.cast_title);
        Utils.setTextViewCustomFont(getApplicationContext(), castTitle);

        TextView releaseDateTextView = (TextView) findViewById(R.id.release_date_text);
        releaseDateTextView.setText(releaseDate);
        Utils.setTextViewCustomFont(getApplicationContext(), releaseDateTextView);

        TextView releaseDateStatic = (TextView) findViewById(R.id.release_date);
        Utils.setTextViewCustomFont(getApplicationContext(), releaseDateStatic);

        getVideoKey();

        ImageButton button = (ImageButton) findViewById(R.id.lunch_video);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(videoKey == null)) {
                    if (!isFullVideoUrl) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoKey)));
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoKey)));
                    }
                } else {
                    Toast.makeText(DetailActivity.this, "NO TRAILER YET", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, title + "\n" + "Release Date:     " + releaseDate + "\n" + overview);
                startActivity(Intent.createChooser(intent, "Share via"));
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void getVideoKey() {

        queue = Volley.newRequestQueue(DetailActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, videoUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            getKey(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        jsObjRequest.setTag(TAG);
        queue.add(jsObjRequest);
    }

    public boolean getKey(JSONObject response) {
        try {
            JSONArray results = response.getJSONArray("results");

            JSONObject current = results.getJSONObject(0);

            videoKey = current.getString("key");

            return videoKey.contains("https");

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
            return false;
        }
    }

    public void fetchCast() {

        if (isMovie) {
            castUrl = "http://api.themoviedb.org/3/movie/" +
                    id +
                    "/casts?api_key=63eedc968f7dbca3af4fe9b1c47fb761";
        } else {
            castUrl = "http://api.themoviedb.org/3/tv/" +
                    id +
                    "?api_key=63eedc968f7dbca3af4fe9b1c47fb761&append_to_response=credits";
        }

        queue = Volley.newRequestQueue(DetailActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, castUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            getCast(response);
                            castAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        jsObjRequest.setTag(TAG);
        queue.add(jsObjRequest);
    }

    public void getCast(JSONObject response) {
        try {

            if (isMovie) {
                JSONArray results = response.getJSONArray("cast");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject current = results.getJSONObject(i);

                    String character = current.getString("character");
                    String actorName = current.getString("name");
                    String actorImage = current.getString("profile_path");

                    castArrayList.add(new Cast(character, actorName, actorImage));
                }
            } else {
                JSONObject credits = response.getJSONObject("credits");

                JSONArray cast = credits.getJSONArray("cast");

                for (int i = 0; i < cast.length(); i++) {
                    JSONObject current = cast.getJSONObject(i);

                    String character = current.getString("character");
                    String actorName = current.getString("name");
                    String actorImage = current.getString("profile_path");

                    castArrayList.add(new Cast(character, actorName, actorImage));
                }
            }


        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }

        castAdapter.notifyDataSetChanged();

        if (castArrayList.isEmpty() && castArrayList.size() == 0){
            Snackbar.make(castList, "No Cast Found", Snackbar.LENGTH_SHORT).show();
        }
    }
}
