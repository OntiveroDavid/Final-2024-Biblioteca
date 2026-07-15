package com.library.biblioteca.service.Impl;

import com.library.biblioteca.model.Libro;
import com.library.biblioteca.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibroServiceImplTest {
    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroServiceImpl libroService;

    Libro libro;
    List<Libro> libros;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        libro = new Libro();
        libros = List.of(libro);
    }

    @Test
    void registrarLibro() {
        doReturn(libro).when(libroRepository).save(libro);

        Libro resultado = libroService.registrarLibro(libro);

        assertNotNull(resultado);
        assertEquals(libro, resultado);
    }

    @Test
    void obtenerTodosLosLibros() {
        doReturn(libros).when(libroRepository).findAll();

        List<Libro> resultado = libroService.obtenerTodosLosLibros();

        assertNotNull(resultado);
        assertEquals(libros, resultado);
    }

    @Test
    void eliminarLibro() {
        Long libroId = 1L;

        // Llamamos al método
        libroService.eliminarLibro(libroId);

        // Verificamos que se haya llamado a deleteById con el ID correcto
        verify(libroRepository, times(1)).deleteById(libroId);
    }

    @Test
    void actualizarLibro() {
        libroService.actualizarLibro(libro);

        verify(libroRepository).save(libro);
    }
}