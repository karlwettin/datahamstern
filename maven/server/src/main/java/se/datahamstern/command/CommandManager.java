package se.datahamstern.command;

import se.datahamstern.external.kolada.KoladaBefolkningsandelsPerAlderCommand;
import se.datahamstern.external.naringslivsregistret.NaringslivsregistretCommand;
import se.datahamstern.external.osm.KommungranspolygonCommand;
import se.datahamstern.external.osm.LansgranspolygonCommand;
import se.datahamstern.external.posten.postnummer.PostenPostnummerCommand;
import se.datahamstern.external.scb.MyndighetsregistretCommand;
import se.datahamstern.external.scb.TatortsArealBefolkningCommand;
import se.datahamstern.external.wikipedia.kommuner.WikipediaKommunCommand;
import se.datahamstern.external.wikipedia.lan.WikipediaLanCommand;
import se.datahamstern.external.wikipedia.orter.WikipediaTatortsCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2012-03-03 23:27
 */
public class CommandManager {

  private static CommandManager instance = new CommandManager();

  private CommandManager() {
    registerCommandClass(NaringslivsregistretCommand.class, NaringslivsregistretCommand.COMMAND_NAME, NaringslivsregistretCommand.COMMAND_VERSION);
    registerCommandClass(PostenPostnummerCommand.class, PostenPostnummerCommand.COMMAND_NAME, PostenPostnummerCommand.COMMAND_VERSION);
    registerCommandClass(WikipediaTatortsCommand.class, WikipediaTatortsCommand.COMMAND_NAME, WikipediaTatortsCommand.COMMAND_VERSION);
    registerCommandClass(WikipediaKommunCommand.class, WikipediaKommunCommand.COMMAND_NAME, WikipediaKommunCommand.COMMAND_VERSION);
    registerCommandClass(WikipediaLanCommand.class, WikipediaLanCommand.COMMAND_NAME, WikipediaLanCommand.COMMAND_VERSION);
    registerCommandClass(MyndighetsregistretCommand.class, MyndighetsregistretCommand.COMMAND_NAME, MyndighetsregistretCommand.COMMAND_VERSION);
    registerCommandClass(KoladaBefolkningsandelsPerAlderCommand.class, KoladaBefolkningsandelsPerAlderCommand.COMMAND_NAME, KoladaBefolkningsandelsPerAlderCommand.COMMAND_VERSION);
    registerCommandClass(KommungranspolygonCommand.class, KommungranspolygonCommand.COMMAND_NAME, KommungranspolygonCommand.COMMAND_VERSION);
    registerCommandClass(LansgranspolygonCommand.class, LansgranspolygonCommand.COMMAND_NAME, LansgranspolygonCommand.COMMAND_VERSION);
    registerCommandClass(TatortsArealBefolkningCommand.class, TatortsArealBefolkningCommand.COMMAND_NAME, TatortsArealBefolkningCommand.COMMAND_VERSION);
  }

  public static CommandManager getInstance() {
    return instance;
  }

  private Map<String, Map<String, Class>> commandClassesByNameAndVersion = new HashMap<String, Map<String, Class>>();

  /**
   * @param commandClass
   * @return previous command of same name and version that was replaced.
   * @throws Exception
   */
  public Class<? extends Command> registerCommandClass(Class<? extends Command> commandClass) throws Exception {
    Command command = commandClass.newInstance();
    return registerCommandClass(commandClass, command.getCommandName(), command.getCommandVersion());
  }

  /**
   * @param commandClass
   * @return previous command of same name and version that was replaced.
   * @throws Exception
   */
  public Class<? extends Command> registerCommandClass(Class<? extends Command> commandClass, String name, String version) {
    Map<String, Class> commandVersions = commandClassesByNameAndVersion.get(name);
    if (commandVersions == null) {
      commandVersions = new HashMap<String, Class>();
      commandClassesByNameAndVersion.put(name, commandVersions);
    }
    return commandVersions.put(version, commandClass);
  }

  public Command commandFactory(String commandName, String commandVersion) throws Exception {
    Map<String, Class> versions = commandClassesByNameAndVersion.get(commandName);
    if (versions == null) {
      throw new UnsupportedCommandException("Unsupported command name " + commandName);
    }
    Class version = versions.get(commandVersion);
    if (version == null) {
      throw new UnsupportedCommandException("Unsupported command version " + commandVersion + " for name " + commandName);
    }
    return (Command) version.newInstance();
  }


}
