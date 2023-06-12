# SimpleCommandSystem
Create commands just a little more easily... (Java 8+)

# Features
- SubCommands (with aliases handled & dedicated permission message handling)
- Easier command executor handling
- Tab completions registered automatically for all subcommands (and optionally their aliases)
- Well documented
- Easier registration (it's pretty much all done for you)
```java
getCommand("fly").setExecutor(new FlyCommand());
getCommand("level").setExecutor(new LevelCommand());
getCommand("shard").setExecutor(new ShardCommand());
getCommand("money").setExecutor(new MoneyCommand());
```
->
```java
// Paper
SimpleCommandSystem.registerCommands(this, Bukkit.getCommandMap(), "com.github.hapily04.myserver");
```

# Getting Started
## Adding the project as a dependency
### Maven
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
```xml
	<dependency>
	    <groupId>com.github.hapily04</groupId>
	    <artifactId>SimpleCommandSystem</artifactId>
	    <version>1.2</version>
	</dependency>
```
### Gradle
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```groovy
	dependencies {
	        implementation 'com.github.hapily04:SimpleCommandSystem:1.0'
	}
```
## Making your first command
Let's start with a simple spawn command
### Base Command
```java
@Name("Spawn")
@Description("Teleports you to the spawnpoint of the world")
@Permission("myplugin.spawn")
@Aliases({"s", "sp"})
public class CmdSpawn extends CommandElement<Player> { // CommandElement<Player> because we only want players to execute the command

    @Override
    public void execute(Player sender, String[] args) {
        if (args.length > 0) { // This method is only run if there are no matching subcommand arguments
            String usage = getUsage();
            assert usage != null;
            sender.sendMessage(getUsage());
            return;
        }
        Location spawnLocation = sender.getWorld().getSpawnLocation();
        sender.teleport(spawnLocation);
        sender.sendMessage(ChatColor.GOLD + "You have been teleported to the spawn location in your world!");
    }

    @Override
    public String getUsage() {
        return "/spawn [set]";
    }

    @Override
    public @NotNull Class<? extends Player> getExecutableBy() {
        return Player.class;
    }

}
```
### SubCommand
```java
@Name("Set")
@Description("Sets the spawn-point of the world")
@Permission("myplugin.spawn.set")
@SubCommand(parent = CmdSpawn.class)
@Aliases(value = "sethere", registerAsTabCompletions = true)
public class SubSet extends CommandElement<Player> { // CommandElement<Player> because we only want players to execute the command
    
    @Override
    public void execute(Player sender, String[] args) {
        if (args.length > 0) { // This method is only run if there are no matching subcommand arguments for this subcommand
            // You could choose to ignore the extra arguments
            String usage = getUsage();
            assert usage != null;
            sender.sendMessage(getUsage());
            return;
        }
        Location playerLocation = sender.getLocation();
        World playerWorld = playerLocation.getWorld();
        playerWorld.setSpawnLocation(playerLocation);
        int x = (int) playerLocation.getX();
        int y = (int) playerLocation.getY();
        int z = (int) playerLocation.getZ();
        sender.sendMessage(ChatColor.GOLD + "You have successfully set the spawn location of your world to " +
                "x: " + x + " y: " + y + " z: " + z);
    }

    @Override
    public String getUsage() {
        return "/spawn set";
    }

    @Override
    public @NotNull Class<? extends Player> getExecutableBy() {
        return Player.class;
    } 
    
}
```
## Adding the registerCommands method call to your onEnable
### Paper
```java
@Override
public void onEnable() {
	SimpleCommandSystem.registerCommands(this, Bukkit.getCommandMap(), "mypackage");
}
```
### Spigot
```java
@Override
public void onEnable() {
	SimpleCommandSystem.registerCommands(this, getCommandMap(), "mypackage");
}

private CommandMap getCommandMap() {
	CommandMap commandMap = null;
	try {
		if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
			Field field = SimplePluginManager.class.getDeclaredField("commandMap");
			field.setAccessible(true);
			commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
		}
	} 
	catch (Exception e) {
		e.printStackTrace();
	}
	return commandMap;
}
```
