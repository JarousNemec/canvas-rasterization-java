package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    // Time formater (etc. 14:25:08)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Static constructor for initial logger welcome text
    static {
        System.out.println("======================================================================================");
        System.out.println("   ____                            ____           _            _          _   _             \n" +
                "  / ___|__ _ _ ____   ____ _ ___  |  _ \\ __ _ ___| |_ ___ _ __(_)______ _| |_(_) ___  _ __  \n" +
                " | |   / _` | '_ \\ \\ / / _` / __| | |_) / _` / __| __/ _ \\ '__| |_  / _` | __| |/ _ \\| '_ \\ \n" +
                " | |__| (_| | | | \\ V / (_| \\__ \\ |  _ < (_| \\__ \\ ||  __/ |  | |/ / (_| | |_| | (_) | | | |\n" +
                "  \\____\\__,_|_| |_|\\_/ \\__,_|___/ |_| \\_\\__,_|___/\\__\\___|_|  |_/___\\__,_|\\__|_|\\___/|_| |_|\n" +
                "                                                                                            ");
        System.out.println("======================================================================================");
    }

    //Private constructor instead of static class like in CSharp
    private Logger(){
    }

    // Helper method for getting timestamp in right format
    private static String getTimestamp() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public static void info(String message) {
        System.out.println("[" + getTimestamp() + "] [INFO] " + message);
    }

    public static void warning(String message) {
        System.out.println("[" + getTimestamp() + "] [WARNING] " + message);
    }

    public static void error(String message) {
        System.err.println("[" + getTimestamp() + "] [ERROR] " + message);
    }
}
