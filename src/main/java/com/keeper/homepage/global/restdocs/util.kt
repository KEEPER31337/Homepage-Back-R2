package com.keeper.homepage.global.restdocs

import org.springframework.http.MediaType
import java.lang.reflect.Field
import kotlin.reflect.jvm.internal.impl.descriptors.FieldDescriptor

infix fun String.type(
        docsFieldType: DocsFieldType
): ResponseField {
    return ResponseField()
}


sealed class DocsFieldType(
        val type: MediaType,
)

object STRING : DocsFieldType(MediaType.APPLICATION_JSON)

fun main() {
    restDocs {
        pathVariable("memberId" means "회원의 ID 값")
        requestBody("awarderId" means "수여자의 ID")
        responseBody(
                "memberId" type STRING means "memberID" isOptional true,
                "memberType" type STRING means "memberType" isOptional true,
        )
    }
}

fun restDocs(init: RestDocs.() -> Unit): RestDocs {
    val restDocs = RestDocs()
    return restDocs
}

class RestDocs {
    private val pathVariables = mutableListOf<PathVariable>()
    private val requestFields = mutableListOf<RequestField>()
    private val responseFields = mutableListOf<ResponseField>()

    fun pathVariable(vararg fields: PathVariable) {
        pathVariables.addAll(fields)
    }

    fun requestBody(vararg fields: RequestField) {
        requestFields.addAll(fields)
    }

    fun responseBody(vararg fields: ResponseField) {
        responseFields.addAll(fields)
    }
}

class PathVariable {
    var pathName: String? = null
    var description: String? = null
}

infix fun String.means(description: String): PathVariable {
    return PathVariable().apply {
        this.pathName = this@means
        this.description = description
    }
}

class RequestField {
    private var description: String? = null
    private var isOptional: Boolean = false

    infix fun means(value: String): RequestField {
        description = value
        return this
    }

    infix fun isOptional(value: Boolean): RequestField {
        isOptional = value
        return this
    }
}

infix fun String.means(description: String): RequestField {
    return RequestField().apply {
        this. = this@means
        this.description = description
    }
}

class ResponseField {
    private var description: String? = null
    private var isOptional: Boolean = false

    infix fun means(value: String): ResponseField {
        description = value
        return this
    }

    infix fun isOptional(value: Boolean): ResponseField {
        isOptional = value
        return this
    }
}
