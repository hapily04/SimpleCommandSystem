package com.github.hapily04.simplecommandsystem;

import java.lang.annotation.*;

/**
 * Marks the CommandElement as a subcommand and requires a parent command <br>
 * The parent command can be either another subcommand or the base command. <br>
 * However, the subcommands must all link back to a base command in order for the system to work properly. <br>
 * If this annotation is missing from the class implementing CommandElement, it will be treated as a base command <br>
 * OPTIONAL
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface SubCommand {

    /**
     * @return The owning command of this subcommand (can be another subcommand)
     */
    Class<? extends CommandElement<?>> parent();

}
