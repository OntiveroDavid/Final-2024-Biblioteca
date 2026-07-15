package com.library.biblioteca.controller;

import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.service.BibliotecaService;
import com.library.biblioteca.service.LibroService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
public class BibliotecaController {


    private final BibliotecaService bibliotecaService;
    private final LibroService libroService;

    public BibliotecaController(BibliotecaService bibliotecaService, LibroService libroService) {
        this.bibliotecaService = bibliotecaService;
        this.libroService = libroService;
    }

    // Endpoints para gestión de libros
    @PostMapping("/libros")
    public Libro registrarLibro(@RequestBody Libro libro) {
        return libroService.registrarLibro(libro);
    }

    @GetMapping("/libros")
    public List<Libro>verTodosLosLbros(){
        return libroService.obtenerTodosLosLibros();
    }

    @PutMapping("/libros")
    public Libro actualizarLibro(@RequestBody Libro libro) {
        return libroService.actualizarLibro(libro);
    }

    @DeleteMapping("/libros/{id}")
    public void eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
    }

    // Endpoints para alquiler y devolución
    @PostMapping("/alquilar")
    public Registro alquilarLibros(@RequestBody List<String> isbns) {
        return bibliotecaService.alquilarLibros(isbns);
    }

    @GetMapping("/alquilar")
    public List<Registro> verTodosLosAlquileres(){
        return bibliotecaService.verTodosLosAlquileres();
    }

    @PostMapping("/devolver/{registroId}")
    public Registro devolverLibros(@PathVariable Long registroId) {
        return bibliotecaService.devolverLibros(registroId);
    }

    // Endpoints para informes
    @GetMapping("/informe-semanal")
    public List<Registro> informeSemanal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio
    ) {
        return bibliotecaService.informeSemanal(fechaInicio);
    }

    @GetMapping("/libros-mas-alquilados")
    public List<Object[]> informeLibrosMasAlquilados() {
        return bibliotecaService.informeLibrosMasAlquilados();
    }
}