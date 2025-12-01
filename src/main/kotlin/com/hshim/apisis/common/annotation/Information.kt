package com.hshim.apisis.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Information(
    val title: String,
    val version: String,
    val callLimit: Long = -1
)
