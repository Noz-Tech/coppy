package org.noztek.coppy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform