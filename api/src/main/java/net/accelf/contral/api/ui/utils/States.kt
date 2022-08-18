package net.accelf.contral.api.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> useState(value: T) = remember { mutableStateOf(value) }
