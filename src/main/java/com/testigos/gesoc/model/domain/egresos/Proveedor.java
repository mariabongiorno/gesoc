package com.testigos.gesoc.model.domain.egresos;

import javax.persistence.*;

import com.testigos.gesoc.model.domain.persistentes.EntidadPersistente;
import com.testigos.gesoc.model.domain.entidades.DireccionPostal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "proveedores")
public abstract class Proveedor extends EntidadPersistente {

    @Column
    protected @Getter String nombre;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "direccPostal_id")
    protected @Getter DireccionPostal direccPostal;
}