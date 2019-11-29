package com.example.asistenciascanner

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.asistenciascanner.Model.Reserva
import com.example.asistenciascanner.Model.Turno
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val refReserva = db.collection("reserva")
    val refTurno= db.collection("turno")


    var hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    var fecha = ""
    var calendar = Calendar.getInstance()
    var formatDate = SimpleDateFormat("dd/MM/yyyy")
    var format = SimpleDateFormat("HH:mm")
    var hora = format.format(calendar.time).toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var but_aisitencias = findViewById<Button>(R.id.btn_colocar_asistencia)
        but_aisitencias.setOnClickListener { IniciarScanner() }

        var but_dia = findViewById<Button>(R.id.btn_seleccionar_dia)
        but_dia.setOnClickListener {seleccionarDia()}
    }

    private fun seleccionarDia(){
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val dayPicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
            view, year, month, dayOfMonth ->

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            fecha = "Fecha: " + formatDate.format(calendar.time)

            selector_dia.text = fecha

        }, year, month, dayOfMonth)

        dayPicker.show()
    }

    private fun IniciarScanner(){
        val scanner = IntentIntegrator(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(false)
        scanner.initiateScan()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
            if(result != null){
                if(result.contents == null){
                    Toast.makeText(this,"Cancelado", Toast.LENGTH_LONG).show()
                }else{
                    ColoarAsistencia(result.contents)
                }
            }else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    fun guardarAsistencia(reserva: Reserva, codigo: String){

        val query = refTurno.document(reserva.codTurno!!)
        query.get()
            .addOnSuccessListener { document ->
                val turno = document.toObject(Turno::class.java)
                Toast.makeText(this,"hola", Toast.LENGTH_LONG).show()
                if (turno == null){
                    Toast.makeText(this,"Turno expirado", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Turno: " +turno.fecha, Toast.LENGTH_LONG).show()

                    if (turno.fecha == hoy){

                        var horaInicio= turno.horaInicio?.split(":")?.get(0)?.toInt()
                        var minutoInicio = turno.horaInicio?.split(":")?.get(1)?.toInt()
                        var horaFin = turno.horaFin?.split(":")?.get(0)?.toInt()
                        var minutoFin = turno.horaFin?.split(":")?.get(1)?.toInt()

                        Toast.makeText(this, horaInicio.toString() + horaFin.toString(), Toast.LENGTH_SHORT).show()

                        if( hora.split(":")[0].toInt() == horaInicio!!){

                            if( hora.split(":")[1].toInt() >= minutoInicio!!){

                                if (hora.split(":")[0].toInt() <= minutoInicio!! + 15!!){
                                    reserva.estado = "Asistido"
                                }else{
                                    reserva.estado = "Tardanza"
                                }
                                val db = FirebaseFirestore.getInstance()
                                db.collection("reserva").document(codigo).set(reserva)
                                Toast.makeText(this, "Asistencia registrada", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(this, "Aun no empieza el turno", Toast.LENGTH_SHORT).show()
                            }


                        } else if (hora.split(":")[0].toInt() == horaFin!!){

                            if( hora.split(":")[1].toInt() <= minutoFin!!){

                                reserva.estado = "Tardanza"

                                val db = FirebaseFirestore.getInstance()
                                db.collection("reserva").document(codigo).set(reserva)
                                Toast.makeText(this, "Asistencia registrada", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(this, "El turno ya finalizó", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Fuera de turno", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Toast.makeText(this,"La reserva no es hoy", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"No turno", Toast.LENGTH_LONG).show()
            }
    }

    private fun ColoarAsistencia(codigo: String){

        val query = refReserva.document(codigo)
        query.get()
            .addOnSuccessListener { document ->
                val reserva = document.toObject(Reserva::class.java)
                if (reserva == null){
                    Toast.makeText(this,"Reserva expirada", Toast.LENGTH_LONG).show()
                } else {
                    guardarAsistencia(reserva,codigo)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"No reserva", Toast.LENGTH_LONG).show()
            }
    }
}
