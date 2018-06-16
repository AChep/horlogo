package com.artemchep.horlogo.extensions

/**
 * Returns the context itself. Use it to seal the `when` expression,
 * for example:
 * ```
 * when(msg) {
 *     Msg.A -> {}
 *     Msg.B -> {}
 * }.sealed()
 * ```
 * so it wont compile until you cover all of the
 * branches.
 */
fun <T> T?.sealed() = this
