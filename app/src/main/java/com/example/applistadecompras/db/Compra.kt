package com.example.applistadecompras.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// Definición de la entidad Compra que será utilizada en la base de datos
@Entity
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Int,            // Identificador único de la compra (generado automáticamente)
    var compra: String,     // Descripción de la compra
    var realizada: Boolean  // Indica si la compra ha sido realizada o no
)
