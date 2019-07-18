package com.udev.mesi.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Database {
    public static final String UNIT_NAME = "udevmesi";

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
    public static final EntityManager em = emf.createEntityManager();
}
