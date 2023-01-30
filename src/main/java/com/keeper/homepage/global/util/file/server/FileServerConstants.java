package com.keeper.homepage.global.util.file.server;

import static java.io.File.separator;

public class FileServerConstants {

  public static final String KEEPER_FILES = "keeper_files";
  public static final String FILES = "files";
  public static final String ROOT_PATH = System.getProperty("user.dir");
  public static final String RESOURCE_PATH = ROOT_PATH + separator + KEEPER_FILES + separator;
  public static final String DEFAULT_FILE_PATH = RESOURCE_PATH + FILES + separator;
}
