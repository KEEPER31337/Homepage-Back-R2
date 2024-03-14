package com.keeper.homepage.global.dsl

open class Field(
        val fieldName: String,
        val description: String?,
        val isOptional: Boolean?,
) {
    constructor(field: Field, isOptional: Boolean) : this(field.fieldName, field.description, isOptional)

}