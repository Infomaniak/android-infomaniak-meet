package com.infomaniak.meet

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.join_alertview.view.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val serveur = "https://meet.infomaniak.com/"
    private val serverURL = URL(serveur)
    private val hashCharList = ('a'..'z').toList().toTypedArray()
    private lateinit var idRoom: String

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
            createButton.callOnClick()
        }

        userNameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text?.let {
                    if (text.length > 1) {
                        userNameLayout.error = null
                    }
                }
            }
        })

        shareButton.setOnClickListener {
            ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(R.string.app_name)
                .setText(getString(R.string.shareText, serveur + idRoom))
                .startChooser()
        }

        createButton.setOnClickListener {
            checkUsername { userName -> createRoom(userName) }
        }

        joinButton.setOnClickListener {
            checkUsername { userName -> joinRoom(userName) }
        }

        val userName =
            PreferenceManager.getDefaultSharedPreferences(this).getString("userName", null)
        userNameEdit.setText(userName)

        idRoom = (1..16).map { hashCharList.random() }.joinToString("")
        intent.data?.let { uri ->
            when (uri.scheme) {
                "https" -> {
                    uri.path?.let { path ->
                        if (path.isNotBlank()) {
                            idRoom = path.substring(1)
                            setAcceptButton()
                        }
                    }
                }
                else -> {
                    uri.host?.let { host ->
                        if (host.isNotBlank()) {
                            idRoom = host
                            setAcceptButton()
                        }
                    }
                }
            }
        }
    }

    private fun setAcceptButton() {
        createButton.text = getString(R.string.acceptButton)
        createButton.callOnClick()
    }

    override fun onPause() {
        super.onPause()
        val userName = userNameEdit.text.toString()
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("userName", userName)
            .apply()
    }

    private fun checkUsername(callback: (userName: String) -> Unit) {
        val userName = userNameEdit.text.toString()
        if (userName.length > 1) {
            callback(userName)
        } else {
            userNameLayout.error = getString(R.string.mandatoryUserName)
        }
    }

    private fun joinRoom(userName: String) {
        val view: View = layoutInflater.inflate(R.layout.join_alertview, null)

        AlertDialog.Builder(this, R.style.DialogStyle)
            .setTitle(R.string.joinRoomTitle)
            .setMessage(R.string.joinRoomAlertDescription)
            .setView(view)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.joinRoom) { _: DialogInterface?, _: Int ->
                val roomName = view.roomNameEdit.text.toString()
                if (roomName.length > 1) {
                    createRoom(userName, roomName)
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun createRoom(userName: String, roomName: String = idRoom) {
        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = userName
        launchRoom(roomName, userInfo)
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
