package com.infomaniak.meet

import android.content.Intent
import android.os.Bundle
import androidx.core.view.updatePadding
import com.infomaniak.core.inappupdate.BaseInAppUpdateManager.Companion.checkUpdateIsRequired
import com.infomaniak.core.inappupdate.updatemanagers.InAppUpdateManager
import com.infomaniak.core.legacy.utils.SnackbarUtils.showSnackbar
import com.infomaniak.core.network.NetworkConfiguration
import com.infomaniak.core.ui.view.edgetoedge.EdgeToEdgeActivity
import com.infomaniak.meet.databinding.ActivityHomeBinding
import com.infomaniak.meet.utils.onApplyWindowInsetsListener

class HomeActivity : EdgeToEdgeActivity() {
    private val inAppUpdateManager by lazy { InAppUpdateManager(activity = this) }

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        setTheme(R.style.AppThemeHome)
        super.onCreate(savedInstanceState)
        setContentView(root)

        configureInfomaniakCore()

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

    private fun initAppUpdateManager() {
        inAppUpdateManager.init(
            onInstallFailure = { showSnackbar(title = R.string.errorUpdateInstall) },
        )

        checkUpdateIsRequired(
            manager = inAppUpdateManager,
            applicationId = BuildConfig.APPLICATION_ID,
            applicationVersionCode = BuildConfig.VERSION_CODE,
            theme = R.style.AppTheme
        )
    }

    private fun configureInfomaniakCore() {
        NetworkConfiguration.init(
            appId = BuildConfig.APPLICATION_ID,
            appVersionCode = BuildConfig.VERSION_CODE,
            appVersionName = BuildConfig.VERSION_NAME,
        )
    }
}
