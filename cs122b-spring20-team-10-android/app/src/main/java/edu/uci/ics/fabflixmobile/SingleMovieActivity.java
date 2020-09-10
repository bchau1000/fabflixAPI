package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

public class SingleMovieActivity extends Activity {
    TextView setTitle;
    TextView setInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovie);

        Intent getMovieInfo = this.getIntent();
        String movieId = getMovieInfo.getStringExtra("movie_id");
        String movieTitle = getMovieInfo.getStringExtra("movie_title");
        String movieYear = getMovieInfo.getStringExtra("movie_year");
        String movieDir = getMovieInfo.getStringExtra("movie_dir");



        RequestQueue queue = NetworkManager.sharedManager(this).queue;


        /*
            To access through LocalHost change the following lines in 'uri':
                .scheme("http")
                .encodedAuthority("http://10.0.2.2:8080/cs122b-spring20-team-10")
         */
        Uri uri = new Uri.Builder()
                .scheme("https")
                .encodedAuthority("ec2-3-15-38-182.us-east-2.compute.amazonaws.com:8443")
                .path("cs122b-spring20-team-10/api/single-movie")
                .appendQueryParameter("id", movieId)
                .appendQueryParameter("src", "mobile")
                .build();

        StringRequest listRequest = new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.substring(0, response.length() - 1);
                String[] movieInfo = response.split(";");

                setTitle = findViewById(R.id.movieTitle);
                setTitle.setText(movieTitle + " (" + movieYear + ")\n");

                setInfo = findViewById(R.id.movieInfo);
                setInfo.setText("Director: " + movieDir + "\n");

                setInfo.append("Genre: " + movieInfo[0] + "\n");
                setInfo.append("Rating: " + movieInfo[1]);

                ListView listView = findViewById(R.id.starlist);
                ArrayList<String> movieStars = new ArrayList<String>();

                for(int i = 2; i < movieInfo.length; i++)
                    movieStars.add(movieInfo[i]);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SingleMovieActivity.this, android.R.layout.simple_list_item_1, movieStars);
                listView.setAdapter(adapter);
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
