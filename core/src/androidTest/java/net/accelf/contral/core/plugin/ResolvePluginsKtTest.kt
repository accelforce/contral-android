package net.accelf.contral.core.plugin

import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResolvePluginsKtTest {

    @Test
    fun getInstalledApplicationsCompat_couldBeCalled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val apps = context.packageManager.getInstalledApplicationsCompat(PackageManager.GET_META_DATA)
        assertTrue("no apps found", apps.isNotEmpty())
        assertTrue("flag is not applied", apps.any { it.metaData != null })
    }
}
