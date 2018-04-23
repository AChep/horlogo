package com.artemchep.horlogo.extensions

infix fun Int.contains(v: Int): Boolean = (this and v) == v
