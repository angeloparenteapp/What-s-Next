package com.angeloparenteapp.upcomingmovies.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angeloparenteapp.upcomingmovies.R;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    int id;
    String imageUrl = "https://image.tmdb.org/t/p/w300";
    String videoUrl;
    boolean isMovie;
    String videoKey;
    boolean isFullVideoUrl;
    String releaseDate;

    RequestQueue queue;
    private static final String TAG = "QueueTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout layoutTitle = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setSelected(true);

        ImageView imageView = (ImageView) findViewById(R.id.toolbar_background);

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String photo = intent.getStringExtra("photo");
        String overview = intent.getStringExtra("overview");
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

        setTitle(title);
        layoutTitle.setExpandedTitleColor(getResources().getColor(R.color.white));
        layoutTitle.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        Glide.with(getApplicationContext()).load(imageUrl + photo).into(imageView);

        TextView textView = (TextView) findViewById(R.id.overview);
        textView.setText(overview);

        TextView releaseDateTextView = (TextView) findViewById(R.id.release_date_text);
        releaseDateTextView.setText(releaseDate);

        getVideoKey();

        Button button = (Button) findViewById(R.id.lunch_video);
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

                if (fab.isSelected()) {
                    fab.setSelected(false);
                    fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                } else {
                    fab.setSelected(true);
                    fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
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

    public void getKey(JSONObject response) {
        try {
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject current = results.getJSONObject(i);

                videoKey = current.getString("key");

                if (videoKey.contains("https")) {
                    isFullVideoUrl = true;
                } else {
                    isFullVideoUrl = false;
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
    }
}
