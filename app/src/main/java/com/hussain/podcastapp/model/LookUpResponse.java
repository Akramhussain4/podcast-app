package com.hussain.podcastapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LookUpResponse {

    @SerializedName("results")
    private List<Results> results;


    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public class Results implements Parcelable {

        @SuppressWarnings("unused")
        public final Parcelable.Creator<Results> CREATOR = new Parcelable.Creator<Results>() {
            @Override
            public Results createFromParcel(Parcel in) {
                return new Results(in);
            }

            @Override
            public Results[] newArray(int size) {
                return new Results[size];
            }
        };
        private String feedUrl;
        @SerializedName("artworkUrl600")
        private String artWork;

        protected Results(Parcel in) {
            feedUrl = in.readString();
            artWork = in.readString();
        }

        public String getFeedUrl() {
            return feedUrl;
        }

        public void setFeedUrl(String feedUrl) {
            this.feedUrl = feedUrl;
        }

        public String getArtWork() {
            return artWork;
        }

        public void setArtWork(String artWork) {
            this.artWork = artWork;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(feedUrl);
            dest.writeString(artWork);
        }
    }
}
