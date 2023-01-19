package org.main;

import lombok.Data;

@Data
public class CommandLineOptions {
    private Boolean test = false;

    private static CommandLineOptions commandLineOptions;
    // singleton implementation of command line options holder
    public static CommandLineOptions get() {
        if(commandLineOptions == null) {
            commandLineOptions = new CommandLineOptions();
        }
        return commandLineOptions;
    }
}
