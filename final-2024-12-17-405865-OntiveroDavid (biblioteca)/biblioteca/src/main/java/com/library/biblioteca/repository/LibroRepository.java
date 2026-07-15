package com.library.biblioteca.repository;

import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    Libro findByIsbn(String isbn);
    List<Libro> findByEstado(EstadoLibro estado);
}