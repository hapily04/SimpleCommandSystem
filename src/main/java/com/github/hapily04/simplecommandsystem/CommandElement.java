package com.github.hapily04.simplecommandsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Base class to be implemented as a parent command or subcommand
 *
 * @param <T> Who will be able to execute the command
 */
public abstract class CommandElement<T extends CommandSender> {

    private final String name;
    private final String description;
    private final String @Nullable [] aliases;
    private final @Nullable String permission;
    private final @Nullable Class<? extends CommandElement<?>> parent;

    private final List<CommandElement<?>> subCommands;

    public CommandElement() {
        this.name = getOptional(Name.class).orElseThrow(() -> new NullPointerException("The name annotation is required to register a command.")).value();
        this.description = getOptional(Description.class).orElseThrow(() -> new NullPointerException("The description annotation is required to register a command.")).value();
        this.aliases = getOptional(Aliases.class).map(Aliases::value).orElse(null);
        this.permission = getOptional(Permission.class).map(Permission::value).orElse(null);
        this.parent = getOptional(SubCommand.class).map(SubCommand::parent).orElse(null);
        this.subCommands = new ArrayList<>();
    }

    /**
     * The method to be run when this command is executed
     *
     * @param sender Who executed the command
     * @param args The arguments provided after this part of the command (if this class is a subcommand, everything after
     *             the subcommand will be provided)
     */
    public abstract void execute(T sender, String[] args);

    /**
     * This method is abstract to encourage implementation by the user for better player experience when running the command. <br>
     * This is not an annotation to support any extra code the user wishes to run to get to the usage message (think minimessage)
     *
     * @return The usage message to provide to the player when the command is not run correctly
     */
    public abstract @Nullable String getUsage();

    /**
     * This method is required to encourage user readability. <br>
     * For base commands, the description implemented in the form of the bukkit /help command. <br>
     * For subcommands, it's hopeful that the description will be implemented in the developer's own help command for
     * the main base command.
     *
     * @return The description of this command element
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * This method can be overridden to provide a permission message of your choice to the player running the command. <br>
     * This is not an annotation to support any extra code the user wishes to run to get to the usage message (think minimessage)
     *
     * @return The permission message to provide to the player when they do not have permission to execute this command
     */
    public @NotNull String getPermissionMessage() {
        return ChatColor.RED + "You do not have permission to execute this command.";
    }

    /**
     * The parent class of this command element. Primarily used by subcommands to indicate to
     * {@link com.github.hapily04.simplecommandsystem.SimpleCommandSystem#registerCommands(JavaPlugin, CommandMap, String)}
     * what element this class belongs to. It also helps with subcommand nesting if that's what the user desires.
     *
     * @return The parent command element that this class belongs to
     */
    public final @Nullable Class<? extends CommandElement<?>> getParent() {
        return parent;
    }

    /**
     * This method is typically used internally with
     * {@link com.github.hapily04.simplecommandsystem.SimpleCommandSystem#registerCommands(JavaPlugin, CommandMap, String)}
     * but is left open in-case the user wants to write their own register methods.
     *
     * @param commandElement The subcommand to add to this command element's subcommand list
     */
    public final void addSubCommand(CommandElement<?> commandElement) {
        subCommands.add(commandElement);
    }

    /**
     * Used internally by {@link CommandElement#asBukkitCommand()} to check who is able to run the command
     *
     * @return The class of who is able to run this command
     */
    public abstract @NotNull Class<? extends T> getExecutableBy();

    /**
     * The "brains" of the system
     *
     * @return A bukkit-friendly object for the command to actually run
     */
    public final @NotNull BukkitCommand asBukkitCommand() {
        // I don't believe String#toLowerCase is required, but it's here just in-case.
        BukkitCommand command = new BukkitCommand(name.toLowerCase()) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                if (args.length > 0) {
                    for (CommandElement<?> subCommand : subCommands) {
                        if (!getAllAliases(subCommand).contains(subCommand.name.toUpperCase())) continue;
                        if (subCommand.permission == null || sender.hasPermission(subCommand.permission)) {
                            return subCommand.asBukkitCommand().execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
                        }
                        sender.sendMessage(subCommand.getPermissionMessage());
                        return true;
                    }
                }
                if (getExecutableBy().isInstance(sender)) {
                    CommandElement.this.execute(getExecutableBy().cast(sender), args);
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You are not able to run this command.");
                }
                return true;
            }

            /**
             * Used in the execute method of the BukkitCommand to determine if the subcommand needs to be run or not
             *
             * @param subCommand The subcommand to query and combine aliases and the name of the subcommand in uppercase
             * @return List of aliases and the name of the subcommand in uppercase
             */
            private List<String> getAllAliases(CommandElement<?> subCommand) {
                String[] aliases = subCommand.aliases;
                if (aliases == null) return List.of(new String[]{subCommand.name});
                List<String> allAliases = new ArrayList<>(aliases.length+1);
                allAliases.add(subCommand.name.toUpperCase());
                for (String alias : aliases) {
                    allAliases.add(alias.toUpperCase());
                }
                return allAliases;
            }
        };
        command.setPermission(permission);
        command.setPermissionMessage(getPermissionMessage());
        command.setDescription(description);
        if (aliases != null) {
            command.setAliases(Arrays.asList(aliases));
        }
        return command;
    }

    private <A extends Annotation> Optional<A> getOptional(Class<A> annotationClass) {
        return Optional.ofNullable(getClass().getAnnotation(annotationClass));
    }

}
