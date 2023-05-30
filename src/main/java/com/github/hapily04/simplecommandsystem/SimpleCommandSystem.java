package com.github.hapily04.simplecommandsystem;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Primary class of this Simple Command System to connect the api to the application
 */
public class SimpleCommandSystem {

    /**
     * The main method you call to register commands
     *
     * @param plugin The owning plugin contains the commands that are to be registered
     * @param commandMap The owning commandmap to register the commands on
     * @param pkg The package in which the command classes can be found under
     */
    public static void registerCommands(JavaPlugin plugin, CommandMap commandMap, String pkg) {
        try {
            Method fileMethod = plugin.getClass().getDeclaredMethod("getFile");
            fileMethod.setAccessible(true);
            File jar = (File) fileMethod.invoke(plugin);
            Class<? extends CommandElement<?>>[] classes = getClasses(pkg,
                    CommandElement.class, jar);
            Map<Class<? extends CommandElement<?>>, CommandElement<?>> commandInstances = new HashMap<>(classes.length);
            for (Class<? extends CommandElement<?>> clazz : classes) {
                if (Modifier.isAbstract(clazz.getModifiers())) continue;
                registerCommand(clazz, commandInstances, commandMap, plugin.getName());
            }
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static CommandElement<?> registerCommand(Class<? extends CommandElement<?>> clazz,
                                                     Map<Class<? extends CommandElement<?>>, CommandElement<?>> commandInstances,
                                                     CommandMap commandMap, String label)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        CommandElement<?> commandElement = clazz.getDeclaredConstructor().newInstance();
        commandInstances.put(clazz, commandElement);
        Class<? extends CommandElement<?>> parent = commandElement.getParent();
        if (parent == null) {
            commandMap.register(label.toLowerCase(Locale.ENGLISH), commandElement.asBukkitCommand());
            return commandElement;
        }
        if (commandInstances.containsKey(parent)) {
            commandInstances.get(parent).addSubCommand(commandElement);
            return commandElement;
        }
        CommandElement<?> parentElement = registerCommand(parent, commandInstances, commandMap, label);
        parentElement.addSubCommand(commandElement);
        return commandElement;
    }

    // The clazz parameter is a wildcard instead of Class<? extends T> because an incompatible types compilation error is thrown
    // when it's a class with a generic.
    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private static <T> Class<? extends T>[] getClasses(String pkg, Class<?> clazz, File jar) throws IOException, ClassNotFoundException {
        List<Class<? extends T>> classes = new ArrayList<>();
        pkg = pkg.replace('.', '/');
        try (JarFile file = new JarFile(jar)) {
            for (Enumeration<JarEntry> enu = file.entries(); enu.hasMoreElements();) {
                JarEntry jarEntry = enu.nextElement();
                String path = jarEntry.getName();
                if (!jarEntry.isDirectory() && path.startsWith(pkg) && path.endsWith(".class")) {
                    path = path.substring(0, path.length()-6); // 6 is the length of the ".class" string
                    path = path.replace('/', '.');
                    Class<?> clz = Class.forName(path);
                    if (clazz.isAssignableFrom(clz)) {
                        classes.add((Class<? extends T>) clz);
                    }
                }
            }
        }
        return (Class<? extends T>[]) classes.toArray(new Class<?>[0]);
    }

}
