package com.mycompany.simple.ext;

import groovy.lang.Script;

/**
 *
 * @author Stanislav Tiurikov <stanislav.tyurikov@gmail.com>
 */
public class AnotherExt {

    public static Object simpleA(Script script, String message) {
        System.out.println("another: " + message);
        return message;
    }
}
