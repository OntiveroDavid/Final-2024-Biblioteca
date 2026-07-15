package com.library.biblioteca.service;

import com.library.biblioteca.model.Registro;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BibliotecaService {

    public Registro alquilarLibros(List<String> isbns);
    public Registro devolverLibros(Long registroId);
    public List<Registro>verTodosLosAlquileres();
    public List<Registro> informeSemanal(LocalDate fechaInicio);
    public List<Object[]> informeLibrosMasAlquilados();
}
