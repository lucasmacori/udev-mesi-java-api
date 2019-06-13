package com.udev.mesi;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DAO {
	
	private static SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
