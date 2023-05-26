package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.*;

/**
 * Provides the list of aliases to be detected for the command <br>
 * OPTIONAL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Aliases {

    String[] value();

}
