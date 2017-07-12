package com.angeloparenteapp.upcomingmovies.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;
    ArrayList<MainPoster> listOfElements = new ArrayList<>();

    String date;
    String movieUrl;
    String showsUrl;
    String searchUrl;
    String searchItem;

    RequestQueue queue;
    private static final String TAG = "QueueTag";

    SwipeRefreshLayout swipeRefreshLayout;
    BottomNavigationView navigation;
    EditText searchView;
    LinearLayout searchLayout;
    ImageButton searchButton;

    int page;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_movies:
                    searchLayout.setVisibility(View.GONE);
                    page = 0;
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchMovies();
                    recyclerViewAdapter.notifyDataSetChanged();
                    return true;
                case R.id.navigation_shows:
                    searchLayout.setVisibility(View.GONE);
                    page = 0;
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchShows();
                    recyclerViewAdapter.notifyDataSetChanged();
                    return true;
                case R.id.navigation_search:
                    searchLayout.setVisibility(View.VISIBLE);
                    page = 0;
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
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

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA).format(Calendar.getInstance().getTime());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        searchLayout = (LinearLayout) findViewById(R.id.search_layout);
        searchLayout.setVisibility(View.GONE);

        searchView = (EditText) findViewById(R.id.search);
        searchButton = (ImageButton) findViewById(R.id.search_button);

        recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), listOfElements);
        recyclerView.setAdapter(recyclerViewAdapter);

        fetchMovies();
        recyclerViewAdapter.notifyDataSetChanged();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;

                if (navigation.getSelectedItemId() == R.id.navigation_movies) {
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchMovies();
                    recyclerViewAdapter.notifyDataSetChanged();
                } else if (navigation.getSelectedItemId() == R.id.navigation_shows) {
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    fetchShows();
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    listOfElements.clear();
                    recyclerViewAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    fetchSearch();
                    recyclerViewAdapter.notifyDataSetChanged();
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
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else if (navigation.getSelectedItemId() == R.id.navigation_shows) {
                        fetchShows();
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        fetchSearch();
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOfElements.clear();
                recyclerViewAdapter.notifyDataSetChanged();

                searchItem = searchView.getText().toString();

                searchItem = searchItem.replace(" ", "%20");

                Log.d("VALOREEEEEE", searchItem);

                searchView.clearFocus();
                searchView.setText("");

                if (getCurrentFocus() != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                fetchSearch();
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void fetchMovies() {

        page++;

        movieUrl = "https://api.themoviedb.org/3/discover/movie?" +
                "api_key=63eedc968f7dbca3af4fe9b1c47fb761" +
                "&language=en-US" +
                "&sort_by=primary_release_date.asc" +
                "&include_adult=false" +
                "&include_video=false" +
                "&page=" + page +
                "&primary_release_date.gte=" + date +
                "&include_null_first_air_dates=true";

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

    public void setMovies(JSONObject response) {

        try {
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject current = results.getJSONObject(i);

                String title = current.getString("title");
                String posterPath = current.getString("poster_path");
                String overview = current.getString("overview");
                int id = current.getInt("id");
                String releaseDate = current.getString("release_date");

                if (!posterPath.equals("null")) {
                    listOfElements.add(new MainPoster(title, posterPath, overview, id, true, releaseDate));
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
    }

    public void fetchShows() {

        page++;

        showsUrl = "https://api.themoviedb.org/3/discover/tv" +
                "?api_key=63eedc968f7dbca3af4fe9b1c47fb761" +
                "&language=en-US" +
                "&sort_by=first_air_date.asc" +
                "&page=" + page +
                "&first_air_date.gte=" + date +
                "&include_null_first_air_dates=true";

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

    public void setShows(JSONObject response) {

        try {
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject current = results.getJSONObject(i);

                String title = current.getString("name");
                String posterPath = current.getString("poster_path");
                String overview = current.getString("overview");
                int id = current.getInt("id");
                String release_date = current.getString("first_air_date");

                if (!posterPath.equals("null")) {
                    listOfElements.add(new MainPoster(title, posterPath, overview, id, false, release_date));
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
    }

    public void fetchSearch() {

        page++;

        searchUrl = "https://api.themoviedb.org/3/search/multi?" +
                "api_key=63eedc968f7dbca3af4fe9b1c47fb761&language=en-US" +
                "&query=" + searchItem +
                "&page=" + page +
                "&include_adult=false";

        swipeRefreshLayout.setRefreshing(true);

        queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, searchUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (response != null) {
                            setSearch(response);
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

    public void setSearch(JSONObject response) {

        try {
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject current = results.getJSONObject(i);

                String mediaType = current.getString("media_type");

                if (mediaType.equals("movie")) {
                    String title = current.getString("title");
                    String posterPath = current.getString("poster_path");
                    String overview = current.getString("overview");
                    int id = current.getInt("id");
                    String release_date = current.getString("release_date");

                    if (!posterPath.equals("null")) {
                        listOfElements.add(new MainPoster(title, posterPath, overview, id, true, release_date));
                    }
                } else if (mediaType.equals("tv")) {
                    String title = current.getString("name");
                    String posterPath = current.getString("poster_path");
                    String overview = current.getString("overview");
                    int id = current.getInt("id");
                    String release_date = current.getString("first_air_date");

                    if (!posterPath.equals("null")) {
                        listOfElements.add(new MainPoster(title, posterPath, overview, id, false, release_date));
                    }
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
    }
}