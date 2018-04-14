package wpam.hashtag

import android.location.Location

class HashTagLocation {
    private lateinit var uid : String

    private val lng : Double = 0.0
    private val lat : Double = 0.0

    private val accuracy : Float = 0.0f

    private val bearing : Float = 0.0f
    private val speed : Double = 0.0

    constructor(json: String) {

    }

    constructor(location: Location)

    override fun toString(): String {
        //@todo
        return super.toString()
    }
}

