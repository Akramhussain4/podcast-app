package com.hussain.podcastapp.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "item", strict = false)
public class Item implements Parcelable {

    @Path("title")
    @Text(required = false)
    public String title = "";

    @Path("itunes:summary")
    @Text(required = false)
    public String summary = "";

    @Path("itunes:image")
    @Attribute(name = "href")
    public String image;

    @Path("itunes:duration")
    @Text(required = false)
    public String duration;

    @Path("enclosure")
    @Attribute(name = "url")
    public String url;

    public Bitmap bitmap;

    protected Item(Parcel in) {
        title = in.readString();
        summary = in.readString();
        image = in.readString();
        duration = in.readString();
        url = in.readString();
        bitmap = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Item() {
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(summary);
        dest.writeString(image);
        dest.writeString(duration);
        dest.writeString(url);
        dest.writeValue(bitmap);
    }
}

