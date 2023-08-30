package com.example.applistadecompras.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Anotación @Database para definir la base de datos y sus entidades
@Database(entities = [Compra::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun compraDao(): CompraDao // Método abstracto para obtener el DAO de Compra

    companion object {
        // Volatile asegura que la propiedad sea actualizada atómicamente
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Función para obtener una instancia de la base de datos
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Crea una instancia de la base de datos si aún no existe
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "compras.bd" // Nombre de la base de datos
                )
                    .fallbackToDestructiveMigration() // Permite la migración destructiva (eliminar y recrear la base de datos en caso de cambios)
                    .build()

                INSTANCE = instance // Asigna la instancia a la propiedad INSTANCE
                instance
            }
        }
    }
}


