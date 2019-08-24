package com.udev.mesi.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Database {
    public static final String UNIT_NAME = "udevmesi";

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIT_NAME);
    public static final EntityManager em = emf.createEntityManager();

    private static final Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
    public static final SessionFactory sessionFactory = cfg.buildSessionFactory();

}
