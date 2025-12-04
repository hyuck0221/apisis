package com.hshim.apisis.common.util

import java.util.*

object QueueUtil {
    fun Queue<Char>.polls(size: Int) = String((0 until size).map { this.poll() }.toCharArray())
    fun Queue<Char>.throwPolls(size: Int): Queue<Char> {
        this.polls(size)
        return this
    }
}