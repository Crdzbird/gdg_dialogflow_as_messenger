package ni.devotion.gdg_bot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ni.devotion.gdg_bot.FloatingService.OverlayService

class MainActivity : AppCompatActivity() {

    lateinit var service: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShow.setOnClickListener {
            service = Intent(App.instance.applicationContext, OverlayService::class.java)
            App.instance.startService(service)
        }
    }
}
