package com.library.clientes.service;

import com.library.clientes.model.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaService {
    public Persona crearPersona(Persona persona);
    public Optional<Persona> obtenerPersonaPorId(Long id);
    public Persona obtenerPersonaAlAzar();
    public List<Persona> obtenerTodasLasPersonas();
    public Persona actualizarPersona(Long id, Persona personaActualizada);
    public void eliminarPersona(Long id);
}