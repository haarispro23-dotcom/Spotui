
package com.music.spotui

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.music.spotui.di.SongPlayer
import com.music.spotui.ui.components.ChristmasSnow
import com.music.spotui.ui.notification.PlaybackService
import com.music.spotui.ui.theme.SpotuiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private fun isAppExpired(): Boolean {
        return !java.time.LocalDate.now()
            .isBefore(java.time.LocalDate.of(2027, 1, 1))
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val notificationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { /* no-op */ }

    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }

        val token = SessionToken(
            this,
            ComponentName(this, PlaybackService::class.java)
        )

        controllerFuture =
            MediaController.Builder(this, token).buildAsync()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller =
            WindowInsetsControllerCompat(window, window.decorView)

        controller.isAppearanceLightNavigationBars = false

        setContent {
            SpotuiTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    App()

                    ChristmasSnow(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                com.music.spotui.ui.components.UpdatePrompt()

                androidx.compose.material3.TextButton(
                    onClick = {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://haarispro23-dotcom.github.io/haaris-mods/"
                                )
                            )
                        )
                    }
                ) {
                    androidx.compose.material3.Text("HAARIS_MODS 😎")
                }

                if (isAppExpired()) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = {},
                        title = {
                            androidx.compose.material3.Text("EXPIRED")
                        },
                        text = {
                            androidx.compose.material3.Text(
                                "⚠️PURITY APP EXPIRED. PLEASE UPDATE."
                            )
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                "https://haarispro23-dotcom.github.io/haaris-mods/"
                                            )
                                        )
                                    )
                                }
                            ) {
                                androidx.compose.material3.Text("UPDATE NOW")
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(
                                onClick = { finish() }
                            ) {
                                androidx.compose.material3.Text("EXIT OUT")
                            }
                        }
                    )
                }
            }
        }

        com.music.spotui.di.SpotifyWebPlayer.attach(this)

        CoroutineScope(Dispatchers.IO).launch {
            com.music.spotui.util.BackupHelper.performAutoBackup(
                applicationContext
            )
        }

        com.music.spotui.util.DeepLinkHandler.handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        com.music.spotui.util.DeepLinkHandler.handleIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }

        SongPlayer.release()
    }
}
