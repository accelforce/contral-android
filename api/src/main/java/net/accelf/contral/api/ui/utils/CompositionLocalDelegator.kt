package net.accelf.contral.api.ui.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import kotlin.reflect.KProperty

fun <T> compositionLocalOf(
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
) = CompositionLocalDelegator { compositionLocalOf(policy, it) }
fun <T> staticCompositionLocalOf() = CompositionLocalDelegator<T>(::staticCompositionLocalOf)

class CompositionLocalDelegator<T>(
    factory: (() -> T) -> ProvidableCompositionLocal<T>,
) {

    private lateinit var name: String

    @SuppressLint("CompositionLocalNaming")
    private val value = factory { error("$name is not set") }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): ProvidableCompositionLocal<T> {
        name = property.name
        return value
    }
}
