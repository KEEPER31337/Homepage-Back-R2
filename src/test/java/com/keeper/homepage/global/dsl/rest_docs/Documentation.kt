package com.keeper.homepage.global.dsl.rest_docs

import org.junit.jupiter.api.Test

@Test
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Documentation(
        val documentName: String,
)
