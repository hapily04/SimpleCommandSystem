package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.*;

/**
 * Provides the list of aliases to be detected for the command <br>
 * Also tells the tab completions registrar if the aliases should be added to the completions list <br>
 * OPTIONAL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Aliases {

    String[] value();
    boolean registerAsTabCompletions() default false;

}
