package com.infomaniak.meet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val serverURL = URL("https://meet.infomaniak.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        createButton.setOnClickListener {
            launchRoom()
        }
    }

    private fun launchRoom() {

        val roomNameText = roomNameEdit.text.toString()
        if (roomNameText.isNotBlank()) {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomNameText)
                .build()
            JitsiMeetActivity.launch(this, options)
        } else {
            roomNameLayout.error = "Le champs ne peut etre vide"
        }
    }
}
