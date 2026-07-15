package com.library.biblioteca.service.Impl;

import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.service.LibroService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;

    public LibroServiceImpl(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public Libro registrarLibro(Libro libro) {
        //TODO
        /**
         * Completar el metodo de registro
         * el estado inicial del libro debe ser DISPONIBLE
         */
        libro.setEstado(EstadoLibro.DISPONIBLE);
        libro.setTitulo(libro.getTitulo());
        libro.setIsbn(libro.getIsbn());
        libro.setAutor(libro.getAutor());

        return libroRepository.save(libro);
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        //TODO
        /**
         * Completar el metodo 
         */
        return libroRepository.findAll();
    }

    @Override
    public void eliminarLibro(Long id) {
        //TODO
        /**
         * Completar el metodo
         */
        libroRepository.deleteById(id);
    }

    @Override
    public Libro actualizarLibro(Libro libro) {
        //TODO
        /**
         * Completar el metodo
         */
        return libroRepository.save(libro);
    }
}
