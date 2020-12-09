package com.testigos.gesoc.persistence;

import java.util.List;

import com.testigos.gesoc.model.domain.usuarios.Usuario;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

@Repository
public class DAOUsuario extends DAO<Usuario> {

    public DAOUsuario() {
        super(Usuario.class);

    }

    public Usuario findConEntidad(String username) {
        createEntityManager();
        beginTransaction();
        Usuario t = em.find(type, username);
        if (t != null) {
            t.getClass();
            Hibernate.initialize(t.getEntidad());
        }
        commit();
        close();
        return t;
    }

    public List<Usuario> findAllConEntidad() {
        createEntityManager();
        beginTransaction();
        List<Usuario> tList = createQuery("From " + type.getSimpleName()).getResultList();
        if (tList != null)
            tList.size();
        for (Usuario i : tList)
            Hibernate.initialize(i.getEntidad());
        commit();
        close();
        return tList;
    }
}