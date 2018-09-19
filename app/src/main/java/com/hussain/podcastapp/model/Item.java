package com.hussain.podcastapp.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "item", strict = false)
public class Item {

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
}

