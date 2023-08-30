package com.example.applistadecompras.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Anotación @Dao para definir el Data Access Object (DAO) para Compra
@Dao
interface CompraDao {
    // Consulta para obtener todas las compras ordenadas por el estado "realizada"
    @Query("SELECT * FROM compra ORDER BY realizada")
    fun findAll(): List<Compra>

    // Consulta para contar el número total de compras
    @Query("SELECT COUNT(*) FROM compra")
    fun contar(): Int

    // Insertar una compra en la base de datos y devolver su ID
    @Insert
    fun insertar(compra: Compra): Long

    // Eliminar una compra de la base de datos
    @Delete
    fun eliminar(compra: Compra)

    // Actualizar una compra en la base de datos
    @Update
    fun actualizar(compra: Compra)
}


