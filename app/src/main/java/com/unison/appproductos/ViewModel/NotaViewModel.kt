package com.unison.appproductos.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unison.appproductos.Models.Categoria
import com.unison.appproductos.Models.Nota
import com.unison.appproductos.room.NotaRepositorio
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EstadoUiNota(
    val notas: List<Nota> = emptyList(),
    val mensaje: String? = null,
    val categorias: List<Categoria> = emptyList()
)

class NotaViewModel(private val repositorio: NotaRepositorio) : ViewModel() {

    private val _estadoUi = MutableStateFlow(EstadoUiNota())
    val estadoUi: StateFlow<EstadoUiNota> = _estadoUi.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    private val _categoriaSeleccionada = MutableStateFlow<Categoria?>(null)
    val categoriaSeleccionada: StateFlow<Categoria?> = _categoriaSeleccionada.asStateFlow()

    init {
        // Combinar los flujos de notas, categorías, búsqueda y categoría seleccionada
        viewModelScope.launch {
            combine(
                repositorio.todasLasNotas,
                repositorio.todasLasCategorias,
                _textoBusqueda,
                _categoriaSeleccionada
            ) { notas, categorias, query, categoria ->
                val notasFiltradas = notas.filter {
                    (it.titulo.contains(query, ignoreCase = true) || it.contenido.contains(query, ignoreCase = true)) &&
                            (categoria == null || it.categoriaId == categoria.id)
                }
                _estadoUi.update { it.copy(notas = notasFiltradas, categorias = categorias) }
            }.collect()
        }
    }

    // Funciones de Notas
    fun agregarNota(nota: Nota) {
        viewModelScope.launch {
            repositorio.insertar(nota)
            setMensaje("Nota agregada")
        }
    }

    fun actualizarNota(nota: Nota) {
        viewModelScope.launch {
            repositorio.actualizar(nota)
            setMensaje("Nota actualizada")
        }
    }

    fun eliminarNota(nota: Nota) {
        viewModelScope.launch {
            repositorio.eliminar(nota)
            setMensaje("Nota eliminada")
        }
    }

    fun setMensaje(mensaje: String) {
        _estadoUi.update { it.copy(mensaje = mensaje) }
    }

    fun clearMensaje() {
        _estadoUi.update { it.copy(mensaje = null) }
    }

    fun buscarNotas(query: String) {
        _textoBusqueda.value = query
    }

    // Funciones de Categorías
    fun agregarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            repositorio.insertarCategoria(categoria)
            setMensaje("Categoría agregada")
            // Emitir manualmente un nuevo valor para refrescar la UI
            _estadoUi.update {
                it.copy(categorias = repositorio.todasLasCategorias.first()) // obtener las categorías actualizadas
            }
        }
    }




    fun eliminarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            repositorio.eliminarCategoria(categoria)
            setMensaje("Categoría eliminada")
        }
    }

    fun seleccionarCategoria(categoria: Categoria?) {
        _categoriaSeleccionada.value = categoria
    }
}

class NotaViewModelFactory(private val repositorio: NotaRepositorio) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotaViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
