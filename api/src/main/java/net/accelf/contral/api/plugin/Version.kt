package net.accelf.contral.api.plugin

data class MinorVersion(
    val major: Int,
    val minor: Int,
) {
    override fun toString() = "$major.$minor.*"

    companion object {
        infix fun Int.minor(minor: Int) = MinorVersion(this, minor)
        infix fun MinorVersion.patch(patch: Int) = Version(major, minor, patch)
    }
}

data class Version(
    val major: Int, // Increment when destructive changes are made
    val minor: Int, // Increment when new features are supported
    val patch: Int, // Increment when other changes are made
) {
    override fun toString() = "$major.$minor.$patch"
}
