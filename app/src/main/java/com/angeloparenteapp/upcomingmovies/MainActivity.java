package com.angeloparenteapp.upcomingmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angeloparenteapp.upcomingmovies.Adapter.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ArrayList<MainPoster> listOfElements = new ArrayList<>();
    private String movieUrl = "https://api.themoviedb.org/3/discover/movie" +
            "?api_key=63eedc968f7dbca3af4fe9b1c47fb761&" +
            "language=en-US&" +
            "sort_by=release_date.asc&" +
            "include_adult=false&" +
            "include_video=false&" +
            "page=1&" +
            "primary_release_date.gte=2017-07-08";

    RequestQueue queue;
    private static final String TAG = "QueueTag";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_movies:
                    fetchMovies();
                    return true;
                case R.id.navigation_shows:

                    return true;
                case R.id.navigation_favorite:

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), listOfElements, recyclerViewAdapter);
        recyclerView.setAdapter(recyclerViewAdapter);

        fetchMovies();
    }

    public void fetchMovies() {

        listOfElements.clear();

        queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, movieUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            setMovies(response);
                            recyclerViewAdapter.notifyDataSetChanged();
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

    public void setMovies(JSONObject response){

        try {
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject current = results.getJSONObject(i);

                String title = current.getString("title");
                String posterPath = current.getString("poster_path");

                listOfElements.add(new MainPoster(title, posterPath));
            }

            for (int j = 0; j < listOfElements.size(); j++){
                if (listOfElements.get(j).getPosterImage().equals("null")){
                    listOfElements.remove(j);
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
    }
}