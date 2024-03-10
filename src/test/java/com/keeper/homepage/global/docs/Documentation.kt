package com.keeper.homepage.global.docs

import org.junit.jupiter.api.Test

@Test
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Documentation(
        val documentName: String,
)
