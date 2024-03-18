package com.keeper.homepage.global.dsl.rest_docs

import com.keeper.homepage.domain.library.api.field

open class Field(
        var fieldName: String,
        val description: String?,
        val isOptional: Boolean?,
) {
    constructor(field: Field, isOptional: Boolean) : this(field.fieldName, field.description, isOptional)

    fun addContentString() {
        fieldName = "content[].$fieldName"
    }

}