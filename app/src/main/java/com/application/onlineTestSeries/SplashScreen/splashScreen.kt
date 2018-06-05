package com.application.onlineTestSeries.SplashScreen

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager

import com.application.onlineTestSeries.Home.MainActivity
import com.application.onlineTestSeries.R
import com.application.onlineTestSeries.login.Models.UserData
import com.application.onlineTestSeries.login.View.loginActivity

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_splash_screen.*

class splashScreen : AppCompatActivity() {

    lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)
        val customFont1 = Typeface.createFromAsset(assets, "fonts/LatoRegular.ttf")
        splashText.typeface =customFont1

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            try {
                realm = Realm.getDefaultInstance()
            } catch (e: Exception) {
                // Get a Realm instance for this thread
                val config = RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build()
                realm = Realm.getInstance(config)
            }
            val results = realm.where<UserData>().findAll()
            if (results.size>0){
                val i = Intent(this@splashScreen, MainActivity::class.java)
                startActivity(i)
            }else{
                val i = Intent(this@splashScreen, loginActivity::class.java)
                startActivity(i)
            }
            // close this activity
            finish()
        }, 3000)}
}
