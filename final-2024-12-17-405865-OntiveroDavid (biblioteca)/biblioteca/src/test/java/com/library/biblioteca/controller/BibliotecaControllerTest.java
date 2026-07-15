package com.library.biblioteca.controller;

import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.service.BibliotecaService;
import com.library.biblioteca.service.LibroService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BibliotecaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BibliotecaService bibliotecaService;

    @Mock
    private LibroService libroService;

    private BibliotecaController bibliotecaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bibliotecaController = new BibliotecaController(bibliotecaService, libroService);
        mockMvc = MockMvcBuilders.standaloneSetup(bibliotecaController).build();
    }

    @Test
    void testRegistrarLibro() throws Exception {
        Libro libro = new Libro();
        libro.setIsbn("1234567890");
        libro.setTitulo("Test Book");

        when(libroService.registrarLibro(any(Libro.class))).thenReturn(libro);

        mockMvc.perform(post("/api/biblioteca/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"1234567890\",\"titulo\":\"Test Book\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

    @Test
    void testAlquilarLibros() throws Exception {
        Registro registro = new Registro();
        registro.setId(1L);
        registro.setClienteId(100L);

        when(bibliotecaService.alquilarLibros(anyList())).thenReturn(registro);

        mockMvc.perform(post("/api/biblioteca/alquilar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"1234567890\", \"0987654321\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testInformeSemanal() throws Exception {
        LocalDate fechaInicio = LocalDate.now();
        Registro registro = new Registro();
        registro.setFechaReserva(fechaInicio);

        when(bibliotecaService.informeSemanal(any(LocalDate.class)))
                .thenReturn(Arrays.asList(registro));

        mockMvc.perform(get("/api/biblioteca/informe-semanal")
                        .param("fechaInicio", fechaInicio.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fechaReserva").exists());
    }

    @Test
    void testInformeLibrosMasAlquilados() throws Exception {
        // Preparar datos simulados de libros más alquilados
        List<Object[]> librosMasAlquilados = Arrays.asList(
                new Object[]{"1234567890", 5L},  // ISBN, número de alquileres
                new Object[]{"0987654321", 3L}
        );

        // Configurar servicio para devolver datos de libros más alquilados
        when(bibliotecaService.informeLibrosMasAlquilados()).thenReturn(librosMasAlquilados);

        // Realizar prueba de endpoint
        mockMvc.perform(get("/api/biblioteca/libros-mas-alquilados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(result -> {
                    // Verificaciones adicionales
                    String responseContent = result.getResponse().getContentAsString();
                    Assertions.assertTrue(responseContent.contains("1234567890"), "Debe contener el primer ISBN");
                    Assertions.assertTrue(responseContent.contains("0987654321"), "Debe contener el segundo ISBN");

                    // Verificar que los números de alquiler son correctos
                    Assertions.assertTrue(responseContent.contains("5"), "Primer libro debe tener 5 alquileres");
                    Assertions.assertTrue(responseContent.contains("3"), "Segundo libro debe tener 3 alquileres");
                });
    }

    @Test
    void testInformeLibrosMasAlquiladosOrdenamiento() throws Exception {
        // Preparar datos simulados con ordenamiento por número de alquileres
        List<Object[]> librosMasAlquilados = Arrays.asList(
                new Object[]{"BEST-SELLER", 10L},   // Libro más popular
                new Object[]{"MEDIO-POPULAR", 5L},  // Libro medio
                new Object[]{"MENOS-POPULAR", 2L}   // Libro menos popular
        );

        when(bibliotecaService.informeLibrosMasAlquilados()).thenReturn(librosMasAlquilados);

        // Realizar prueba
        mockMvc.perform(get("/api/biblioteca/libros-mas-alquilados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();

                    // Verificar orden de libros (de más a menos alquilados)
                    int indexBestSeller = responseContent.indexOf("BEST-SELLER");
                    int indexMedioPopular = responseContent.indexOf("MEDIO-POPULAR");
                    int indexMenosPopular = responseContent.indexOf("MENOS-POPULAR");

                    Assertions.assertTrue(indexBestSeller < indexMedioPopular,
                            "Libro más alquilado debe aparecer primero");
                    Assertions.assertTrue(indexMedioPopular < indexMenosPopular,
                            "Libro medio debe aparecer antes que el menos popular");
                });
    }

    @Test
    void testDevolverLibrosWithCostCalculation() throws Exception {
        // Preparar un registro de alquiler con múltiples libros
        Registro registro = new Registro();
        registro.setId(1L);
        registro.setFechaReserva(LocalDate.now().minusDays(3)); // 3 días de alquiler

        // Simular varios libros para verificar cálculo de costo
        Libro libro1 = new Libro();
        Libro libro2 = new Libro();
        registro.setLibrosReservados(Arrays.asList(libro1, libro2));

        // Configurar servicio para devolver registro con costo calculado
        when(bibliotecaService.devolverLibros(anyLong())).thenAnswer(invocation -> {
            Registro registroDevuelto = registro;
            registroDevuelto.setFechaDevolucion(LocalDate.now());

            // Cálculo manual del costo esperado (similar a la lógica en el servicio)
            BigDecimal costoMedio = BigDecimal.valueOf(150);
            BigDecimal costoExtra = BigDecimal.valueOf(30);
            int cantidadLibros = 2;

            BigDecimal costoCalculado = costoMedio
                    .add(costoExtra.multiply(BigDecimal.valueOf(1))) // 1 día extra después de 2 días base
                    .multiply(BigDecimal.valueOf(cantidadLibros));

            registroDevuelto.setTotal(costoCalculado);
            return registroDevuelto;
        });

        // Realizar prueba de devolución
        mockMvc.perform(post("/api/biblioteca/devolver/{registroId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(result -> {
                    // Verificación adicional del costo
                    BigDecimal total = new BigDecimal(
                            result.getResponse().getContentAsString().split("total\":")[1].split("}")[0]
                    );

                    // Validaciones de costo
                    Assertions.assertTrue(total.compareTo(BigDecimal.ZERO) > 0, "El costo debe ser mayor a cero");
                    Assertions.assertEquals(2, total.divide(BigDecimal.valueOf(150)).intValue(),
                            "El costo debe reflejar el número de libros");
                });
    }

    @Test
    void testCostoAlquilerVariasDuraciones() {
        // Prueba de diferentes duraciones de alquiler
        String[] duraciones = {"1", "3", "6"};

        for (String diasAlquiler : duraciones) {
            // Simular alquiler con diferente duración
            Registro registro = new Registro();
            registro.setFechaReserva(LocalDate.now().minusDays(Long.parseLong(diasAlquiler)));
            registro.setLibrosReservados(Arrays.asList(new Libro(), new Libro()));

            when(bibliotecaService.devolverLibros(anyLong())).thenAnswer(invocation -> {
                Registro registroDevuelto = registro;
                registroDevuelto.setFechaDevolucion(LocalDate.now());

                // Lógica de cálculo de costo según la duración
                BigDecimal costoBase = BigDecimal.valueOf(100);
                BigDecimal costoMedio = BigDecimal.valueOf(150);
                BigDecimal costoExtra = BigDecimal.valueOf(30);
                int cantidadLibros = 2;
                long dias = Long.parseLong(diasAlquiler);

                BigDecimal costoCalculado;
                if (dias <= 2) {
                    costoCalculado = costoBase.multiply(BigDecimal.valueOf(cantidadLibros));
                } else if (dias <= 5) {
                    costoCalculado = costoMedio.multiply(BigDecimal.valueOf(cantidadLibros));
                } else {
                    long diasExtras = dias - 5;
                    costoCalculado = costoMedio
                            .add(costoExtra.multiply(BigDecimal.valueOf(diasExtras)))
                            .multiply(BigDecimal.valueOf(cantidadLibros));
                }

                registroDevuelto.setTotal(costoCalculado);
                return registroDevuelto;
            });

            // Prueba de devolución para cada duración
            try {
                mockMvc.perform(post("/api/biblioteca/devolver/{registroId}", 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.total").exists())
                        .andExpect(result -> {
                            BigDecimal total = new BigDecimal(
                                    result.getResponse().getContentAsString().split("total\":")[1].split("}")[0]
                            );
                            Assertions.assertTrue(total.compareTo(BigDecimal.ZERO) > 0,
                                    "El costo debe ser mayor a cero para " + diasAlquiler + " días");
                        });
            } catch (Exception e) {
                Assertions.fail("Error al probar devolución para " + diasAlquiler + " días: " + e.getMessage());
            }
        }
    }
}