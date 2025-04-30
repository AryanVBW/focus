package com.focus.app.ui.blocker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.focus.app.R

class BlockedPageActivity : AppCompatActivity() {

    // JavaScript interface to communicate between HTML and Android
    inner class WebAppInterface {
        @JavascriptInterface
        fun returnToSafety() {
            // Return to home screen
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
            
            // Close this activity
            finish()
        }
        
        @JavascriptInterface
        fun goToSafeSite() {
            // Open a safe site like Google
            val safeIntent = Intent(Intent.ACTION_VIEW)
            safeIntent.data = android.net.Uri.parse("https://www.google.com")
            startActivity(safeIntent)
            
            // Close this activity
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_page)

        val webView: WebView = findViewById(R.id.webViewBlockedPage)
        // Enable JavaScript - needed for our interface
        webView.settings.javaScriptEnabled = true
        
        // Add our JavaScript interface
        webView.addJavascriptInterface(WebAppInterface(), "Android")

        // Get block information from intent
        val packageName = intent.getStringExtra("package_name") ?: ""
        val appName = intent.getStringExtra("app_name") ?: "Content"
        val isFocusMode = intent.getBooleanExtra("is_focus_mode", false)
        val featureName = intent.getStringExtra("feature_name")
        
        // Determine the block type and customize the message
        val pageTitle: String
        val pageMessage: String
        
        when {
            // Adult content blocking
            packageName.isBlank() -> {
                pageTitle = "Site Blocked"
                pageMessage = "This website has been blocked by Focus<br>due to restricted content settings."
            }
            // Focus mode - complete app blocking
            isFocusMode -> {
                pageTitle = "$appName Blocked"
                pageMessage = "$appName is blocked while Focus Mode is enabled.<br>Take a break and focus on what matters."
            }
            // Normal mode - selective feature blocking
            else -> {
                val feature = featureName ?: "Features"
                pageTitle = "$feature Blocked"
                pageMessage = "$appName $feature are blocked to help you<br>avoid distracting short-form content."
            }
        }

        // Load the blocked page with customized content
        val blockedContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Content Blocked</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: sans-serif;
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-items: center;
                        height: 90vh;
                        text-align: center;
                        background-color: #f0f0f0;
                        color: #333;
                        padding: 20px;
                    }
                    .logo-circle {
                        width: 80px;
                        height: 80px;
                        border-radius: 50%;
                        background-color: #E94057;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        margin-bottom: 20px;
                    }
                    .logo-target {
                        width: 60px;
                        height: 60px;
                        border-radius: 50%;
                        border: 4px solid white;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }
                    .logo-inner {
                        width: 30px;
                        height: 30px;
                        border-radius: 50%;
                        border: 3px solid white;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }
                    .logo-dot {
                        width: 10px;
                        height: 10px;
                        border-radius: 50%;
                        background-color: white;
                    }
                    h1 {
                        color: #E94057;
                        font-size: 1.5em;
                        margin-bottom: 10px;
                    }
                    p {
                        font-size: 1.1em;
                        line-height: 1.5;
                    }
                    .button {
                        background-color: #E94057;
                        color: white;
                        border: none;
                        padding: 10px 20px;
                        border-radius: 4px;
                        margin-top: 20px;
                        font-weight: bold;
                        text-decoration: none;
                    }
                </style>
            </head>
            <body>
                <div class="logo-circle">
                    <div class="logo-target">
                        <div class="logo-inner">
                            <div class="logo-dot"></div>
                        </div>
                    </div>
                </div>
                <h1>${pageTitle}</h1>
                <p>${pageMessage}</p>
                <div style="display: flex; gap: 10px; justify-content: center;">
                    <a href="#" class="button" onclick="Android.returnToSafety(); return false;">Return to Home</a>
                    <a href="#" class="button" style="background-color: #4285F4;" onclick="Android.goToSafeSite(); return false;">Go to Google</a>
                </div>
            </body>
            </html>
        """.trimIndent()

        // Load the HTML content directly
        webView.loadDataWithBaseURL(null, blockedContent, "text/html", "UTF-8", null)
    }

    // Override onBackPressed to simply finish the activity
    // so the user doesn't get stuck on the block page.
    override fun onBackPressed() {
        finish()
    }
}
