package com.library.biblioteca.service.Impl;

import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import com.library.biblioteca.service.BibliotecaService;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BibliotecaServiceImpl implements BibliotecaService {
    private final RegistroRepository registroRepository;
    private final LibroRepository libroRepository;
    private final RestTemplate restTemplate;

    public BibliotecaServiceImpl(LibroRepository libroRepository, RegistroRepository registroRepository,  RestTemplate restTemplate) {
        this.registroRepository = registroRepository;
        this.libroRepository = libroRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Registro alquilarLibros(List<String> isbns) {
        //TODO
        /**
         * Completar el metodo de alquiler
         * Se debe buscar la lista de libros por su codigo de isbn,
         * validar que los libros a alquilar tengan estado DISPONIBLE sino arrojar una exception
         * ya que solo se pueden alquilar libros que esten en dicho estado
         * throw new IllegalStateException("Uno o más libros ya están reservados.")
         * Recuperar un cliente desde la api externa /api/personas/aleatorio y guardar la reserva
         */

        List<Libro> libros = isbns.stream()
                .map(isbn -> {
                    Libro libro = libroRepository.findByIsbn(isbn);
                    if (libro == null || libro.getEstado() != EstadoLibro.DISPONIBLE) {
                        throw new IllegalArgumentException("Libro no disponible para alquiler: " + isbn);
                    }
                    libro.setEstado(EstadoLibro.RESERVADO);
                    return libro;
                }).toList();

        libroRepository.saveAll(libros);

        String clienteServiceUrl = "http://clientes-service:8081/api/personas/aleatorio";

        ClienteDTO cliente = restTemplate.getForObject(clienteServiceUrl, ClienteDTO.class);

        Registro registro = new Registro();
        registro.setLibrosReservados(libros);
        registro.setClienteId(cliente.getId());
        registroRepository.save(registro);

        return registro;
    }

    @Override
    public Registro devolverLibros(Long registroId) {
        //TODO
        /**
         * Completar el metodo de devolucion
         * Se debe buscar la reserva por su id,
         * actualizar la fecha de devolucion y calcular el importe a facturar,
         * actualizar el estado de los libros a DISPONIBLE
         * y guardar el registro con los datos actualizados 
         */
        Registro registro = registroRepository.findById(registroId).orElseThrow();

        LocalDate fechaDevolucion = LocalDate.now();
        BigDecimal total = calcularCostoAlquiler(registro.getFechaReserva(), fechaDevolucion, registro.getLibrosReservados().size());

        for (Libro libro : registro.getLibrosReservados()) {
            Libro libroActual = libroRepository.findById(libro.getId()).orElseThrow();
            libroActual.setEstado(EstadoLibro.DISPONIBLE);
        }

        registro.setFechaDevolucion(fechaDevolucion);
        registro.setTotal(total);
        registro.setNombreCliente(null);
        registro.setClienteId(null);
        registro.setFechaReserva(null);

        registroRepository.save(registro);

        return registro;
    }

    @Override
    public List<Registro> verTodosLosAlquileres() {
        return registroRepository.findAll();
    }

    // Cálculo de costo de alquiler
    private BigDecimal calcularCostoAlquiler(LocalDate inicio, LocalDate fin, int cantidadLibros) {
        //TODO
        /**
         * Completar el metodo de calculo
         * se calcula el importe a pagar por libro en funcion de la cantidad de dias,
         * es la diferencia entre el alquiler y la devolucion, respetando la siguiente tabla:
         * hasta 2 dias se debe pagar $100 por libro
         * desde 3 dias y hasta 5 dias se debe pagar $150 por libro
         * más de 5 dias se debe pagar $150 por libro + $30 por cada día extra
         */
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        BigDecimal costoPorLibro;

        if (dias <= 2) {
            costoPorLibro = BigDecimal.valueOf(100);
        } else if (dias <= 5) {
            costoPorLibro = BigDecimal.valueOf(150);
        } else {
            costoPorLibro = BigDecimal.valueOf(150 + (dias - 5) * 30);
        }

        return costoPorLibro.multiply(BigDecimal.valueOf(cantidadLibros));
    }

    @Override
    public List<Registro> informeSemanal(LocalDate fechaInicio) {
        //TODO
        /**
         * Completar el metodo de reporte semanal
         * se debe retornar la lista de registros de la semana tomando como referencia
         * la fecha de inicio para la busqueda
         */
        LocalDate fechaFin = LocalDate.now().plusDays(7);

        return registroRepository.obtenerRegistrosSemana(fechaInicio, fechaFin);
    }

    @Override
    public List<Object[]> informeLibrosMasAlquilados() {
        //TODO
        /**
         * Completar el metodo de reporte de libros mas alquilados
         * se debe retornar la lista de libros mas alquilados
         */ 
        return registroRepository.obtenerLibrosMasAlquilados();
    }

}
