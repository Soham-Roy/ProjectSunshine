package com.example.android.projectsunshine.model

interface EntityMapper <Entity, Model> {

    fun mapToModel(entity: Entity) : Model

}