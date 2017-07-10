package com.angeloparenteapp.upcomingmovies.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angeloparenteapp.upcomingmovies.Adapter.RecyclerViewAdapter;
import com.angeloparenteapp.upcomingmovies.MyClasses.MainPoster;
import com.angeloparenteapp.upcomingmovies.R;

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

    private String date;
    private String movieUrl;
    private String showsUrl;

    RequestQueue queue;
    private static final String TAG = "QueueTag";

    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView navigation;

    int page;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_movies:
                    page = 0;
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchMovies();
                    return true;
                case R.id.navigation_shows:
                    page = 0;
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchShows();
                    return true;
                case R.id.navigation_favorite:
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    Snackbar.make(navigation, "You selected Favorites", Snackbar.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), listOfElements);
        recyclerView.setAdapter(recyclerViewAdapter);

        fetchMovies();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;

                if (navigation.getSelectedItemId() == R.id.navigation_movies) {
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchMovies();
                } else if (navigation.getSelectedItemId() == R.id.navigation_shows) {
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchShows();
                } else {
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int totalItemCount = recyclerView.getLayoutManager().getItemCount();

                if (totalItemCount == gridLayoutManager.findLastVisibleItemPosition() + 1) {
                    if (navigation.getSelectedItemId() == R.id.navigation_movies) {
                        fetchMovies();
                    } else if (navigation.getSelectedItemId() == R.id.navigation_shows) {
                        fetchShows();
                    } else {
                        listOfElements.clear();
                        recyclerViewAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    public void fetchMovies() {

        page++;

        movieUrl = "https://api.themoviedb.org/3/discover/movie" +
                "?api_key=63eedc968f7dbca3af4fe9b1c47fb761&" +
                "language=en-US&" +
                "sort_by=release_date.asc&" +
                "include_adult=false&" +
                "include_video=false&" +
                "page=" + page + "&" +
                "primary_release_date.gte=" + date;

        swipeRefreshLayout.setRefreshing(true);

        queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, movieUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (response != null) {
                            setMovies(response);
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        jsObjRequest.setTag(TAG);
        queue.add(jsObjRequest);
    }

    public void fetchShows() {

        page++;

        showsUrl = "https://api.themoviedb.org/3/discover/tv" +
                "?api_key=63eedc968f7dbca3af4fe9b1c47fb761&" +
                "sort_by=first_air_date.asc" +
                "&first_air_date.gte=" + date +
                "&page=" + page +
                "&include_null_first_air_dates=false";

        swipeRefreshLayout.setRefreshing(true);

        queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, showsUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (response != null) {
                            setShows(response);
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        jsObjRequest.setTag(TAG);
        queue.add(jsObjRequest);
    }

    public void setMovies(JSONObject response) {

        try {
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject current = results.getJSONObject(i);

                String title = current.getString("title");
                String posterPath = current.getString("poster_path");

                if (!posterPath.equals("null")) {
                    listOfElements.add(new MainPoster(title, posterPath));
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
    }

    public void setShows(JSONObject response) {

        try {
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject current = results.getJSONObject(i);

                String title = current.getString("name");
                String posterPath = current.getString("poster_path");

                if (!posterPath.equals("null")) {
                    listOfElements.add(new MainPoster(title, posterPath));
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
    }
}