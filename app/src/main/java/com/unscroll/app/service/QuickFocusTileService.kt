package com.unscroll.app.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.unscroll.app.data.UnscrollPreferences

@RequiresApi(Build.VERSION_CODES.N)
class QuickFocusTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        try {
            val prefs = UnscrollPreferences(this)
            qsTile?.apply {
                state = if (prefs.interceptorActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                label = if (prefs.interceptorActive) "Unscroll Active" else "Unscroll Paused"
                updateTile()
            }
        } catch (_: Exception) {
        }
    }

    override fun onClick() {
        super.onClick()
        try {
            val prefs = UnscrollPreferences(this)
            val newState = !prefs.interceptorActive
            prefs.interceptorActive = newState

            qsTile?.apply {
                state = if (newState) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                label = if (newState) "Unscroll Active" else "Unscroll Paused"
                updateTile()
            }

            if (newState) {
                FocusNotificationManager.showMindfulnessNudge(
                    this,
                    "Shield Enabled",
                    "Unscroll is now actively intercepting short-form feeds."
                )
            }
        } catch (_: Exception) {
        }
    }
}
