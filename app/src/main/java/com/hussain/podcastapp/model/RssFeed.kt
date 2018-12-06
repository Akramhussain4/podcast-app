package com.hussain.podcastapp.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
class RssFeed {

    @Element(name = "channel")
    var channel: Channel? = null
}