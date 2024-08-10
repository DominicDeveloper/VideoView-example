package com.asadbek.videoplayer

class VideoFile {
    var id:Int? = null
    var pushingKey:String? = null
    var videoLink:String? = null

    constructor()
    constructor(id: Int?, pushingKey: String?, videoLink: String?) {
        this.id = id
        this.pushingKey = pushingKey
        this.videoLink = videoLink
    }

    constructor(pushingKey: String?, videoLink: String?) {
        this.pushingKey = pushingKey
        this.videoLink = videoLink
    }


}