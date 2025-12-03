package com.hshim.apisis.common.annotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldDescription(
    val description: String
)
