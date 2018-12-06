package com.hussain.podcastapp.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "item", strict = false)
class Item : Parcelable {

    @Path("title")
    @Text(required = false)
    var title = ""

    @Path("itunes:summary")
    @Text(required = false)
    var summary = ""

    @Path("itunes:image")
    @Attribute(name = "href")
    var image: String? = ""

    @Path("itunes:duration")
    @Text(required = false)
    var duration: String? = null

    @Path("enclosure")
    @Attribute(name = "url")
    var url: String? = null

    var bitmap: Bitmap? = null

    private constructor(parcel: Parcel) {
        title = parcel.readString()
        summary = parcel.readString()
        image = parcel.readString()
        duration = parcel.readString()
        url = parcel.readString()
        bitmap = parcel.readValue(Bitmap::class.java.classLoader) as Bitmap
    }

    constructor()

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(summary)
        dest.writeString(image)
        dest.writeString(duration)
        dest.writeString(url)
        dest.writeValue(bitmap)
    }


    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }

}

