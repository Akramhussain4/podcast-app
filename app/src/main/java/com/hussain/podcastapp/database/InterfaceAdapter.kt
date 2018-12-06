package com.hussain.podcastapp.database

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.hussain.podcastapp.model.PodcastImage

import java.io.IOException

class InterfaceAdapter : TypeAdapter<PodcastImage>() {
    @Throws(IOException::class)
    override fun write(writer: JsonWriter, image: PodcastImage?) {
        if (image == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()

        writer.name("name")
        writer.value(image.label)

        /*writer.name("sequence");
        writeSequence(writer, image.sequence);*/

        writer.endObject()
    }

    @Throws(IOException::class)
    private fun writeSequence(writer: JsonWriter, seq: List<Int>) {
        writer.beginObject()
        for (i in seq.indices) {
            writer.name("index_$i")
            writer.value(seq[i])
        }
        writer.endObject()
    }

    override fun read(`in`: JsonReader): PodcastImage? {
        // This is left blank as an exercise for the reader
        return null
    }
}