package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the name of the command element (not case-sensitive)
 * REQUIRED (NullPointerException is thrown if this annotation is missing from the class implementing CommandElement)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Name {

    String value();

}
