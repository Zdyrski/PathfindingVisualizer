package controls;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;

public class MyLog {

    private final static Logger logger = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );

    private static void setupLogger(String fileName, String msg1) {
        LogManager.getLogManager().reset();
        logger.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        try { // Default file output is in users's home directory
            FileHandler fileHandler = new FileHandler(fileName);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            // don't stop my program but log out to console.
            logger.log(Level.SEVERE, "File logger not working.", e);
        }
        System.out.println("----------LOG----------");
        logger.info(msg1);
    }

    public static void writeToFile(ArrayList crtX, ArrayList crtY, String fileName, Boolean check) {
        try {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file, true);
            if(crtX.size() != 0) {
                BufferedWriter myWriter = new BufferedWriter(new OutputStreamWriter(fos));
                /*
                if(check) {
                    for(int i = 0; i < crtX.size(); i++) {
                        myWriter.write(String.valueOf(crtX));
                        myWriter.newLine();
                    }
                } else {
                    myWriter.write(String.valueOf(crtX));
                    myWriter.newLine();
                    myWriter.write(String.valueOf(crtY));
                } */
                myWriter.write(String.valueOf(crtX));
                myWriter.newLine();
                myWriter.write(String.valueOf(crtY));
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } else {
                BufferedWriter myWriter = new BufferedWriter(new OutputStreamWriter(fos));
                myWriter.write("Algorytm nie znalazł ścieżki");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static Logger getLogger(){
        if(logger == null){
            new MyLog();
        }
        return logger;
    }

    public static void log(Level level, String msg, String msg1, String fileName){
        setupLogger(fileName, msg1);
        //getLogger().log(level, msg, msg1);
        System.out.println(msg + ": " + msg1);
    }


    public static void main(String[] args) throws java.io.IOException {}
}
