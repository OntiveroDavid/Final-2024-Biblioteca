package com.library.biblioteca.model;

import com.library.biblioteca.enums.EstadoLibro;
import jakarta.persistence.*;


@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String isbn;
    private String titulo;
    private String autor;

    @Enumerated(EnumType.STRING)
    private EstadoLibro estado;

    public Libro(Long id, String isbn, String titulo, String autor, EstadoLibro estado) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = estado;
    }

    public Libro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public EstadoLibro getEstado() {
        return estado;
    }

    public void setEstado(EstadoLibro estado) {
        this.estado = estado;
    }
}
