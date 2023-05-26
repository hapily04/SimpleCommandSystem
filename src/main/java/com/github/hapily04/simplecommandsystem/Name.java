package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.*;

/**
 * Provides the name of the command element (not case-sensitive) <br>
 * REQUIRED (NullPointerException is thrown if this annotation is missing from the class implementing CommandElement)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Name {

    String value();

}
