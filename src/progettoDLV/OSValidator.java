/**
 * Author: Domenico Monaco, Yong Mook Kim
 * <p>
 * Source: https://gist.github.com/kiuz/816e24aa787c2d102dd0
 * <p>
 * License: GNU v2 2014
 * <p>
 * Fork / Learned: http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 */
package progettoDLV;

class OSValidator {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    public static String getOS() {
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        } else {
            return "err";
        }
    }

}