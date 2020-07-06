package com.example.projekt_3

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

//https://www.vogella.com/tutorials/AndroidXML/article.html

class RSSParser {

    companion object {
        private const val PUB_DATE = "pubDate"
        private const val DESCRIPTION = "description"
        private const val CHANNEL = "channel"
        private const val LINK = "link"
        private const val TITLE = "title"
        private const val ITEM = "item"
        private const val ENCLOSURE = "enclosure"
        private const val GUID = "guid"
    }

    fun parse(rssFeed: String?): List<RSSItem?> {
        val list: MutableList<RSSItem?> = ArrayList<RSSItem?>()
        val parser = Xml.newPullParser()
        var stream: InputStream? = null
        try {
            // auto-detect the encoding from the stream
            stream = URL(rssFeed).openConnection().getInputStream()
            parser.setInput(stream, null)
            var eventType = parser.eventType
            var done = false
            var item: RSSItem? = null
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                var name: String? = null
                when (eventType) {
                    XmlPullParser.START_DOCUMENT -> {
                    }
                    XmlPullParser.START_TAG -> {
                        name = parser.name
                        if (name.equals(ITEM, ignoreCase = true)) {
                            Log.i("new item", "Create new item")
                            item = RSSItem()
                        } else if (item != null) {
                            if (name.equals(LINK, ignoreCase = true)) {
                                Log.i("Attribute", "setLink")
                                item.link = parser.nextText()
                            } else if (name.equals(
                                    DESCRIPTION,
                                    ignoreCase = true
                                )
                            ) {
                                Log.i("Attribute", "description")
                                item.description = parser.nextText().trim { it <= ' ' }
                            } else if (name.equals(Companion.PUB_DATE, ignoreCase = true)) {
                                Log.i("Attribute", "date")
                                item.pubdate = parser.nextText()
                            } else if (name.equals(TITLE, ignoreCase = true)) {
                                Log.i("Attribute", "title")
                                item.title = parser.nextText().trim { it <= ' ' }
                            } else if (name.equals(ENCLOSURE, ignoreCase = true)) {
                                item.enclosure = parser.getAttributeValue(null,"url")
                            } else if (name.equals(GUID, ignoreCase = true)) {
                                item.guid = parser.nextText().trim { it <= ' ' }.replace("/","",ignoreCase = true)
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        name = parser.name
                        Log.i("End tag", name)
                        if (name.equals(ITEM, ignoreCase = true) && item != null) {
                            Log.i("Added", item.toString())
                            list.add(item)
                        } else if (name.equals(CHANNEL, ignoreCase = true)) {
                            done = true
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return list
    }

}