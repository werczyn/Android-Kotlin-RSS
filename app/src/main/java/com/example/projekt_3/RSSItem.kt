package com.example.projekt_3

data class RSSItem(
    var title : String = "",
    var link : String = "",
    var description : String = "",
    var pubdate : String = "",
    var guid : String = "",
    var enclosure : String = ""
)