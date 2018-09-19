package com.hussain.podcastapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Entry implements Parcelable {

    @SerializedName("id")
    private FeedID feedId;
    @SerializedName("title")
    private Title EntryTitle;
    @SerializedName("im:image")
    private List<Image> image;
    @SerializedName("summary")
    private Summary summary;

    public FeedID getFeedId() {
        return feedId;
    }

    public void setFeedId(FeedID feedId) {
        this.feedId = feedId;
    }

    public Title getEntryTitle() {
        return EntryTitle;
    }

    public void setEntryTitle(Title entryTitle) {
        this.EntryTitle = entryTitle;
    }

    public List<Image> getImage() {
        return image;
    }

    public void setImage(List<Image> image) {
        this.image = image;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

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

    protected Entry(Parcel in) {
        feedId = (FeedID) in.readValue(FeedID.class.getClassLoader());
        EntryTitle = (Title) in.readValue(Title.class.getClassLoader());
        if (in.readByte() == 0x01) {
            image = new ArrayList<Image>();
            in.readList(image, Image.class.getClassLoader());
        } else {
            image = null;
        }
        summary = (Summary) in.readValue(Summary.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
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

    public class Title implements Parcelable {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

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

        protected Title(Parcel in) {
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

    public class Image implements Parcelable {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @SuppressWarnings("unused")
        public final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
            @Override
            public Image createFromParcel(Parcel in) {
                return new Image(in);
            }

            @Override
            public Image[] newArray(int size) {
                return new Image[size];
            }
        };

        protected Image(Parcel in) {
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

    public class Summary implements Parcelable {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

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

    public class FeedID implements Parcelable {

        private Attributes attributes;

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }


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

        protected FeedID(Parcel in) {
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

    public class Attributes implements Parcelable {

        @SerializedName("im:id")
        private String id;

        public String getIm() {
            return id;
        }

        public void setIm(String im) {
            this.id = im;
        }

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

        protected Attributes(Parcel in) {
            id = in.readString();
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