package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Provides the permission node required to run the command/subcommand
 * If the user doesn't have permission to run the primary command, the subcommand cannot be run.
 * OPTIONAL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Permission {

    String value();

}
