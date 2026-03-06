package com.example.ksweb

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var phpProcess: Process? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        thread {
            try {
                // 1. Extract PHP binary and Web files from assets
                val appDataDir = filesDir.absolutePath
                val phpBinPath = "$appDataDir/php"
                val wwwDir = "$appDataDir/www"

                extractAssets("php", phpBinPath)
                extractAssetsFolder("www", wwwDir)

                // 2. Set executable permission for PHP
                val phpFile = File(phpBinPath)
                if (phpFile.exists()) {
                    phpFile.setExecutable(true, false)
                } else {
                    Log.e("KSWEB", "PHP binary not found! Did you add it to assets/php?")
                    return@thread
                }

                // 3. Start PHP built-in web server
                Log.d("KSWEB", "Starting PHP server on 127.0.0.1:8080...")
                val pb = ProcessBuilder(phpBinPath, "-S", "127.0.0.1:8080", "-t", wwwDir)
                pb.environment()["TMPDIR"] = appDataDir // Fix for sessions/uploads on Android
                pb.redirectErrorStream(true)
                phpProcess = pb.start()

                // Read server output for debugging
                thread {
                    phpProcess?.inputStream?.bufferedReader()?.use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            Log.d("KSWEB_PHP", line ?: "")
                        }
                    }
                }

                // 4. Load the Web Interface in WebView
                runOnUiThread {
                    // Give server a tiny bit of time to start
                    webView.postDelayed({
                        webView.loadUrl("http://127.0.0.1:8080")
                    }, 1000)
                }

            } catch (e: Exception) {
                Log.e("KSWEB", "Error starting server: ${e.message}", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Terminate PHP process when app closes
        phpProcess?.destroy()
    }

    // --- Helper functions to extract assets ---
    private fun extractAssets(assetName: String, targetPath: String) {
        val targetFile = File(targetPath)
        if (!targetFile.exists() || assetName == "www") { // Force extract www folder for updates
            try {
                assets.open(assetName).use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                Log.e("KSWEB", "Failed to extract asset: $assetName", e)
            }
        }
    }

    private fun extractAssetsFolder(assetPath: String, targetPath: String) {
        val targetDir = File(targetPath)
        if (!targetDir.exists()) targetDir.mkdirs()

        assets.list(assetPath)?.forEach { file ->
            val assetFile = "$assetPath/$file"
            val targetFile = "$targetPath/$file"
            val fileList = assets.list(assetFile)
            if (fileList.isNullOrEmpty()) {
                // It's a file
                extractAssets(assetFile, targetFile)
            } else {
                // It's a directory
                extractAssetsFolder(assetFile, targetFile)
            }
        }
    }
}