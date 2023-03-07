package org.main;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.*;
import org.apache.commons.cli.*;
import org.bju.KCG.KCGLexer;
import org.bju.KCG.KCGParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParseException, IOException {
        List<String> files = parseCommandLineArgs(args);

        StringBuilder contents = new StringBuilder();
        for(String file : files) {
            String fileContents = Files.readString(Paths.get(file));
            if(!fileContents.endsWith("\n")) {
                fileContents += "\n";
            }
            contents.append(fileContents);
        }

        CharStream input = CharStreams.fromString(contents.toString());
        org.bju.KCG.KCGLexer lexer = new KCGLexer(input);
        org.bju.KCG.KCGParser parser = new KCGParser(new CommonTokenStream(lexer));
        var tree = parser.start();
        var analysis = new KCGSemanticAnalyzer();
        analysis.visit(tree);
        String filename = files.get(0).replace(".kcg", ".s");
        new KCGCodeGenerator(analysis.getSymbolTable(), files.get(0).replace(".kcg", ".s")).visit(tree);
    }

    // parse the command line arguments and return all non arg arguments as an ArrayList
    private static List<String> parseCommandLineArgs(String[] args) throws ParseException {
        Options options = new Options();

        // add test option
        options.addOption("t", "test", false, "test");

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        if(commandLine.hasOption("t")) {
            CommandLineOptions.get().setTest(true);
        }

        // all non matched things get returned
        return commandLine.getArgList();
    }
}
