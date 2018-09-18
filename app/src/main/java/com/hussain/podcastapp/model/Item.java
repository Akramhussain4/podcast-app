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
}

