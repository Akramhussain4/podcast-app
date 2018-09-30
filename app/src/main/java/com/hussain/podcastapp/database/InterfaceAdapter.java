package com.hussain.podcastapp.database;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hussain.podcastapp.model.PodcastImage;

import java.io.IOException;
import java.util.List;

public class InterfaceAdapter extends TypeAdapter<PodcastImage> {
    public void write(JsonWriter writer, PodcastImage image)
            throws IOException {
        if (image == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();

        writer.name("name");
        writer.value(image.label);

        /*writer.name("sequence");
        writeSequence(writer, image.sequence);*/

        writer.endObject();
    }

    private void writeSequence(JsonWriter writer, List<Integer> seq)
            throws IOException {
        writer.beginObject();
        for (int i = 0; i < seq.size(); i++) {
            writer.name("index_" + i);
            writer.value(seq.get(i));
        }
        writer.endObject();
    }

    @Override
    public PodcastImage read(JsonReader in) {
        // This is left blank as an exercise for the reader
        return null;
    }
}