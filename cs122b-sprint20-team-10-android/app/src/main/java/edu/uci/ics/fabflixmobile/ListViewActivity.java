package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ListViewActivity extends Activity {
    private String url;
    private Button getNext;
    private Button getPrev;
    private int pageNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        url = "http://10.0.2.2:8080/cs122b-spring20-team-10/api/";
        getNext = findViewById(R.id.nextButton);
        getPrev = findViewById(R.id.prevButton);

        final ArrayList<Movie> movies = new ArrayList<>();

        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
        parseMovies(movies, adapter);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                Intent moviePage = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                moviePage.putExtra("movie_id", movie.getId());
                moviePage.putExtra("movie_title", movie.getTitle());
                moviePage.putExtra("movie_year", String.valueOf(movie.getYear()));
                moviePage.putExtra("movie_dir", movie.getDirector());

                startActivity(moviePage);
            }
        });

        getNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum++;
                parseMovies(movies, adapter);
                adapter.notifyDataSetChanged();
            }
        });

        getPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pageNum > 0)
                    pageNum--;
                parseMovies(movies, adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void parseMovies(ArrayList<Movie> movies, MovieListViewAdapter adapter)
    {
        RequestQueue queue = NetworkManager.sharedManager(this).queue;

        Intent getQuery = this.getIntent();
        String query = getQuery.getStringExtra("query");

        /*
            To access through LocalHost change the following lines in 'uri':
                .scheme("http")
                .encodedAuthority("http://10.0.2.2:8080/cs122b-spring20-team-10")
         */
        Uri uri = new Uri.Builder()
                .scheme("https")
                .encodedAuthority("ec2-3-15-38-182.us-east-2.compute.amazonaws.com:8443")
                .path("cs122b-spring20-team-10/api/androidlist")
                .appendQueryParameter("pageNum", "" + pageNum)
                .appendQueryParameter("query", query)
                .build();

        StringRequest listRequest = new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Movie[] movArr = gson.fromJson(response, Movie[].class);

                if(response.equals("[]")) {
                    pageNum--;
                    Log.d("Page:", pageNum + "");
                    return;
                }

                movies.clear();
                for(Movie m : movArr)
                    movies.add(m);

                adapter.notifyDataSetChanged();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("list.error", error.toString());
                    }
                }) {
        };

        queue.add(listRequest);
    }
}