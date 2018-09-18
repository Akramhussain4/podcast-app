package com.hussain.podcastapp.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "channel", strict = false)
public class Channel {
    @ElementList(name = "item", inline = true)
    private ArrayList<Item> items;
}
