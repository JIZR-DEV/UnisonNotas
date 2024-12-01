package com.unison.appproductos.room

import androidx.paging.PagingSource
import androidx.room.*
import com.unison.appproductos.Models.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Query("SELECT * FROM notas ORDER BY id DESC")
    fun obtenerTodasLasNotas(): Flow<List<Nota>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNota(nota: Nota): Long

    @Update
    suspend fun actualizarNota(nota: Nota)

    @Delete
    suspend fun eliminarNota(nota: Nota)

    @Query("""
        SELECT * FROM notas 
        WHERE (titulo LIKE '%' || :query || '%' OR contenido LIKE '%' || :query || '%')
        AND (:categoriaId IS NULL OR categoriaId = :categoriaId)
        ORDER BY id DESC
    """)
    fun buscarNotas(query: String, categoriaId: Int?): Flow<List<Nota>>

    @Query("SELECT * FROM notas ORDER BY id DESC")
    fun obtenerNotasPaginadas(): PagingSource<Int, Nota>
}
