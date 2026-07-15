package com.library.clientes.controller;

import com.library.clientes.model.Persona;
import com.library.clientes.service.PersonaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @PostMapping
    public Persona crearPersona(@RequestBody Persona persona) {
        return personaService.crearPersona(persona);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPersona(@PathVariable Long id) {
        return personaService.obtenerPersonaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/aleatorio")
    public ResponseEntity<Persona> obtenerPersonaAlAzar() {
        return ResponseEntity.ok(personaService.obtenerPersonaAlAzar());
    }

    @GetMapping
    public List<Persona> obtenerTodasLasPersonas() {
        return personaService.obtenerTodasLasPersonas();
    }

    @PutMapping("/{id}")
    public Persona actualizarPersona(@PathVariable Long id, @RequestBody Persona persona) {
        return personaService.actualizarPersona(id, persona);
    }

    @DeleteMapping("/{id}")
    public void eliminarPersona(@PathVariable Long id) {
        personaService.eliminarPersona(id);
    }
}