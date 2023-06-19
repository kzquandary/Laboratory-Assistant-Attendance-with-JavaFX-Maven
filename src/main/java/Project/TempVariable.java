package Project;

import java.lang.String;
import java.io.File;

public class TempVariable {
    public static String username;
    public static String password;
    public static String token;
    public static String appDataDir = System.getenv("APPDATA");
    public static String folderPath = appDataDir + File.separator + "AslabApp";
    public static String filePath = folderPath + File.separator + "token.txt";
}
