package com.example.abdalnassef.movieapp;
public class Movie {
    private String poster_url;
    private String id;
    private String titel;
    private String date;
    private String vote;
    private String overview;
    static String state;

    public String getPoster_url() {
        return poster_url;
    }

    public String getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getDate() {
        return date;
    }

    public String getVote() {
        return vote;
    }

    public String getOverview() {
        return overview;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

}
