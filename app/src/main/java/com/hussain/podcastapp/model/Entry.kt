package com.hussain.podcastapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName
import java.util.*


@Entity(tableName = "entry")
class Entry : Parcelable {
    @NonNull
    @PrimaryKey
    @Embedded
    @SerializedName("id")
    var feedId: FeedID? = null
    @Embedded
    @SerializedName("title")
    @get:Exclude
    @set:Exclude
    var entryTitle: Title? = null
    @SerializedName("im:image")
    var image: List<PodcastImage>? = null
    @Embedded
    @SerializedName("summary")
    var summary: Summary? = null

    constructor()

    @Ignore private constructor(parcel: Parcel) {
        feedId = parcel.readValue(FeedID::class.java.classLoader) as FeedID
        entryTitle = parcel.readValue(Title::class.java.classLoader) as Title
        if (parcel.readByte().toInt() == 0x01) {
            image = ArrayList()
            parcel.readList(image, PodcastImage::class.java.classLoader)
        } else {
            image = null
        }
        summary = parcel.readValue(Summary::class.java.classLoader) as Summary
    }

    override fun describeContents(): Int {
        return 0
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["feedId"] = feedId
        result["EntryTitle"] = entryTitle
        result["image"] = image
        result["summary"] = summary
        return result
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(feedId)
        dest.writeValue(entryTitle)
        if (image == null) {
            dest.writeByte(0x00.toByte())
        } else {
            dest.writeByte(0x01.toByte())
            dest.writeList(image)
        }
        dest.writeValue(summary)
    }

    class Title : Parcelable {

        companion object CREATOR : Parcelable.Creator<Title> {
            override fun createFromParcel(parcel: Parcel): Title {
                return Title(parcel)
            }

            override fun newArray(size: Int): Array<Title?> {
                return arrayOfNulls(size)
            }
        }

        @SerializedName("label")
        var labelTitle: String? = ""

        constructor()

        @Ignore
        internal constructor(parcel: Parcel) {
            labelTitle = parcel.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(labelTitle)
        }
    }

    class Summary : Parcelable {

        companion object CREATOR : Parcelable.Creator<Summary> {
            override fun createFromParcel(parcel: Parcel): Summary {
                return Summary(parcel)
            }

            override fun newArray(size: Int): Array<Summary?> {
                return arrayOfNulls(size)
            }
        }

        var label: String? = ""

        constructor()

        @Ignore private constructor(parcel: Parcel) {
            label = parcel.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(label)
        }
    }

    class FeedID : Parcelable {

        companion object CREATOR : Parcelable.Creator<FeedID> {
            override fun createFromParcel(parcel: Parcel): FeedID {
                return FeedID(parcel)
            }

            override fun newArray(size: Int): Array<FeedID?> {
                return arrayOfNulls(size)
            }
        }

        @Embedded
        @NonNull
        var attributes: Attributes? = null

        constructor()

        @Ignore
        internal constructor(parcel: Parcel) {
            attributes = parcel.readValue(Attributes::class.java.classLoader) as Attributes
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeValue(attributes)
        }
    }

    class Attributes : Parcelable {

        companion object CREATOR : Parcelable.Creator<Attributes> {
            override fun createFromParcel(parcel: Parcel): Attributes {
                return Attributes(parcel)
            }

            override fun newArray(size: Int): Array<Attributes?> {
                return arrayOfNulls(size)
            }
        }

        @NonNull
        @SerializedName("im:id")
        var id: String? = ""

        constructor()

        @Ignore private constructor(parcel: Parcel) {
            id = parcel.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(id)
        }
    }

    companion object CREATOR : Parcelable.Creator<Entry> {
        override fun createFromParcel(parcel: Parcel): Entry {
            return Entry(parcel)
        }

        override fun newArray(size: Int): Array<Entry?> {
            return arrayOfNulls(size)
        }
    }

}