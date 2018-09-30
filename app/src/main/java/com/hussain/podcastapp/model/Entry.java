package com.hussain.podcastapp.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity(tableName = "entry")
public class Entry implements Parcelable {

    @Exclude
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
    @NonNull
    @PrimaryKey
    @Embedded
    @SerializedName("id")
    public FeedID feedId;
    @Embedded
    @SerializedName("title")
    public Title EntryTitle;
    @SerializedName("im:image")
    public List<PodcastImage> image;
    @Embedded
    @SerializedName("summary")
    public Summary summary;

    public Entry() {
    }

    public FeedID getFeedId() {
        return feedId;
    }

    public void setFeedId(FeedID feedId) {
        this.feedId = feedId;
    }

    @Ignore
    protected Entry(Parcel in) {
        feedId = (FeedID) in.readValue(FeedID.class.getClassLoader());
        EntryTitle = (Title) in.readValue(Title.class.getClassLoader());
        if (in.readByte() == 0x01) {
            image = new ArrayList<>();
            in.readList(image, PodcastImage.class.getClassLoader());
        } else {
            image = null;
        }
        summary = (Summary) in.readValue(Summary.class.getClassLoader());
    }

    @Exclude
    public Title getEntryTitle() {
        return EntryTitle;
    }

    @Exclude
    public void setEntryTitle(Title entryTitle) {
        this.EntryTitle = entryTitle;
    }

    public List<PodcastImage> getImage() {
        return image;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public void setImage(List<PodcastImage> image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("feedId", feedId);
        result.put("Title", EntryTitle);
        result.put("image", image);
        result.put("summary", summary);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(feedId);
        dest.writeValue(EntryTitle);
        if (image == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(image);
        }
        dest.writeValue(summary);
    }

    public static class Title implements Parcelable {

        @Exclude
        @Ignore
        @SuppressWarnings("unused")
        public final Parcelable.Creator<Title> CREATOR = new Parcelable.Creator<Title>() {
            @Override
            public Title createFromParcel(Parcel in) {
                return new Title(in);
            }

            @Override
            public Title[] newArray(int size) {
                return new Title[size];
            }
        };

        public Title() {
        }

        @Ignore
        Title(Parcel in) {
            labelTitle = in.readString();
        }

        @SerializedName("label")
        public String labelTitle;

        public String getLabelTitle() {
            return labelTitle;
        }

        public void setLabelTitle(String labelTitle) {
            this.labelTitle = labelTitle;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(labelTitle);
        }
    }

    public static class Summary implements Parcelable {

        @Exclude
        @Ignore
        @SuppressWarnings("unused")
        public final Parcelable.Creator<Summary> CREATOR = new Parcelable.Creator<Summary>() {
            @Override
            public Summary createFromParcel(Parcel in) {
                return new Summary(in);
            }

            @Override
            public Summary[] newArray(int size) {
                return new Summary[size];
            }
        };
        public String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Summary() {
        }

        @Ignore
        protected Summary(Parcel in) {
            label = in.readString();
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

    public static class FeedID implements Parcelable {

        @Exclude
        @Ignore
        @SuppressWarnings("unused")
        public final Parcelable.Creator<FeedID> CREATOR = new Parcelable.Creator<FeedID>() {
            @Override
            public FeedID createFromParcel(Parcel in) {
                return new FeedID(in);
            }

            @Override
            public FeedID[] newArray(int size) {
                return new FeedID[size];
            }
        };
        @Embedded
        @NonNull
        public Attributes attributes;

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public FeedID() {
        }

        @Ignore
        FeedID(Parcel in) {
            attributes = (Attributes) in.readValue(Attributes.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(attributes);
        }
    }

    public static class Attributes implements Parcelable {

        @Exclude
        @Ignore
        @SuppressWarnings("unused")
        public final Parcelable.Creator<Attributes> CREATOR = new Parcelable.Creator<Attributes>() {
            @Override
            public Attributes createFromParcel(Parcel in) {
                return new Attributes(in);
            }

            @Override
            public Attributes[] newArray(int size) {
                return new Attributes[size];
            }
        };
        @NonNull
        @SerializedName("im:id")
        public String id;

        public Attributes() {
        }

        @Ignore
        protected Attributes(Parcel in) {
            id = in.readString();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
        }
    }
}