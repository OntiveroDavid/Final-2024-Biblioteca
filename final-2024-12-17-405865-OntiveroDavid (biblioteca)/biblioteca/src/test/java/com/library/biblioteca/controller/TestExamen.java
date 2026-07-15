package com.library.biblioteca.controller;

import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import com.library.biblioteca.service.Impl.BibliotecaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TestExamen {

    private BibliotecaServiceImpl bibliotecaService;

    @Mock
    private RegistroRepository registroRepository;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bibliotecaService = new BibliotecaServiceImpl(libroRepository, registroRepository, restTemplate);
    }

    @Test
    void testAlquilarLibros() {
        Libro libro = new Libro();
        libro.setIsbn("1234567890");
        libro.setEstado(EstadoLibro.DISPONIBLE);

        ClienteDTO cliente = new ClienteDTO(1L, "Juan Perez", "Calle 123");

        doReturn(libro).when(libroRepository).findByIsbn("1234567890");
        doReturn(cliente).when(restTemplate).getForObject(anyString(), eq(ClienteDTO.class));
        when(registroRepository.save(any(Registro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Registro resultado = bibliotecaService.alquilarLibros(List.of("1234567890"));

        assertNotNull(resultado);
        assertEquals(1L, resultado.getClienteId());
        assertEquals(EstadoLibro.RESERVADO, libro.getEstado());
    }

    @Test
    void testDevolverLibros() {
        Libro libro = new Libro();
        libro.setEstado(EstadoLibro.RESERVADO);
        libro.setId(1L);

        Registro registro = new Registro();
        registro.setId(1L);
        registro.setFechaReserva(LocalDate.now().minusDays(3));
        registro.setLibrosReservados(List.of(libro));

        doReturn(Optional.of(registro)).when(registroRepository).findById(1L);
        doReturn(Optional.of(libro)).when(libroRepository).findById(1L);
        doReturn(registro).when(registroRepository).save(registro);

        Registro resultado = bibliotecaService.devolverLibros(1L);

        assertNotNull(resultado);
        assertNotNull(resultado.getTotal());
        assertEquals(LocalDate.now(), resultado.getFechaDevolucion());
        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
    }

    @Test
    void testCostoAlquilerCalculation() {
        // Escenarios de prueba para diferentes duraciones de alquiler
        testCostoAlquilerEscenario(1, 1);    // 1 día - 1 libro - costo base
        testCostoAlquilerEscenario(1, 2);    // 1 día - 2 libros - costo base
        testCostoAlquilerEscenario(3, 1);    // 3 días - 1 libro - costo medio
        testCostoAlquilerEscenario(3, 2);    // 3 días - 2 libros - costo medio
        testCostoAlquilerEscenario(6, 2);    // 6 días - 2 libros - costo medio + extras
        testCostoAlquilerEscenario(6, 3);    // 6 días - 3 libros - costo medio + extras

    }

    private void testCostoAlquilerEscenario(
            int diasAlquiler,
            int cantidadLibros
    ) {
        // Preparar libros
        List<Libro> librosReservados = new ArrayList<>();
        for (int i = 0; i < cantidadLibros; i++) {
            Libro libro = new Libro();
            libro.setEstado(EstadoLibro.RESERVADO);
            librosReservados.add(libro);
        }

        // Preparar registro
        Registro registro = new Registro();
        registro.setId(1L);
        registro.setFechaReserva(LocalDate.now().minusDays(diasAlquiler));
        registro.setLibrosReservados(librosReservados);

        // Configurar mocks
        when(registroRepository.findById(1L)).thenReturn(Optional.of(registro));
        when(registroRepository.save(any(Registro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar método de devolución
        Registro resultado = bibliotecaService.devolverLibros(1L);

        // Verificaciones de costo
        assertNotNull(resultado.getTotal(), "El total no debe ser nulo");

        // Cálculo de costo base
        BigDecimal costoBase = BigDecimal.valueOf(100);
        BigDecimal costoMedio = BigDecimal.valueOf(150);
        BigDecimal costoExtra = BigDecimal.valueOf(30);

        // Calcular costo esperado según días y libros
        BigDecimal costoEsperadoBigDecimal;
        if (diasAlquiler <= 2) {
            costoEsperadoBigDecimal = costoBase.multiply(BigDecimal.valueOf(cantidadLibros));
        } else if (diasAlquiler <= 5) {
            costoEsperadoBigDecimal = costoMedio.multiply(BigDecimal.valueOf(cantidadLibros));
        } else {
            long diasExtras = diasAlquiler - 5;
            costoEsperadoBigDecimal = costoMedio
                    .add(costoExtra.multiply(BigDecimal.valueOf(diasExtras)))
                    .multiply(BigDecimal.valueOf(cantidadLibros));
        }

        assertEquals(
                costoEsperadoBigDecimal,
                resultado.getTotal(),
                "El costo calculado no coincide para " + diasAlquiler + " días y " + cantidadLibros + " libros"
        );

        assertTrue(
                resultado.getTotal().compareTo(BigDecimal.ZERO) > 0,
                "El costo debe ser mayor a cero"
        );
    }

    @Test
    void testInformeLibrosMasAlquiladosValidation() {
        // Preparar datos simulados de libros más alquilados
        List<Object[]> librosMasAlquilados = Arrays.asList(
                new Object[]{"9876543210", 5L},
                new Object[]{"1234567890", 3L},
                new Object[]{"0987654321", 1L}
        );

        // Configurar repositorio para devolver datos
        when(registroRepository.obtenerLibrosMasAlquilados()).thenReturn(librosMasAlquilados);

        // Ejecutar método de informe
        List<Object[]> resultado = bibliotecaService.informeLibrosMasAlquilados();

        // Verificaciones
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals(3, resultado.size(), "Debe devolver 3 resultados");

        // Verificar primer libro (más alquilado)
        assertEquals("9876543210", resultado.get(0)[0], "Primer ISBN debe ser correcto");
        assertEquals(5L, resultado.get(0)[1], "Número de alquileres del primer libro debe ser 5");

        // Verificar último libro (menos alquilado)
        assertEquals("0987654321", resultado.get(2)[0], "Último ISBN debe ser correcto");
        assertEquals(1L, resultado.get(2)[1], "Número de alquileres del último libro debe ser 1");
    }

    @Test
    void testInformeLibrosMasAlquiladosOrdenamiento() {
        // Preparar datos simulados
        List<Object[]> librosMasAlquilados = Arrays.asList(
                new Object[]{"LIBRO-MAS-POPULAR", 10L},
                new Object[]{"LIBRO-MEDIO", 5L},
                new Object[]{"LIBRO-MENOS-POPULAR", 1L}
        );

        // Configurar repositorio
        when(registroRepository.obtenerLibrosMasAlquilados()).thenReturn(librosMasAlquilados);

        // Ejecutar método
        List<Object[]> resultado = bibliotecaService.informeLibrosMasAlquilados();

        // Verificaciones de ordenamiento
        assertEquals(3, resultado.size(), "Debe devolver 3 resultados");

        // Verificar orden descendente por número de alquileres
        assertTrue((Long)resultado.get(0)[1] > (Long)resultado.get(1)[1],
                "Primer libro debe tener más alquileres que el segundo");

        assertTrue((Long)resultado.get(1)[1] > (Long)resultado.get(2)[1],
                "Segundo libro debe tener más alquileres que el tercero");
    }


}
