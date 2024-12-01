package com.unison.appproductos.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unison.appproductos.Models.Nota
import com.unison.appproductos.Models.Categoria

@Database(entities = [Nota::class, Categoria::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notaDao(): NotaDao
    abstract fun categoriaDao(): CategoriaDao
}
