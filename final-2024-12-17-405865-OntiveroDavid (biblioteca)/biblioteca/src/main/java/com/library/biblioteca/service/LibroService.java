package com.library.biblioteca.service;

import com.library.biblioteca.model.Libro;

import java.util.List;

public interface LibroService {
    public Libro registrarLibro(Libro libro);
    public List<Libro>obtenerTodosLosLibros();
    public void eliminarLibro(Long id);
    public Libro actualizarLibro(Libro libro);
}
