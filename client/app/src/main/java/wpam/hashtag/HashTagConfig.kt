package wpam.hashtag


class HashTagConfig {

    // location updates
    var locationUpdateInterval: Long = 1000
    var locationUpdateDistance: Float = 10.0f

    // Map themes
    var mapThemes: HashMap<String, Int> = hashMapOf(
            "dark-blue" to R.raw.map_style_dblue,
            "retro" to R.raw.map_style_retro,
            "clean" to R.raw.map_style_clean,
            "contrast" to R.raw.map_style_contrast
    )
    var activeMapThemeId: Int = R.raw.map_style_retro

}