package com.hussain.podcastapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entry {

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

    public class Title {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public class Image {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public class Summary {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public class FeedID {

        private Attributes attributes;

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

    }

    public class Attributes {

        @SerializedName("im:id")
        private String id;

        public String getIm() {
            return id;
        }

        public void setIm(String im) {
            this.id = im;
        }
    }

}
