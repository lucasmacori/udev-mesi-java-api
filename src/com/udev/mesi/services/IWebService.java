package com.udev.mesi.services;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface IWebService {
    static Object create() {
        throw new NotImplementedException();
    }

    static Object read() {
        throw new NotImplementedException();
    }

    static Object readOne() {
        throw new NotImplementedException();
    }

    static Object update() {
        throw new NotImplementedException();
    }

    static Object delete() {
        throw new NotImplementedException();
    }

    static Object exists(Object pk) {
        throw new NotImplementedException();
    }
}
