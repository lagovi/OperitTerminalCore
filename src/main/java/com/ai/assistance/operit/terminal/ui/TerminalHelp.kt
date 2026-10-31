package com.ai.assistance.operit.terminal.ui

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Host-side sink for terminal element help. The terminal module does not depend
 * on the app help implementation; the app provides this sink and maps the ids
 * to its help catalog.
 */
interface TerminalHelpAnchorSink {
    fun register(id: String, bounds: Rect)
    fun unregister(id: String)
}

val LocalTerminalHelpAnchorSink = compositionLocalOf<TerminalHelpAnchorSink?> { null }

fun Modifier.terminalHelpAnchor(id: String): Modifier = composed {
    val sink = LocalTerminalHelpAnchorSink.current ?: return@composed this
    DisposableEffect(sink, id) {
        onDispose { sink.unregister(id) }
    }
    this.onGloballyPositioned { sink.register(id, it.boundsInRoot()) }
}
