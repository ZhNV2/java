package ru.spbau.zhidkov.utils;

import com.beust.jcommander.Parameter;

/**
 * Basic class for basic parameters and functions of
 * classes communicating with user.
 */
public abstract class MainParametersAbstract {
    @Parameter(names = "--help", help = true, description = "help information")
    protected boolean help;

    private final static String HELP = "use --help for more information";

    @Parameter(names = "--port", required = true, description = "target server port")
    protected int serverPort;

    @Parameter(names = "--hostname", required = true, description = "target server hostname")
    protected String hostname;

    /**
     * Writes help info in case of exception
     * @param e exception to be logged
     */
    protected static void logException(Exception e) {
        System.out.println(e.getMessage());
        System.out.println(HELP);
    }
}
