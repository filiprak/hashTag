package wpam.hashtag

import android.app.Application
import android.content.Context
import android.provider.Settings


class HashTagApplication : Application() {

    init {
        instance = this
    }

    companion object {
        /** Unique user id */
        lateinit var UID: String
            private set

        private var instance: HashTagApplication? = null

        /* Application config object */
        val config: HashTagConfig = HashTagConfig()

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        // initialize user id
        HashTagApplication.UID = Settings.Secure.getString(this.contentResolver,
                Settings.Secure.ANDROID_ID)

        // Use ApplicationContext.
        // example: SharedPreferences etc...
        val context: Context = HashTagApplication.applicationContext()
    }
}