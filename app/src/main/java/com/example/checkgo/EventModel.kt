package com.example.checkgo

data class EventModel(
    var eventID: String? = null,
    var name: String? = null,
    var type: String? = null,
    var date: String? = null,
    var details: String? = null,
    var location: String? = null
    //var userId: String? = null
//    var photo: Image? = null sends as a text not as a picture
// https://www.youtube.com/watch?v=KuqLbN41Rag
)