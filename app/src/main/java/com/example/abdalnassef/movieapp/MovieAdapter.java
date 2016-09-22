package com.example.abdalnassef.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends BaseAdapter {
    Context context;
    Movie[] movie;

    public MovieAdapter(Context context, Movie[] movie) {
        this.context = context;
        this.movie = movie;
    }

    @Override
    public int getCount() {
        return movie.length;
    }

    @Override
    public Object getItem(int i) {
        return movie[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_movie, viewGroup, false);
        }
        ImageView imgView = (ImageView) view.findViewById(R.id.list_item_movie_imageView);
        String baseUrl = "http://image.tmdb.org/t/p/w185";
        String poster_url = baseUrl + movie[i].getPoster_url();
        Picasso.with(context).load(poster_url).into(imgView);
        return view;
    }
}
