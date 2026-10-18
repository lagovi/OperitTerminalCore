package com.ai.assistance.operit.terminal

import java.io.File

/**
 * Supplies the Ubuntu rootfs archive that the local terminal unpacks on first
 * use.
 *
 * The archive is no longer bundled inside the APK (it is 61 MiB), so the host
 * application installs an implementation into [TerminalRootfs.provider] before
 * the terminal environment is first initialized. The archive then comes from
 * the fork's own release download, not from `context.assets`.
 */
interface RootfsArchiveProvider {
    /**
     * Ensures the rootfs archive exists at [destination], downloading it when
     * it is absent. [destination] is `filesDir/[TerminalRootfs.ARCHIVE_NAME]`
     * and the implementation must place the verified archive exactly there.
     *
     * Success means the terminal has what it needs: either [destination] now
     * exists, or Ubuntu is already installed and no archive is required.
     */
    suspend fun ensureArchive(destination: File): Result<File>
}

/**
 * The one place that knows the archive name and where the installed Ubuntu
 * tree lives, shared by the terminal and by the host application's gate.
 */
object TerminalRootfs {
    const val ARCHIVE_NAME = "ubuntu-noble-aarch64-pd-v4.18.0.tar.xz"

    /** The host application must set this before the terminal initializes. */
    @Volatile
    var provider: RootfsArchiveProvider? = null

    /** The installed Ubuntu tree under [filesDir]. */
    fun installedRootfs(filesDir: File): File =
        File(filesDir, "usr/var/lib/proot-distro/installed-rootfs/ubuntu")

    /**
     * True once [installedRootfs] has been unpacked. Mirrors the marker the
     * install script writes (`.operit_installed_ok`) and the fallback it also
     * accepts (`etc/issue.net` already present from an older build).
     */
    fun isInstalled(filesDir: File): Boolean {
        val root = installedRootfs(filesDir)
        return File(root, ".operit_installed_ok").isFile || File(root, "etc/issue.net").isFile
    }
}
