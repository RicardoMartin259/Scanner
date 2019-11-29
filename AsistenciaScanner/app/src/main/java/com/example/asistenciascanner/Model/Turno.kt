package com.example.asistenciascanner.Model

import java.io.Serializable

data class Turno(
    var horaInicio:String? = null,
    var horaFin:String? = null,
    var fecha : String? = null,
    var capacidadCubierta: Int? = null,
    var capacidadTotal: Int? = null,
    var estado:String? = null,
    var observaciones: String? = null,
    var profesor:Int? = null,
    var id:String? = null,
    var dia:String? = null
): Serializable