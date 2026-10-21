package com.music.spotui

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.music.spotui.di.SongPlayer
import com.music.spotui.ui.notification.PlaybackService
import com.music.spotui.ui.theme.SpotuiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
        private fun isAppExpired(): Boolean {
        return !java.time.LocalDate.now()
            .isBefore(java.time.LocalDate.of(2021, 12, 10))
        }
    
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        if (isAppExpired()) {
    androidx.appcompat.app.AlertDialog.Builder(this)
        .setTitle("EXPIRED")
        .setMessage("PURITY APP EXPIRED. PLEASE UPDATE.")
        .setPositiveButton("UPDATE NOW") { _, _ ->
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://haarispro23-dotcom.github.io/haaris-mods/")
                )
            )
        }
        .setNegativeButton("EXIT OUT") { _, _ ->
            finish()
        }
        .setCancelable(false)
        .show()

    return
}
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Ask for notification permission (Android 13+) so the media notification shows.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Connect a controller to bootstrap the MediaSessionService: this brings up
        // the system media notification and keeps playback alive in the background.
        val token = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, token).buildAsync()


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightNavigationBars = false
        setContent {
    SpotuiTheme {
        App()

        com.music.spotui.ui.components.UpdatePrompt()
        com.music.spotui.ui.components.DefaultAppPrompt()

        androidx.compose.material3.TextButton(
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://haarispro23-dotcom.github.io/haaris-mods/")
                )
                startActivity(intent)
            }
        ) {
            androidx.compose.material3.Text("HAARIS_MODS 😎")
        }
    }
}

        // Experimental Spotify web-player engine: attach its hidden WebView AFTER
        // setContent so the Compose content view doesn't replace/orphan it (an
        // orphaned WebView gets a 0×0 viewport and Spotify won't render/navigate).
        com.music.spotui.di.SpotifyWebPlayer.attach(this)

        // Perform background auto-backup if a backup directory is configured.
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            com.music.spotui.util.BackupHelper.performAutoBackup(applicationContext)
        }

        // Handle initial deep link intent if launched via Spotify link
        com.music.spotui.util.DeepLinkHandler.handleIntent(intent)
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        com.music.spotui.util.DeepLinkHandler.handleIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        SongPlayer.release()
    }
}



