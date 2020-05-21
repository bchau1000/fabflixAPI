package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

public class SearchActivity extends Activity {
    ImageButton searchButton;
    EditText searchMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchButton = findViewById(R.id.searchButton);
        searchMovie = findViewById(R.id.searchMovie);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listView = new Intent(SearchActivity.this, ListViewActivity.class);
                listView.putExtra("query", searchMovie.getText().toString());
                startActivity(listView);
            }
        });

        searchMovie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    Intent listView = new Intent(SearchActivity.this, ListViewActivity.class);
                    listView.putExtra("query", searchMovie.getText().toString());
                    startActivity(listView);

                    handled = true;
                }
                return handled;
            }
        });
    }
}
