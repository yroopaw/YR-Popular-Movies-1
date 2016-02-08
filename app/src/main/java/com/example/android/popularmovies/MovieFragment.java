package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private movieInfoAdapter mMovieAdapter;

    public interface Callback {
        /**
         * Movie FragmentCallback for when an item has been selected.
         */
                public void onItemSelected(Uri dateUri);
    }


    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar
        int id = item.getItemId();

      /*  if (id == R.id.action_refresh) {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute();
            return true;
        }*/

        return super.onOptionsItemSelected(item);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mMovieAdapter = new movieInfoAdapter(getActivity(), new ArrayList<movieInfo>());

        //Get reference to Gridview and set adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        gridView.setAdapter(mMovieAdapter);

        //For getting Detail View
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                movieInfo clickedMovie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movieDetail", clickedMovie);

                startActivity(intent);
            }
        });



        return rootView;
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter);
        //yySharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
       //yy String location = prefs.getString(getString(R.string.pref_location_key),
         //yy       getString(R.string.pref_location_default));
        movieTask.execute();
    }

    @Override
    public  void onStart() {
        super.onStart();
        updateMovies();
    }



    public static movieInfo[] sortByHighestRated(movieInfo[] movies) {
        for (int i = 1; i < movies.length; i++) {
            for (int j = 0; j < movies.length - i; j++) {
                if (((movies[j].user_rating).compareTo((movies[j+1].user_rating))) < 0) {

                    movieInfo temp = movies[j];
                    movies[j] = movies[j + 1];
                    movies[j + 1] = temp;
                }
            }
        }
        return movies;
    }

    public static movieInfo[] sortByPopularity(movieInfo[] movies) {
        for (int i = 1; i < movies.length; i++) {
            for (int j = 0; j < movies.length - i; j++) {
                if ((movies[j].popularity) >((movies[j+1].popularity)))  {

                    movieInfo temp = movies[j];
                    movies[j] = movies[j + 1];
                    movies[j + 1] = temp;
                }
            }
        }
        return movies;
    }


}




