package com.infomaniak.meet.utils

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.onApplyWindowInsetsListener(
    shouldConsumeInsets: Boolean = false,
    callback: (View, Insets, Boolean) -> Unit,
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        val cutoutInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
        val combinedInsets = Insets.max(systemBarsInsets, cutoutInsets)

        val navBarVisible: Boolean = windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())

        callback(view, combinedInsets, navBarVisible)

        if (shouldConsumeInsets) WindowInsetsCompat.CONSUMED else windowInsets
    }
}