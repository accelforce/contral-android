package net.accelf.contral.api.plugin

import net.accelf.contral.api.plugin.MinorVersion.Companion.minor
import net.accelf.contral.api.plugin.MinorVersion.Companion.patch
import org.junit.Assert.*
import org.junit.Test

class PluginResolverTest {

    @Test
    fun require_denyDifferentMajor() {
        PluginResolver("sample").apply {
            assertThrows("Two duplicated dependencies for sample requires different major version: 1.2.* and 2.2.*", IllegalStateException::class.java) {
                require("example", 1 minor 2)
                require("example", 2 minor 2)
            }
        }
    }

    @Test
    fun require_takeBiggerMinor() {
        val plugin = PluginResolver("sample").apply {
            version = 0 minor 0 patch 0
            require("bigger-than-before", 1 minor 3)
            require("bigger-than-before", 1 minor 5)

            require("smaller-than-before", 2 minor 3)
            require("smaller-than-before", 2 minor 1)

            require("same-to-before", 3 minor 3)
            require("same-to-before", 3 minor 3)
        }
            .build()

        assertEquals(1 minor 5, plugin.dependencies["bigger-than-before"])
        assertEquals(2 minor 3, plugin.dependencies["smaller-than-before"])
        assertEquals(3 minor 3, plugin.dependencies["same-to-before"])
    }
}
