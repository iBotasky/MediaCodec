package com.example.media.aop.annotations


@Target(AnnotationTarget.FUNCTION)
@Retention(value = AnnotationRetention.RUNTIME)
annotation class NetworkCheck() {}