package com.hussain.podcastapp.model

import org.simpleframework.xml.*
import java.util.*

@Root(name = "channel", strict = false)
class Channel {
    @ElementList(name = "item", inline = true)
    var items: ArrayList<Item>? = null
    @Element(name = "title")
    var title: String? = null
    @Element(name = "description")
    var description: String? = null
    @Path("link")
    @Text(required = false)
    var shareLink: String? = null
}
