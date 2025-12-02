package com.hshim.apisis.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Information(
    val category: String,
    val title: String,
    val description: String,
    val version: String,
    val callLimit: Long = -1
)
