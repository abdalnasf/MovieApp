package com.example.abdalnassef.movieapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GridView gridView,trailergGridView;
    private ArrayList<String> movieArrayList = null;
    TrailerAdapter trailerAdapter;
    MovieAdapter movieAdapter;
    FavoriteAdapter favoriteAdapter;
    public Movie[] movie = null;
    boolean largeScreen = false;
    Context context;
    static TextView date_text, overview_text, vote_text, titel;
    static ImageView poster;
    String id,title,overview;
    static View fragDetail;
    static String currentState = "popular?";
public static Movie imovie=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState != null) {
            currentState = savedInstanceState.getString("state");
        } else {
            Movie.state = "popular?";
            currentState = Movie.state;
        }
        context = getBaseContext();
        fragDetail = findViewById(R.id.fragment2);
        poster = (ImageView) findViewById(R.id.movie_image);
        titel = ((TextView) findViewById(R.id.movie_name_text));
        date_text = (TextView) findViewById(R.id.movie_date_text);
        vote_text = ((TextView) findViewById(R.id.movie_averge_text));
        overview_text = ((TextView) findViewById(R.id.movie_overview_text));
        gridView = (GridView) findViewById(R.id.grid_view);

        if (isNetworkAvailable()) {
            executeTask();
        } else {
            Toast.makeText(getBaseContext(), "No Network Available", Toast.LENGTH_LONG).show();
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (size.x > size.y || screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            View viewGroup = (View) findViewById(R.id.fragment2);
           try{ viewGroup.setVisibility(View.INVISIBLE);}catch (NullPointerException e){}
            largeScreen = true;
            DetailActivityFragment.land = true;
        } else {
            largeScreen = false;
            DetailActivityFragment.land = false;
        }

        fragmentTransaction.commit();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                imovie=movie[i];
                if (!largeScreen) {
                    Intent Deatail = new Intent(context, DetailActivity.class);
                    Deatail.putExtra("poster_url", movie[i].getPoster_url());
                    Deatail.putExtra("title", movie[i].getTitel());
                    Deatail.putExtra("date", movie[i].getDate());
                    Deatail.putExtra("vote", movie[i].getVote());
                    Deatail.putExtra("ov", movie[i].getOverview());
                    Deatail.putExtra("id", movie[i].getId());
                    startActivity(Deatail);
                } else {
                    title = movie[i].getTitel();
                    String date = movie[i].getDate();
                    String vote = movie[i].getVote();
                    overview = movie[i].getOverview();
                    id = movie[i].getId();
                    fragDetail.setVisibility(View.VISIBLE);
                    String baseUrl = "http://image.tmdb.org/t/p/w185";
                    String poster_url = baseUrl + movie[i].getPoster_url();
                    DetailActivityFragment.poster_url = poster_url;
                    DetailActivityFragment.title = title;
                    DetailActivityFragment.date = date;
                    DetailActivityFragment.overview_string = overview;
                    DetailActivityFragment.id = id;
                    Picasso.with(context).load(poster_url).into(poster);
                    titel.setText(title);
                    date_text.setText(date);
                    vote_text.setText(vote);

                    trailergGridView = (GridView) findViewById(R.id.trailer_view);
                    MovieTask2 task = new MovieTask2();
                    task.execute(id);
                    MovieTask3 task2 = new MovieTask3();
                    task2.execute(id);
                    trailergGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movieArrayList.get(i))));
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("state", Movie.state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.favorite_menu) {
            Toast.makeText(context, "Favorite", Toast.LENGTH_LONG).show();
            favoriteAdapter = new FavoriteAdapter(context);
            movie = favoriteAdapter.AllFav();
            movieAdapter = new MovieAdapter(context, movie);
            gridView.setAdapter(movieAdapter);
            return true;
        }
        if (id == R.id.top_rated_menu) {
            Toast.makeText(context, "top rated ", Toast.LENGTH_LONG).show();
            Movie.state = "top_rated?";
            currentState = "top_rated?";
            executeTask();
            return true;
        }
        if (id == R.id.popular_menu) {
            Toast.makeText(context, "Popular ", Toast.LENGTH_LONG).show();
            Movie.state = "popular?";
            currentState = "popular?";
            executeTask();
            return true;
        }
        if (id == R.id.share) {
            shareNews();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareNews() {
        if (title == null) {
            Toast.makeText(getBaseContext(), "Sorry you should Choose Film", Toast.LENGTH_LONG).show();
        } else {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + overview);
            startActivity(Intent.createChooser(shareIntent, "Share using"));
        }
    }

    public void executeTask() {
        MovieTask task = new MovieTask();
        task.execute(Movie.state);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class MovieTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(String... params) {
            String FORECAST_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "1962bc00de1584940b4f338dc55d6887")
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("error", "Error closing stream", e);
                    }
                }
            }
            try {
                return getDataFromJson(JsonStr);
            } catch (JSONException e) {
                Log.e("error", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movie) {
            getData(movie);
        }

        private Movie[] getDataFromJson(String jsonStr) throws JSONException {
            final String M_LIST = "results";
            final String poster_path = "poster_path";
            final String release_date = "release_date";
            final String vote_average = "vote_average";
            final String overview = "overview";
            final String original_title = "original_title";
            final String id = "id";
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray(M_LIST);
            movie = new Movie[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                movie[i] = new Movie();
                JSONObject movie = movieArray.getJSONObject(i);
                String poster = movie.getString(poster_path);
                String release = movie.getString(release_date);
                String vote = movie.getString(vote_average);
                String overvie = movie.getString(overview);
                String title = movie.getString(original_title);
                String _id = movie.getString(id);
                MainActivity.this.movie[i].setPoster_url(poster);
                MainActivity.this.movie[i].setDate(release);
                MainActivity.this.movie[i].setVote(vote);
                MainActivity.this.movie[i].setTitel(title);
                MainActivity.this.movie[i].setOverview(overvie);
                MainActivity.this.movie[i].setId(_id);
            }
            return movie;
        }

        public void getData(Movie[] moviesData) {
            if (moviesData != null) {
                movie = moviesData;
                movieAdapter = new MovieAdapter(getBaseContext(), moviesData);
                gridView.setAdapter(movieAdapter);

            } else
                Log.d("MMMM", "No Data");
        }
    }

    public class MovieTask2 extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            String FORECAST_BASE_URL =
                    "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String JsonStr = null;


            try {

                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "1962bc00de1584940b4f338dc55d6887")
                        .build();

                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("error", "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(JsonStr);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] movie) {
            if (movie != null) {
                movieArrayList = new ArrayList<>();
                for (int i = 0; i < movie.length; i++) {
                    movieArrayList.add("https://www.youtube.com/watch?v=" + movie[i]);
                }
                trailergGridView = (GridView) findViewById(R.id.trailer_view);
                trailerAdapter = new TrailerAdapter(context, movieArrayList);
                trailergGridView.setAdapter(trailerAdapter);
            }
        }

        private String[] getDataFromJson(String jsonStr) throws JSONException {
            final String M_LIST = "results";
            final String key = "key";
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray(M_LIST);

            String[] results = new String[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String poster_key = movie.getString(key);
                results[i] = poster_key;
            }
            return results;
        }

    }

    public class MovieTask3 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.d("message", "message");
            String FORECAST_BASE_URL =
                    "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String JsonStr = null;


            try {

                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "1962bc00de1584940b4f338dc55d6887")
                        .build();

                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Error", "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(JsonStr);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage(), e);
                e.printStackTrace();
            }
             return null;
        }

        @Override
        protected void onPostExecute(String movie) {
            if (movie != null) {
                overview_text.setText(movie);
            }
        }

        private String getDataFromJson(String jsonStr) throws JSONException {
            final String M_LIST = "results";
            final String author = "author";
            final String content = "content";
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray(M_LIST);

            String review = null;

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String auther = movie.getString(author);
                String contnt = movie.getString(content);
                review = auther + "\n" + contnt;
            }
            return review;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(imovie!=null){
            title = imovie.getTitel();
            String date = imovie.getDate();
            String vote = imovie.getVote();
            overview = imovie.getOverview();
            id = imovie.getId();
            fragDetail.setVisibility(View.VISIBLE);
            String baseUrl = "http://image.tmdb.org/t/p/w185";
            String poster_url = baseUrl + imovie.getPoster_url();
            DetailActivityFragment.poster_url = poster_url;
            DetailActivityFragment.title = title;
            DetailActivityFragment.date = date;
            DetailActivityFragment.overview_string = overview;
            DetailActivityFragment.id = id;
            Picasso.with(context).load(poster_url).into(poster);
            titel.setText(title);
            date_text.setText(date);
            vote_text.setText(vote);

            trailergGridView = (GridView) findViewById(R.id.trailer_view);
            MovieTask2 task = new MovieTask2();
            task.execute(id);
            MovieTask3 task2 = new MovieTask3();
            task2.execute(id);
            trailergGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movieArrayList.get(i))));
                }
            });

        }
    }
}
