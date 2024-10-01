package com.infomaniak.meet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.stores.StoreUtils.checkUpdateIsRequired
import com.infomaniak.meet.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        setTheme(R.style.AppThemeHome)
        super.onCreate(savedInstanceState)
        setContentView(root)

        checkUpdateIsRequired(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, R.style.AppTheme)

        createButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            intent.putExtra("isCreate", true)
            startActivity(intent)
        }

        joinButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            intent.putExtra("isCreate", false)
            startActivity(intent)
        }
    }
}
