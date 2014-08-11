package org.cru.cdi;

import org.cru.qualifiers.Oaf;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Created by William.Randall on 8/11/2014.
 */
public class EntityManagerProducer
{
    @PersistenceContext(unitName = "Oaf")
    @Produces
    @Oaf
    EntityManager oafEntityManager;

    @PersistenceUnit(unitName = "Oaf")
    @Produces
    @Oaf
    EntityManagerFactory oafEntityManagerFactory;
}
