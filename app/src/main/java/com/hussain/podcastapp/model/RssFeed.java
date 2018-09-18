package com.hussain.podcastapp.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class RssFeed{

    @Element(name = "channel")
    private Channel channel;

}