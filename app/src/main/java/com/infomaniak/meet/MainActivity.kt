package com.infomaniak.meet

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private val serveur = "https://kmeet.infomaniak.com/"
    private val serverURL = URL(serveur)
    private val hashCharList = ('a'..'z').toList().toTypedArray()
    private lateinit var idRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
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

        val isCreate = intent.getBooleanExtra("isCreate", false)

        if (isCreate) {
            titleMain.setText(R.string.titleCreate)
            roomNameLayout.visibility = GONE
            createButton.setText(R.string.createButton)
        }

        createButton.setOnClickListener {
            if (isCreate) {
                checkUsername { userName ->
                    createRoom(userName)
                }
            } else {
                checkRoomName { roomName ->
                    idRoom = roomName
                    checkUsername { userName ->
                        createRoom(userName)
                    }
                }
            }
        }

        val userName = PreferenceManager.getDefaultSharedPreferences(this).getString("userName", null)
        userNameEdit.setText(userName)

        idRoom = (1..16).map { hashCharList.random() }.joinToString("")
        intent.data?.let { uri ->
            when (uri.scheme) {
                "https" -> {
                    uri.path?.let { path ->
                        if (path.isNotBlank()) {
                            roomNameEdit.setText(path.substring(1))
                            createButton.callOnClick()
                        }
                    }
                }
                else -> {
                    uri.host?.let { host ->
                        if (host.isNotBlank()) {
                            roomNameEdit.setText(host)
                            createButton.callOnClick()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val userName = userNameEdit.text.toString()
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("userName", userName).apply()
    }

    private fun checkRoomName(callback: (roomName: String) -> Unit) {
        var roomName = roomNameEdit.text.toString()
        if (roomName.isNotEmpty()) {
            val regex = "^(\\d{3}-\\d{4}-\\d{3}|\\d{10})$".toRegex()
            if (regex.containsMatchIn(roomName)) {
                lifecycleScope.launch {
                    roomName = getRoomName(roomName.replace("-", "")) ?: ""
                    if (roomName.isEmpty()) {
                        roomNameEdit.error = getString(R.string.codeDoesntExistError)
                    } else {
                        callback(roomName)
                    }
                }
            } else {
                callback(roomName.replace(serveur, ""))
            }
        } else {
            roomNameLayout.error = getString(R.string.mandatoryField)
        }
    }

    private fun checkUsername(callback: (userName: String) -> Unit) {
        val userName = userNameEdit.text.toString()
        if (userName.length > 1) {
            callback(userName)
        } else {
            userNameLayout.error = getString(R.string.mandatoryUserName)
        }
    }

    private fun createRoom(userName: String, roomName: String = idRoom) {
        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = userName
        launchRoom(roomName, userInfo)
    }

    private fun launchRoom(roomNameText: String, userInfo: JitsiMeetUserInfo) {
        val roomName = URLEncoder.encode(roomNameText, "UTF-8").replace("+", "%20")
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(roomName)
            .setUserInfo(userInfo)
            .build()
        JitsiMeetActivity.launch(this, options)
        finish()
    }

    suspend fun getRoomName(code: String): String? {
        createButton.isClickable = false
        createButton.showProgress {
            progressColor = Color.WHITE
        }
        var result: ApiResponse<CodeRoomName>? = null
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://welcome.infomaniak.com/api/components/meet/conference/${code}")
                .get()
                .build()

            val response = OkHttpClient.Builder().build().newCall(request).execute()
            val bodyResponse = response.body()?.string()

            result =
                Gson().fromJson<ApiResponse<CodeRoomName>>(bodyResponse, object : TypeToken<ApiResponse<CodeRoomName>>() {}.type)
        }

        createButton?.isClickable = true
        createButton?.hideProgress(R.string.startButton)
        return result?.data?.name
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
