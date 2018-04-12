package wpam.hashtag.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_welcome.*
import wpam.hashtag.GetMetaData
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
        startActivity(Intent(this, MapsActivity::class.java))
    }

    private fun quitBtnHandler(view: View) {
        finish()
    }

    private fun friendsBtnHandler(view: View) {
        TODO("not implemented")
    }

    private fun devBtnHandler(view: View) {
        TODO("not implemented")
    }
}
