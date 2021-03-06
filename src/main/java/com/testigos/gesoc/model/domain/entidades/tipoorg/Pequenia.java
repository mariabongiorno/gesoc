package com.testigos.gesoc.model.domain.entidades.tipoorg;

import javax.persistence.Entity;

import com.testigos.gesoc.model.domain.entidades.categorizador.SectoresEnum;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Pequenia extends Empresa {

    public Pequenia(String actividad, SectoresEnum sector, Integer cantPersonal, double promVentasAnuales) {
        this.actividad = actividad;
        this.sector = sector;
        this.cantPersonal = cantPersonal;
        this.promVentasAnuales = promVentasAnuales;
    }
}
