package com.hussain.podcastapp.model;

import java.util.List;

public class ApiResponse {

    private Feed feed;

    public Feed getFeed() {
        return feed;
    }

    public ApiResponse() {
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public class Feed {
        private List<Entry> entry;

        public List getEntry() {
            return entry;
        }

        public void setEntry(List entry) {
            this.entry = entry;
        }

    }
}
