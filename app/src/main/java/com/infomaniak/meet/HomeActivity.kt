package com.infomaniak.meet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import com.infomaniak.meet.databinding.ActivityHomeBinding
import com.infomaniak.meet.utils.onApplyWindowInsetsListener

class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        setTheme(R.style.AppThemeHome)
        super.onCreate(savedInstanceState)
        setContentView(root)

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

        footer.onApplyWindowInsetsListener { view, insets, _ ->
            view.updatePadding(bottom = insets.bottom)
        }
    }
}
