package com.library.clientes;

import org.junit.jupiter.api.Test;
import com.library.clientes.model.Persona;
import com.library.clientes.controller.PersonaController;
import com.library.clientes.service.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ClientesApplicationTests {

	@Mock
	private PersonaService personaService;

	@InjectMocks
	private PersonaController personaController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCrearPersona() {
		Persona persona = new Persona(null, "Juan", "Calle Falsa 123");
		when(personaService.crearPersona(persona)).thenReturn(new Persona(1L, "Juan", "Calle Falsa 123"));

		Persona resultado = personaController.crearPersona(persona);

		assertNotNull(resultado.getId());
		assertEquals("Juan", resultado.getNombre());
	}

	@Test
	void testObtenerPersona() {
		Persona persona = new Persona(1L, "Juan", "Calle Falsa 123");
		when(personaService.obtenerPersonaPorId(1L)).thenReturn(Optional.of(persona));

		ResponseEntity<Persona> resultado = personaController.obtenerPersona(1L);

		assertTrue(resultado.getStatusCode().is2xxSuccessful());
		assertEquals(persona, resultado.getBody());
	}

	@Test
	void testObtenerPersonaAlAzar() {
		Persona persona = new Persona(1L, "Juan", "Calle Falsa 123");
		when(personaService.obtenerPersonaAlAzar()).thenReturn(persona);

		ResponseEntity<Persona> resultado = personaController.obtenerPersonaAlAzar();

		assertTrue(resultado.getStatusCode().is2xxSuccessful());
		assertEquals(persona, resultado.getBody());
	}

	@Test
	void testObtenerTodasLasPersonas() {
		List<Persona> personas = Arrays.asList(
				new Persona(1L, "Juan", "Calle 1"),
				new Persona(2L, "María", "Calle 2")
		);
		when(personaService.obtenerTodasLasPersonas()).thenReturn(personas);

		List<Persona> resultado = personaController.obtenerTodasLasPersonas();

		assertEquals(2, resultado.size());
	}

	@Test
	void testActualizarPersona() {
		Persona personaExistente = new Persona(1L, "Juan", "Calle Vieja");
		Persona personaActualizada = new Persona(1L, "Juan Pérez", "Calle Nueva 123");

		when(personaService.actualizarPersona(1L, personaActualizada)).thenReturn(personaActualizada);

		Persona resultado = personaController.actualizarPersona(1L, personaActualizada);

		assertEquals(personaActualizada.getNombre(), resultado.getNombre());
		assertEquals(personaActualizada.getDomicilio(), resultado.getDomicilio());
	}

	@Test
	void testEliminarPersona() {
		Long idPersona = 1L;

		// No esperamos ningún valor de retorno, solo verificamos que no lance excepción
		doNothing().when(personaService).eliminarPersona(idPersona);

		// Ejecutamos el método de eliminación
		assertDoesNotThrow(() -> personaController.eliminarPersona(idPersona));

		// Verificamos que el método del servicio fue llamado con el ID correcto
		verify(personaService, times(1)).eliminarPersona(idPersona);
	}

	@Test
	void testObtenerPersonaNoEncontrada() {
		when(personaService.obtenerPersonaPorId(999L)).thenReturn(Optional.empty());

		ResponseEntity<Persona> resultado = personaController.obtenerPersona(999L);

		assertTrue(resultado.getStatusCode().is4xxClientError());
	}



}
