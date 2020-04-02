package com.infomaniak.meet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val serveur = "https://meet.infomaniak.com/"
    private val serverURL = URL(serveur)
    private val hashCharList = ('a'..'z').toList().toTypedArray()
    lateinit var idRoom: String

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

        userNameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let {
                    createButton.isEnabled = text.length > 1
                }
            }
        })

        shareButton.setOnClickListener {
            ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(R.string.app_name)
                .setText(getString(R.string.shareText,serveur + idRoom))
                .startChooser()
        }

        createButton.setOnClickListener {
            createRoom()
        }

        idRoom = (1..16).map { hashCharList.random() }.joinToString("")
        intent.data?.let { uri ->
            uri.path?.let {
                if (it.isNotBlank()) {
                    idRoom = it.substring(1)
                }
            }
        }
    }

    private fun createRoom() {
        val userName = userNameEdit.text.toString()
        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = userName
        launchRoom(idRoom, userInfo)
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
        finish()
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
