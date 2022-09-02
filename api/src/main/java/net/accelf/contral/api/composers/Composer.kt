package net.accelf.contral.api.composers

interface Composer {
    /**
     * @return [Boolean] Clears contents if `true`
     */
    suspend fun compose(content: String): Boolean
}
