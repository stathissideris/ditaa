package jargs.examples.gnu;

import jargs.gnu.CmdLineParser;

public class OptionTest {

    private static void printUsage() {
        System.err.println("usage: prog [{-v,--verbose}] [{-n,--name} a_name]"+
                           "[{-s,--size} a_number] [{-f,--fraction} a_float]");
    }

    public static void main( String[] args ) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option verbose = parser.addBooleanOption('v', "verbose");
        CmdLineParser.Option size = parser.addIntegerOption('s', "size");
        CmdLineParser.Option name = parser.addStringOption('n', "name");
        CmdLineParser.Option fraction = parser.addDoubleOption('f', "fraction");

        try {
            parser.parse(args);
        }
        catch ( CmdLineParser.OptionException e ) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(2);
        }

        // Extract the values entered for the various options -- if the
        // options were not specified, the corresponding values will be
        // null.
        Boolean verboseValue = (Boolean)parser.getOptionValue(verbose);
        Integer sizeValue = (Integer)parser.getOptionValue(size);
        String nameValue = (String)parser.getOptionValue(name);
        Double fractionValue = (Double)parser.getOptionValue(fraction);

        // For testing purposes, we just print out the option values
        System.out.println("verbose: " + verboseValue);
        System.out.println("size: " + sizeValue);
        System.out.println("name: " + nameValue);
        System.out.println("fraction: " + fractionValue);

        // Extract the trailing command-line arguments ('a_number') in the
        // usage string above.
        String[] otherArgs = parser.getRemainingArgs();
        System.out.println("remaining args: ");
        for ( int i = 0; i < otherArgs.length; ++i ) {
            System.out.println(otherArgs[i]);
        }

        // In a real program, one would pass the option values and other
        // arguments to a function that does something more useful.

        System.exit(0);
    }

}
