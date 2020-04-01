package com.infomaniak.meet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val serverURL = URL("https://meet.infomaniak.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        userNameEdit.onDone {
            if (createButton.isEnabled) createButton.callOnClick()
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                createButton.isEnabled =
                    roomNameEdit.text.toString().length > 2 && userNameEdit.text.toString().length > 2
            }
        }
        roomNameEdit.addTextChangedListener(textWatcher)
        userNameEdit.addTextChangedListener(textWatcher)

        createButton.setOnClickListener {
            createRoom()
        }
    }

    private fun createRoom() {
        val roomName = roomNameEdit.text.toString()
        if (roomName.isNotBlank()) {
            val userName = userNameEdit.text.toString()
            if (userName.isNotBlank()) {
                val userInfo = JitsiMeetUserInfo()
                userInfo.displayName = userName
                launchRoom(roomName, userInfo)
            } else {
                userNameLayout.error = "Le champs ne peut etre vide"
            }
        } else {
            roomNameLayout.error = "Le champs ne peut etre vide"
        }
    }

    private fun launchRoom(
        roomNameText: String,
        userInfo: JitsiMeetUserInfo
    ) {
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(roomNameText)
            .setUserInfo(userInfo)
            .build()
        JitsiMeetActivity.launch(this, options)
    }

    private fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                callback.invoke()
                true
            }
            false
        }
    }
}
