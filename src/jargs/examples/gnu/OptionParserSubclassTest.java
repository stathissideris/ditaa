package jargs.examples.gnu;

import jargs.gnu.CmdLineParser;

public class OptionParserSubclassTest {

    private static class MyOptionsParser extends CmdLineParser {

        public static final Option VERBOSE = new
            CmdLineParser.Option.BooleanOption('v',"verbose");

        public static final Option SIZE = new
            CmdLineParser.Option.IntegerOption('s',"size");

        public static final Option NAME = new
            CmdLineParser.Option.StringOption('n',"name");

        public static final Option FRACTION = new
            CmdLineParser.Option.DoubleOption('f',"fraction");

        public MyOptionsParser() {
            super();
            addOption(VERBOSE);
            addOption(SIZE);
            addOption(NAME);
            addOption(FRACTION);
        }
    }

    private static void printUsage() {
        System.err.println("usage: prog [{-v,--verbose}] [{-n,--name} a_name]"+
                           "[{-s,--size} a_number] [{-f,--fraction} a_float]");
    }

    public static void main( String[] args ) {
        MyOptionsParser myOptions = new MyOptionsParser();

        try {
            myOptions.parse(args);
        }
        catch ( CmdLineParser.UnknownOptionException e ) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(2);
        }
        catch ( CmdLineParser.IllegalOptionValueException e ) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(2);
        }

        CmdLineParser.Option[] allOptions =
            new CmdLineParser.Option[] { MyOptionsParser.VERBOSE,
                                         MyOptionsParser.NAME,
                                         MyOptionsParser.SIZE,
                                         MyOptionsParser.FRACTION };

        for ( int j = 0; j<allOptions.length; ++j ) {
            System.out.println(allOptions[j].longForm() + ": " +
                               myOptions.getOptionValue(allOptions[j]));
        }

        String[] otherArgs = myOptions.getRemainingArgs();
        System.out.println("remaining args: ");
        for ( int i = 0; i<otherArgs.length; ++i ) {
            System.out.println(otherArgs[i]);
        }
        System.exit(0);
    }

}
