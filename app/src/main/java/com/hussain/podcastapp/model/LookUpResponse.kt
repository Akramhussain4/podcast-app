package com.hussain.podcastapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class LookUpResponse {

    @SerializedName("results")
    var results: List<Results>? = null

    class Results constructor(parcel: Parcel) : Parcelable {

        var feedUrl: String? = null
        @SerializedName("artworkUrl600")
        var artWork: String? = null

        init {
            feedUrl = parcel.readString()
            artWork = parcel.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(feedUrl)
            dest.writeString(artWork)
        }

        companion object CREATOR : Parcelable.Creator<Results> {
            override fun createFromParcel(parcel: Parcel): Results {
                return Results(parcel)
            }

            override fun newArray(size: Int): Array<Results?> {
                return arrayOfNulls(size)
            }
        }
    }
}
