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
import kotlinx.android.synthetic.main.activity_welcome.*
import wpam.hashtag.GetMetaData
import wpam.hashtag.HashTagApplication
import wpam.hashtag.R


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
        val intervValEditText = dview.findViewById<EditText>(R.id.intervVal)
        val fintervValEditText = dview.findViewById<EditText>(R.id.fintervVal)
        val mapsbtn = dview.findViewById<Button>(R.id.openMapsBtn)
        mapsbtn.setOnClickListener { view ->
            startActivity(Intent(this, MapsActivity::class.java))
        }

        val androidIdView = dview.findViewById<EditText>(R.id.androidIdPlainText)

        androidIdView.setText(HashTagApplication.UID)

        dbuilder.setView(dview)
        dbuilder.setPositiveButton("Save") { dialog, p1 ->
            //@todo save options state
        }
        dbuilder.setNegativeButton("Cancel") { dialog, p1 ->
            dialog.dismiss()
        }
        dbuilder.show()
    }
}
