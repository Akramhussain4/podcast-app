package com.hussain.podcastapp.model;

import com.google.gson.annotations.SerializedName;

public class PodcastImage {

    @SerializedName("label")
    public String label;

    public PodcastImage() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
