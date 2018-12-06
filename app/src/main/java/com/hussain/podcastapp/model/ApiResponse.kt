package com.hussain.podcastapp.model

class ApiResponse {

    var feed: Feed? = null

    inner class Feed {
        private var entry: List<Entry>? = null

        fun getEntry(): List<Entry>? {
            return entry
        }

        fun setEntry(entry: List<Entry>) {
            this.entry = entry
        }

    }
}
