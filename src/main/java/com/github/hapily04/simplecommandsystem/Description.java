package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the description of the command element
 * See {@link CommandElement#getDescription()} for more details
 * REQUIRED (NullPointerException is thrown if this annotation is missing from the class implementing CommandElement)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Description {

    String value();

}
