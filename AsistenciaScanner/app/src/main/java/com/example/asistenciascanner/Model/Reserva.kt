package com.example.asistenciascanner.Model

import java.io.Serializable


data class Reserva(
    var codReserva:String? = "",
    var codTurno:String? = null,
    var codUsuario:String? = null,
    var fechaReserva:String? = null,
    var modalidad: String? = null,
    var estado : String? = null
): Serializable