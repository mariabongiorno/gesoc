package com.testigos.gesoc.persistence;

import java.util.List;

import javax.persistence.Query;

import com.testigos.gesoc.model.domain.egresos.Egreso;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

@Repository
public class DAOEgreso extends DAO<Egreso> {

    public DAOEgreso() {
        super(Egreso.class);
    }

    public void updateIngreso(Egreso e) {
        createEntityManager();
        beginTransaction();
        Query q = em.createQuery("UPDATE Egreso e set e.ingresoAsociado = :i where e.id = :id");
        q.setParameter("i", e.getIngresoAsociado());
        q.setParameter("id", e.getId());
        q.executeUpdate();
        commit();
        close();
    }

    public List<Egreso> findAllConProveedor() {
        createEntityManager();
        beginTransaction();
        List<Egreso> tList = createQuery("From " + type.getSimpleName()).getResultList();
        if (tList != null)
            tList.size();
        for (Egreso e : tList)
            Hibernate.initialize(e.getVendedor());
        commit();
        close();
        return tList;
    }

    public List<Egreso> findAllConItems() {
        createEntityManager();
        beginTransaction();
        List<Egreso> tList = createQuery("From " + type.getSimpleName()).getResultList();
        if (tList != null)
            tList.size();

        for (Egreso e : tList)
            Hibernate.initialize(e.getItems());
        commit();
        close();
        return tList;
    }

    public List<Egreso> findAllConProveedoreItems() {
        createEntityManager();
        beginTransaction();
        List<Egreso> tList = createQuery("From " + type.getSimpleName()).getResultList();
        if (tList != null)
            tList.size();
        for (Egreso e : tList) {
            Hibernate.initialize(e.getItems());
            Hibernate.initialize(e.getVendedor());
        }
        commit();
        close();
        return tList;
    }

    public List<Egreso> findAllConIngreso() {
        createEntityManager();
        beginTransaction();
        List<Egreso> tList = createQuery("From " + type.getSimpleName()).getResultList();
        if (tList != null)
            tList.size();
        for (Egreso e : tList) {
            Hibernate.initialize(e.getItems());
            Hibernate.initialize(e.getVendedor());
            Hibernate.initialize(e.getIngresoAsociado());
        }
        commit();
        close();
        return tList;
    }

    public List<Egreso> findAllConDocumentoComercial() {
        createEntityManager();
        beginTransaction();
        List<Egreso> tList = createQuery("From " + type.getSimpleName()).getResultList();
        if (tList != null)
            tList.size();
        for (Egreso e : tList)
            Hibernate.initialize(e.getDocumento());
        commit();
        close();
        return tList;
    }

}
