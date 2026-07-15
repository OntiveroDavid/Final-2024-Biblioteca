package com.library.biblioteca.service.Impl;

import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BibliotecaServiceImplTest {
    @Mock
    RegistroRepository registroRepository;
    @Mock
    LibroRepository libroRepository;
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    BibliotecaServiceImpl bibliotecaService;

    Registro registro;
    Libro libro;
    List<Libro> libros;
    List<Libro> librosAAlquilar;
    List<String> isbns;
    ClienteDTO cliente;
    LocalDate fechaReserva;
    LocalDate fechaDevolucion;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        registro = new Registro();
        registro.setClienteId(1L);
        libro = new Libro();
        libro.setId(1L);
        libros = List.of(libro);
        cliente = new ClienteDTO(); //esta bien?
        registro.setLibrosReservados(List.of(libro));
        registro.setFechaReserva(LocalDate.now().minusDays(5));
        fechaReserva = LocalDate.now().minusDays(7);
    }

    @Test
    void alquilarLibros() {
        List<String> isbns = List.of("12345");
        Libro libro = new Libro(1L, "12345", "Libro Test", "Autor", EstadoLibro.DISPONIBLE);
        ClienteDTO clienteMock = new ClienteDTO(1L, "Test", "Cliente");

        doReturn(libro).when(libroRepository).findByIsbn("12345");
        doReturn(List.of(libro)).when(libroRepository).saveAll(anyList());
        doReturn(clienteMock).when(restTemplate).getForObject(anyString(), eq(ClienteDTO.class));
        doReturn(registro).when(registroRepository).save(any());

        Registro resultado = bibliotecaService.alquilarLibros(isbns);

        assertNotNull(resultado);
        assertEquals(1, resultado.getLibrosReservados().size());
        assertEquals(clienteMock.getId(), resultado.getClienteId());
        assertEquals(EstadoLibro.RESERVADO, resultado.getLibrosReservados().get(0).getEstado());
    }

    @Test
    void devolverLibros() {
        doReturn(Optional.of(registro)).when(registroRepository).findById(1L);
        doReturn(Optional.of(libro)).when(libroRepository).findById(1L);
        doReturn(registro).when(registroRepository).save(any());

        Registro resultado = bibliotecaService.devolverLibros(1L);

        assertNotNull(resultado);
        assertEquals(registro, resultado);
    }

    @Test
    void verTodosLosAlquileres() {
        doReturn(List.of(registro)).when(registroRepository).findAll();

        List<Registro> resultado = bibliotecaService.verTodosLosAlquileres();

        assertNotNull(resultado);
        assertEquals(List.of(registro), resultado);
    }

    @Test
    void informeSemanal() {
        doReturn(List.of(registro)).when(registroRepository).obtenerRegistrosSemana(any(LocalDate.class), any(LocalDate.class));

        List<Registro> resultado = bibliotecaService.informeSemanal(fechaReserva);

        assertNotNull(resultado);
        assertEquals(List.of(registro), resultado);
    }

    @Test
    void informeLibrosMasAlquilados() {
        // Simular un resultado como el que devolvería el repositorio
        Object[] libro1 = new Object[] { "El Señor de los Anillos", 15L };
        Object[] libro2 = new Object[] { "1984", 10L };
        List<Object[]> mockResultado = List.of(libro1, libro2);

        // Configurar mock del repositorio
        Mockito.doReturn(mockResultado).when(registroRepository).obtenerLibrosMasAlquilados();

        // Ejecutar método del servicio
        List<Object[]> resultado = bibliotecaService.informeLibrosMasAlquilados();

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(mockResultado, resultado);
    }
}