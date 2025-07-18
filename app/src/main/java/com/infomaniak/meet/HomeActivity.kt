package com.infomaniak.meet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.core.utils.SnackbarUtils.showSnackbar
import com.infomaniak.lib.stores.updatemanagers.InAppUpdateManager
import com.infomaniak.meet.databinding.ActivityHomeBinding
import com.infomaniak.lib.stores.StoreUtils.checkUpdateIsRequired
import androidx.core.view.updatePadding
import com.infomaniak.meet.utils.onApplyWindowInsetsListener

class HomeActivity : AppCompatActivity() {

    private val inAppUpdateManager by lazy { InAppUpdateManager(this, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE) }

    private fun initAppUpdateManager() {
        inAppUpdateManager.init(
            onInstallFailure = { showSnackbar(title = R.string.errorUpdateInstall) },
        )
    }

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        setTheme(R.style.AppThemeHome)
        super.onCreate(savedInstanceState)
        setContentView(root)

        this@HomeActivity.checkUpdateIsRequired(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, R.style.AppTheme)

        initAppUpdateManager()

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
