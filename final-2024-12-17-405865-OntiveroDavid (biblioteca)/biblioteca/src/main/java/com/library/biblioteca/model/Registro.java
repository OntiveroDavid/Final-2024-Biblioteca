package com.library.biblioteca.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "registros")
public class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private String nombreCliente;

    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Libro> librosReservados;

    @Column(name = "total_alquiler")
    private BigDecimal total;

    public Registro(Long id, Long clienteId, String nombreCliente, LocalDate fechaReserva, LocalDate fechaDevolucion, List<Libro> librosReservados, BigDecimal total) {
        this.id = id;
        this.clienteId = clienteId;
        this.nombreCliente = nombreCliente;
        this.fechaReserva = fechaReserva;
        this.fechaDevolucion = fechaDevolucion;
        this.librosReservados = librosReservados;
        this.total = total;
    }

    public Registro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public List<Libro> getLibrosReservados() {
        return librosReservados;
    }

    public void setLibrosReservados(List<Libro> librosReservados) {
        this.librosReservados = librosReservados;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
