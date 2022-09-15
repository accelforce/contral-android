package net.accelf.contral.api.ui.states

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

interface StateHolder {
    @Composable
    @SuppressLint("ComposableNaming")
    fun prepare()
}
