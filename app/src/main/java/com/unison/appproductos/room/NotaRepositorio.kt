package com.unison.appproductos.room

import com.unison.appproductos.Models.Categoria
import com.unison.appproductos.Models.Nota
import kotlinx.coroutines.flow.Flow

class NotaRepositorio(private val notaDao: NotaDao, private val categoriaDao: CategoriaDao) {

    val todasLasNotas: Flow<List<Nota>> = notaDao.obtenerTodasLasNotas()

    suspend fun insertar(nota: Nota) = notaDao.insertarNota(nota)

    suspend fun actualizar(nota: Nota) = notaDao.actualizarNota(nota)

    suspend fun eliminar(nota: Nota) = notaDao.eliminarNota(nota)

    fun buscarNotas(query: String, categoriaId: Int?): Flow<List<Nota>> = notaDao.buscarNotas(query, categoriaId)

    fun obtenerNotasPaginadas() = notaDao.obtenerNotasPaginadas()

    // Categorías
    val todasLasCategorias: Flow<List<Categoria>> = categoriaDao.obtenerTodasLasCategorias()

    suspend fun insertarCategoria(categoria: Categoria) = categoriaDao.insertarCategoria(categoria)

    suspend fun actualizarCategoria(categoria: Categoria) = categoriaDao.actualizarCategoria(categoria)

    suspend fun eliminarCategoria(categoria: Categoria) = categoriaDao.eliminarCategoria(categoria)
}
