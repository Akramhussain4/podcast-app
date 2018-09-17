package com.hussain.podcastapp.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.ArrayList;

@Root(name = "rss", strict = false)
public class RssFeed{

    @Element(name = "channel")
    private Channel channel;

}

@Root(name = "item", strict = false)
class Item {

    @Path("title")
    @Text(required=false)
    public String title = "";
    private String ItemTitle;
}

@Root(name = "channel", strict = false)
class Channel {

    @ElementList(name = "item", inline = true)
    private ArrayList<Item> items;
}
