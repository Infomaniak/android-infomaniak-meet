package com.infomaniak.meet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppThemeHome)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        createButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("isCreate", true)
            startActivity(intent)
        }

        joinButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("isCreate", false)
            startActivity(intent)
        }
    }
}