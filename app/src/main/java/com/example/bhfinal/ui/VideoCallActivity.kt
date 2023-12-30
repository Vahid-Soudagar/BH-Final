package com.example.bhfinal.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bhfinal.databinding.ActivityVideoCallBinding
import com.example.bhfinal.utils.Constants.REQUEST_CODE_PERMISSION_AUDIO

class VideoCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoCallBinding
    private lateinit var myWebView: WebView
    private val TAG = "myTag"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myWebView = binding.webView


        // Request audio permission
        requestAudioVideoPermission()

        // Load the URL
        val url: String = "https://pre-prod.av-portal.pages.dev/call?user-id=1892&user-type=PATIENT&user-name=patient-1&Appointment-id=CALL12345"
        myWebView.loadUrl(url)

        myWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = false
            allowContentAccess = true
            allowFileAccess = true
            saveFormData = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }

        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
                Log.d(TAG, "onPageStarted: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
                Log.d(TAG, "onPageFinished: $url")
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (error != null) {
                    Log.e(TAG, "onReceivedError: ${error.description}")
                }
            }
        }

        myWebView.webChromeClient = object : WebChromeClient() {
            @SuppressLint("ObsoleteSdkInt")
            override fun onPermissionRequest(request: PermissionRequest?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val resources = request?.resources
                    if (!resources.isNullOrEmpty()) {
                        // Grant permission based on the requested resources
                        if (resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE) ||
                            resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                        ) {
                            request.grant(resources)
                        } else {
                            super.onPermissionRequest(request)
                        }
                    }
                } else {
                    super.onPermissionRequest(request)
                }
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.d(TAG, "onConsoleMessage: ${consoleMessage?.message()}")
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }

    private fun requestAudioVideoPermission() {
        val audioPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val audioManage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS)

        val permissionsToRequest = mutableListOf<String>()

        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.RECORD_AUDIO)
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.CAMERA)
        }

        if (audioManage != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.MODIFY_AUDIO_SETTINGS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_PERMISSION_AUDIO
            )
            Log.d(TAG, "requestAudioAndCameraPermission: Permissions not granted, requesting...")
        } else {
            Log.d(TAG, "requestAudioAndCameraPermission: Permissions already granted")
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (myWebView.canGoBack()) {
            Log.d(TAG, "onBackPressed: Going back in WebView")
        } else {
            super.onBackPressed()
            Log.d(TAG, "onBackPressed: Exiting the activity")
        }
    }
}
