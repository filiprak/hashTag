package wpam.hashtag.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_welcome.*
import wpam.hashtag.GetMetaData
import wpam.hashtag.HashTagApplication
import wpam.hashtag.R
import wpam.hashtag.R.id.textView


class WelcomeActivity : AppCompatActivity() {

    private var tag = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        tag = GetMetaData(this, "log_tag") ?: ""
        setupHandlers()
    }

    private fun setupHandlers() {
        quitBtn.setOnClickListener(View.OnClickListener(::quitBtnHandler))
        startBtn.setOnClickListener(View.OnClickListener(::startBtnHandler))
        friendsBtn.setOnClickListener(View.OnClickListener(::friendsBtnHandler))
        devBtn.setOnClickListener(View.OnClickListener(::devBtnHandler))
    }

    private fun startBtnHandler(view: View) {
        startActivity(Intent(this, LobbyActivity::class.java))
    }

    private fun quitBtnHandler(view: View) {
        finish()
    }

    private fun friendsBtnHandler(view: View) {
        TODO("not implemented")
    }

    /** Developer settings dialog
     */
    private fun devBtnHandler(view: View) {
        val dbuilder = AlertDialog.Builder(this)
        dbuilder.setTitle("Developer settings")
        val dview = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null)

        /* controls */
        val intervValEditText = dview.findViewById<EditText>(R.id.intervVal)
        val distanceValEditText = dview.findViewById<EditText>(R.id.distanceVal)
        val androidIdView = dview.findViewById<EditText>(R.id.androidIdPlainText)
        val mapStylesGroup = dview.findViewById<RadioGroup>(R.id.mapStylesGroup)

        /* set values */
        androidIdView.setText(HashTagApplication.UID)
        intervValEditText.setText(HashTagApplication.config.locationUpdateInterval.toString())
        distanceValEditText.setText(HashTagApplication.config.locationUpdateDistance.toString())
        for ((themeName, themeRId) in HashTagApplication.config.mapThemes) {
            val radio = RadioButton(this)
            radio.text = themeName
            mapStylesGroup.addView(radio)
            if (themeRId == HashTagApplication.config.activeMapThemeId) mapStylesGroup.check(radio.id)
        }

        /* callbacks */
        dview.findViewById<Button>(R.id.openMapsBtn).setOnClickListener { view ->
            startActivity(Intent(this, MapsActivity::class.java))
        }
        mapStylesGroup.setOnCheckedChangeListener({ radioGroup, id ->
            val themeName = radioGroup.findViewById<RadioButton>(id).text.toString()
            HashTagApplication.config.activeMapThemeId = HashTagApplication.config.mapThemes.get(themeName)!!
        })

        /* setup dialog */
        dbuilder.setView(dview)
        dbuilder.setPositiveButton("Save") { dialog, p1 ->
            // save config state
            HashTagApplication.config.locationUpdateInterval = Math.max(intervValEditText.text.toString().toInt().toLong(), 0)
            HashTagApplication.config.locationUpdateDistance = Math.max(distanceValEditText.text.toString().toFloat(), 0.0f)
        }
        dbuilder.setNegativeButton("Cancel") { dialog, p1 -> dialog.dismiss() }
        dbuilder.show()
    }
}
