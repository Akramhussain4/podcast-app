package com.hussain.podcastapp.model;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class PodcastImage implements Parcelable {

    @Exclude
    @Ignore
    @SuppressWarnings("unused")
    public final Parcelable.Creator<PodcastImage> CREATOR = new Parcelable.Creator<PodcastImage>() {
        @Override
        public PodcastImage createFromParcel(Parcel in) {
            return new PodcastImage(in);
        }

        @Override
        public PodcastImage[] newArray(int size) {
            return new PodcastImage[size];
        }
    };
    public String label;

    public PodcastImage() {
    }

    @Ignore
    public PodcastImage(Parcel in) {
        label = in.readString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
    }
}
