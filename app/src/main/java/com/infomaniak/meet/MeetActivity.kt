package com.infomaniak.meet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.facebook.react.modules.core.PermissionListener
import com.infomaniak.meet.databinding.ActivityMeetBinding
import com.infomaniak.meet.utils.onApplyWindowInsetsListener
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetOngoingConferenceService
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.jitsi.meet.sdk.JitsiMeetView
import java.net.URL

class MeetActivity : AppCompatActivity(), JitsiMeetActivityInterface {

    private val binding by lazy { ActivityMeetBinding.inflate(layoutInflater) }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            this@MeetActivity.onBroadcastReceived(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        super.onCreate(savedInstanceState)
        setContentView(root)

        registerBroadcast()

        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(intent.getStringExtra(INTENT_EXTRA_ROOM_NAME_KEY))
            .setServerURL(intent.getSerializableExtra(INTENT_EXTRA_SERVER_URL_KEY) as URL)
            .setUserInfo(JitsiMeetUserInfo(intent.extras))
            .build()

        jitsiMeetView.join(options)

        jitsiMeetView.handleEdgeToEdge()
    }

    override fun onDestroy() {
        binding.jitsiMeetView.abort()

        JitsiMeetOngoingConferenceService.abort(this)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.broadcastReceiver)
        JitsiMeetActivityDelegate.onHostDestroy(this)

        super.onDestroy()
    }

    override fun requestPermissions(
        permissions: Array<String?>,
        requestCode: Int,
        grantResults: PermissionListener,
    ) {
        JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, grantResults)
    }

    private fun JitsiMeetView.handleEdgeToEdge() {
        onApplyWindowInsetsListener { view, insets, navBarVisible ->
            updatePadding(bottom = if (navBarVisible) insets.bottom else 0)
        }

        if (SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
    }

    private fun registerBroadcast() {
        val intentFilter = IntentFilter()

        for (type in BroadcastEvent.Type.entries) {
            intentFilter.addAction(type.action)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(this.broadcastReceiver, intentFilter)
    }

    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.type) {
                BroadcastEvent.Type.CONFERENCE_JOINED -> {
                    JitsiMeetOngoingConferenceService.launch(this, event.data)
                }
                BroadcastEvent.Type.READY_TO_CLOSE -> {
                    binding.jitsiMeetView.abort()
                    finish()
                }
                else -> Unit
            }
        }
    }

    companion object {
        const val INTENT_EXTRA_ROOM_NAME_KEY = "roomName"
        const val INTENT_EXTRA_SERVER_URL_KEY = "serverURL"
        const val INTENT_EXTRA_DISPLAY_NAME_KEY = "displayName"
        const val INTENT_EXTRA_EMAIL_KEY = "email"
        const val INTENT_EXTRA_AVATAR_URL_KEY = "avatarURL"
    }
}