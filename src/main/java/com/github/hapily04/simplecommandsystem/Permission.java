package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.*;

/**
 * Provides the permission node required to run the command/subcommand <br>
 * If the user doesn't have permission to run the primary command, the subcommand cannot be run. <br>
 * OPTIONAL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Permission {

    String value();

}
