package com.infomaniak.meet

import android.graphics.Color
import android.net.Uri
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

    private var serverURL = URL("https://kmeet.infomaniak.com/")
    private val hashCharList = ('a'..'z').toList().toTypedArray()
    private lateinit var idRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setVideoMuted(true)
            .setWelcomePageEnabled(false)
            .setFeatureFlag("live-streaming.enabled", false)
            .setFeatureFlag("video-share.enabled", false)
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
                        }
                    }
                }
                else -> {
                    roomNameEdit.setText(setServerURLFromUri(uri))
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
                    val roomCredential = getRoomCredential(roomName.replace("-", ""))
                    roomCredential?.hostname?.let {
                        serverURL = URL("https://$it/")
                    }
                    roomName = roomCredential?.name ?: ""
                    if (roomName.isEmpty()) {
                        roomNameEdit.error = getString(R.string.codeDoesntExistError)
                    } else {
                        callback(roomName)
                    }
                }
            } else {
                val uri = Uri.parse(roomName)
                if (uri.scheme == null) {
                    callback(URLEncoder.encode(roomName, "UTF-8").replace("+", "%20"))
                } else {
                    callback(setServerURLFromUri(uri))
                }
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

    private fun createRoom(userName: String) {
        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = userName
        launchRoom(idRoom, userInfo)
    }

    private fun launchRoom(roomName: String, userInfo: JitsiMeetUserInfo) {
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(roomName)
            .setServerURL(serverURL)
            .setUserInfo(userInfo)
            .build()
        JitsiMeetActivity.launch(this, options)
        finish()
    }

    private fun setServerURLFromUri(uri: Uri): String {
        serverURL = URL("https://${uri.host}/")
        return uri.toString().replace("${uri.scheme}://${uri.host}/", "")
    }

    private suspend fun getRoomCredential(code: String): CodeRoomCredential? {
        createButton.isClickable = false
        createButton.showProgress {
            progressColor = Color.WHITE
        }
        var result: ApiResponse<CodeRoomCredential>? = null
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(getCodeRoomCredential(code))
                    .get()
                    .build()

                val response = OkHttpClient.Builder().build().newCall(request).execute()
                val bodyResponse = response.body()?.string()

                result =
                    Gson().fromJson<ApiResponse<CodeRoomCredential>>(
                        bodyResponse,
                        object : TypeToken<ApiResponse<CodeRoomCredential>>() {}.type
                    )
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }

        createButton?.isClickable = true
        createButton?.hideProgress(R.string.startButton)
        return result?.data
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

    companion object {
        private const val kmeetApi = "https://welcome.infomaniak.com/api/components/"

        fun getCodeRoomCredential(conferenceName: String) = "${kmeetApi}meet/conference/code/${conferenceName}"
    }
}
