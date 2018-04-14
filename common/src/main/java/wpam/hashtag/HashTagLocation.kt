package wpam.hashtag

import com.google.gson.Gson
import com.google.gson.JsonObject


class HashTagLocation {
    var uid : String? = null

    var lng : Double = 0.0
    var lat : Double = 0.0

    var accuracy : Float? = null
    var bearing : Float? = null
    var speed : Float? = null


    var hasAccuracy : Boolean = false
    var hasBearing : Boolean = false
    var hasSpeed : Boolean = false
    var hasUid : Boolean = false


    constructor(lat: Double, lng: Double, accuracy: Float, bearing: Float, speed: Float) {
        this.lat = lat
        this.lng = lng
        this.accuracy = accuracy
        this.bearing = bearing
        this.speed = speed
    }

    constructor(jsonStr: String) {
        val parsed = Gson().fromJson(jsonStr, HashTagLocation::class.java)
        if (parsed != null) {
            uid = parsed.uid
            lng = parsed.lng
            lat = parsed.lat
            accuracy = parsed.accuracy
            bearing = parsed.bearing
            speed = parsed.speed

            hasUid = parsed.uid != null
            hasAccuracy = parsed.hasBearing
            hasBearing = parsed.hasBearing

        }
    }

    constructor(parsed: JsonObject?) {
        if (parsed != null) {
            uid = parsed.get("uid")?.asString
            lng = parsed.get("lng").asDouble
            lat = parsed.get("lat").asDouble
            accuracy = parsed.get("accuracy")?.asFloat
            bearing = parsed.get("bearing")?.asFloat
            speed = parsed.get("speed")?.asFloat

            hasUid = parsed.get("hasUid").asBoolean
            hasAccuracy = parsed.get("hasAccuracy").asBoolean
            hasBearing = parsed.get("hasBearing").asBoolean
            hasSpeed = parsed.get("hasSpeed").asBoolean
        }
    }

    fun jsonify(): String {
        return Gson().toJson(this)
    }

    override fun toString(): String {
        //@todo
        return super.toString()
    }
}

