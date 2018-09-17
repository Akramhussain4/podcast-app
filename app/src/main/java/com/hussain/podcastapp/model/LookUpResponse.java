package com.hussain.podcastapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LookUpResponse {

    @SerializedName("results")
    private List<Results> results;
    @SerializedName("artworkUrl600")
    private String artWork;

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String getArtWork() {
        return artWork;
    }

    public void setArtWork(String artWork) {
        this.artWork = artWork;
    }

    public class Results{

        private String feedUrl;

        public String getFeedUrl() {
            return feedUrl;
        }

        public void setFeedUrl(String feedUrl) {
            this.feedUrl = feedUrl;
        }
    }
}
