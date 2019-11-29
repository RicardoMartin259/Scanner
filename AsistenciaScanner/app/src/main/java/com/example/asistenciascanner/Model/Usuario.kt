package com.example.asistenciascanner.Model

import java.io.Serializable


data class Usuario(
    var codigo: String? = null,
    var estado: String? = null,
    var password: String? = null,
    var tipo: String? = null,
    var observaciones: String? = null,
    var celular: String? = null,
    var inasistencias: Long? = null,
    var nivel: String? = null,
    var nombre: String? = null,
    var foto: String? = null
): Serializable
