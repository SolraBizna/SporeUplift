/*

This file is part of SporeUplift.

SporeUplift is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation, either version 3 of the License, or (at your option) any later
version.

SporeUplift is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
SporeUplift. If not, see <https://www.gnu.org/licenses/>. 

*/

package name.bizna.sporeuplift;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale.Category;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

public class SporeUplift {
    public static final ResourceBundle messages = ResourceBundle.getBundle("SporeUpliftMessages", Locale.getDefault(Category.DISPLAY));
    public static final boolean rtl = messages.getString("orientation").toLowerCase(Locale.ENGLISH).equals("rtl");
    public static final String MESSAGE_PREFIX = "<html><p style=\"width: 400px\">";
    public static final String ISSUE_URL = "https://github.com/SolraBizna/SporeUplift/issues";
    public static final String MESSAGE_SUFFIX = "</p></html>";

    public static final int DEFAULT_FRAME_LIMIT_MS = 1000/30;
    public static final boolean DEFAULT_DISABLE_VALIDATION = false;
    // I don't know who originally determined these defaults, but they seem
    // legit?
    public static final int DEFAULT_SKINPAINT_TEXTURE_SIZE = 512;
    public static final float DEFAULT_SKINPAINT_BUMP_HEIGHT = 5.0f;
    public static final float DEFAULT_SKINPAINT_GLOSS_MULTIPLIER = 0.7f;
    public static final float DEFAULT_SKINPAINT_PHONG_MULTIPLIER = 0.95f;
    public static final float DEFAULT_SKINPAINT_PART_BUMP_SCALE = 0.8f;
    public static final boolean DEFAULT_SKINPAINT_AMB_OCC_ENABLED = true;
    public static final float DEFAULT_SKINPAINT_AMB_OCC_DIFFUSE = 0.5f;
    public static final float DEFAULT_SKINPAINT_AMB_OCC_SPECULAR = 0.5f;
    public static final int MIN_SKINPAINT_TEXTURE_SIZE_BITS = 9; // 512x512
    public static final int MAX_SKINPAINT_TEXTURE_SIZE_BITS = 12; // 4096x4096
    public static final int DANGEROUS_SKINPAINT_TEXTURE_SIZE_BITS = 12;

    public static boolean want4gb;
    public static int frameLimitMS = DEFAULT_FRAME_LIMIT_MS;
    public static boolean disableValidation = DEFAULT_DISABLE_VALIDATION;
    // if unspecified, assume they'll want the default
    public static int skinpaintTextureSize = 1024; // except this
    public static float skinpaintBumpHeight = DEFAULT_SKINPAINT_BUMP_HEIGHT;
    public static float skinpaintGlossMultiplier
        = DEFAULT_SKINPAINT_GLOSS_MULTIPLIER;
    public static float skinpaintPhongMultiplier
        = DEFAULT_SKINPAINT_PHONG_MULTIPLIER;
    public static float skinpaintPartBumpScale
        = DEFAULT_SKINPAINT_PART_BUMP_SCALE;
    public static boolean skinpaintAmbOccEnabled
        = DEFAULT_SKINPAINT_AMB_OCC_ENABLED;
    public static float skinpaintAmbOccDiffuse
        = DEFAULT_SKINPAINT_AMB_OCC_DIFFUSE;
    public static float skinpaintAmbOccSpecular
        = DEFAULT_SKINPAINT_AMB_OCC_SPECULAR;

    public static Window frame = null;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Slider.paintValue", false);
            ToolTipManager.sharedInstance().setDismissDelay(1000000); // :(
        } catch(Exception e) {
            stderrMessage("exception.laf_error");
            e.printStackTrace();
        }
        try {
            if(args.length > 0) {
                Paths.validateSpore(new File(args[0]));
            }
            Paths.findSpore();
            makeBackups();
            FourGig.check();
            if(FourGig.possible && !FourGig.wasOffered) {
                FourGig.set(true);
            }
            want4gb = FourGig.isSet;
            addProperties();
            checkConfigManager();
            frame = new Window();
        } catch(Exception e) {
            uncaught(e);
        }
    }
    /** Display an error dialog for an uncaught exception, and then exit. */
    static void uncaught(Exception e) {
        stderrMessage("exception.uncaught");
        e.printStackTrace();
        JOptionPane.showMessageDialog(
            frame,
            bodyMessage(
                "uncaught_exception",
                ISSUE_URL,
                e.getClass().getName()
            ),
            titleMessage("uncaught_exception"),
            JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
    /** Back up any of the files we touch, if they haven't already been backed
     * up. */
    static void makeBackups() throws Exception {
        File sporeAppPath
            = new File(Paths.sporeBinDir, "SporeApp.exe");
        File sporeAppBackupPath
            = new File(Paths.sporeBinDir, "SporeApp.exe.Backup");
        long appSize = sporeAppPath.length();
        long appBackupSize = 0;
        if(!sporeAppBackupPath.exists()
        || (appBackupSize = sporeAppBackupPath.length()) == 0) {
            // We are using the same ".Backup" convention as NTCore's 4GB
            // Patcher, which users may already have used.
            copy(sporeAppPath, sporeAppBackupPath);
        } else if(appBackupSize < appSize) {
            JOptionPane.showMessageDialog(
                frame,
                bodyMessage("spore_backup_too_small"),
                titleMessage("confused_by_spore"),
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
        File configManagerPath
            = new File(Paths.sporeConfigDir, "ConfigManager.txt");
        File configManagerBackupPath
            = new File(Paths.sporeConfigDir, "ConfigManager.txt.Backup");
        if(!configManagerBackupPath.exists()
        || configManagerBackupPath.length() == 0) {
            copy(configManagerPath, configManagerBackupPath);
        }
        File propertiesPath
            = new File(Paths.sporeConfigDir, "Properties.txt");
        File propertiesBackupPath
            = new File(Paths.sporeConfigDir, "Properties.txt.Backup");
        if(!propertiesBackupPath.exists()
        || propertiesBackupPath.length() == 0) {
            copy(propertiesPath, propertiesBackupPath);
        }
    }
    /** Make sure all required properties are in Properties.txt. If some are
     * missing, or we weren't able to be sure, add them to the end. */
    static void addProperties() throws Exception {
        final Pattern PROPERTY_PATTERN
            = Pattern.compile("property[ \t]+([^ \t]+)");
        File propertiesPath = new File(Paths.sporeConfigDir, "Properties.txt");
        String asString = "";
        try {
            byte[] allBytes = Files.readAllBytes(propertiesPath.toPath());
            asString = new String(allBytes, "ISO-8859-1");
        } catch(Exception e) {
            stderrMessage("exception.reading", "Properties.txt");
            e.printStackTrace();
            if(JOptionPane.showOptionDialog(
                frame,
                bodyMessage("read_error_maybe_recreate", "Properties.txt"),
                titleMessage("file_error", "Properties.txt"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                buttonStrings("go_ahead", "cancel"),
                null
            ) != JOptionPane.OK_OPTION) {
                stderrMessage("cancel_quit");
                System.exit(0);
            }
            asString = "";
        }
        String origString = asString;
        if(!asString.equals("") && !asString.endsWith("\r\n")) {
            stderrMessage("properties_missing_crlf");
            asString += "\r\n";
        }
        HashSet<String> existingProperties = new HashSet<String>();
        Matcher matcher = PROPERTY_PATTERN.matcher(origString);
        while(matcher.find()) {
            existingProperties.add(matcher.group(1));
        }
        for(int n = 0; n < Properties.NEEDED_PROPERTIES.length; ++n) {
            Properties.NeededProperty prop = Properties.NEEDED_PROPERTIES[n];
            if(!existingProperties.contains(prop.name)) {
                stderrMessage("properties_add_missing", prop.name);
                asString = asString + prop.toString() + "\r\n";
            }
        }
        if(origString.equals(asString)) {
            stderrMessage("properties_no_change_needed");
        } else {
            stderrMessage("properties_saving");
            Files.write(propertiesPath.toPath(),
                        asString.getBytes("ISO-8859-1"));
        }
    }
    /** Look in ConfigManager.txt to see whether some of the options we support
     * were already present in the file. */
    static void checkConfigManager() {
        final Pattern COMMENT_PATTERN = Pattern.compile("#[^\r\n]+([\r\n])");
        final Pattern SET_PROP_PATTERN
            = Pattern.compile("(int|bool|float)Prop[ \t]+([^ \t\r\n]+)[ \t]+"+
            "([^ \t\r\n]+)");
        File configManagerPath = new File(Paths.sporeConfigDir,
                                          "ConfigManager.txt");
        String asString;
        try {
            byte[] allBytes = Files.readAllBytes(configManagerPath.toPath());
            asString = new String(allBytes, "ISO-8859-1");
        } catch(Exception e) {
            stderrMessage("exception.reading", "ConfigManager.txt");
            e.printStackTrace();
            if(JOptionPane.showOptionDialog(
                frame,
                bodyMessage("warn_unparseable_configmanager"),
                titleMessage("file_error", "ConfigManager.txt"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                buttonStrings("continue", "cancel"),
                null
            ) != JOptionPane.OK_OPTION) {
                stderrMessage("cancel_quit");
                System.exit(0);
            }
            return;
        }
        asString = COMMENT_PATTERN.matcher(asString).replaceAll("$1");
        HashMap<String, String> setProps = new HashMap<String, String>();
        Matcher matcher = SET_PROP_PATTERN.matcher(asString);
        while(matcher.find()) {
            setProps.put(matcher.group(2), matcher.group(3));
        }
        try {
            String v = setProps.get("frameLimitMS");
            if(v != null) frameLimitMS = Integer.parseInt(v);
            v = setProps.get("disableValidation");
            if(v != null) disableValidation = parseBool(v);
            v = setProps.get("skinpaintTextureSize");
            if(v != null) skinpaintTextureSize = Integer.parseInt(v);
            v = setProps.get("skinpaintBumpHeight");
            if(v != null) skinpaintBumpHeight = Float.parseFloat(v);
            v = setProps.get("skinpaintGlossMultiplier");
            if(v != null) skinpaintGlossMultiplier = Float.parseFloat(v);
            v = setProps.get("skinpaintPhongMultiplier");
            if(v != null) skinpaintPhongMultiplier = Float.parseFloat(v);
            v = setProps.get("skinpaintPartBumpScale");
            if(v != null) skinpaintPartBumpScale = Float.parseFloat(v);
            v = setProps.get("skinpaintAmbOccEnabled");
            if(v != null) skinpaintAmbOccEnabled = parseBool(v);
            v = setProps.get("skinpaintAmbOccDiffuse");
            if(v != null) skinpaintAmbOccDiffuse = Float.parseFloat(v);
            v = setProps.get("skinpaintAmbOccSpecular");
            if(v != null) skinpaintAmbOccSpecular = Float.parseFloat(v);
        } catch(Exception e) {
            stderrMessage("exception.parsing_configmanager");
            e.printStackTrace();
            if(JOptionPane.showOptionDialog(
                frame,
                bodyMessage("confused_by_configmanager"),
                titleMessage("file_error", "ConfigManager.txt"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                buttonStrings("continue", "cancel"),
                null
            ) != JOptionPane.OK_OPTION) {
                stderrMessage("cancel_quit");
                System.exit(0);
            }
        }
    }
    /** Copy the contents of the file "from" to the file "to". */
    static void copy(File from, File to) throws Exception {
        stderrMessage("copying", from.toString(), to.toString());
        FileInputStream i = new FileInputStream(from);
        FileOutputStream o = new FileOutputStream(to);
        byte[] buf = new byte[16384];
        while(true) {
            int red = i.read(buf);
            if(red == -1) break;
            if(red != 0) o.write(buf, 0, red);
        }
        i.close();
        o.close();
    }
    /** Restore backed-up copies of all the files we touch, then exit.
     * (Triggered by a button press.) */
    public static void restoreBackups() {
        if(JOptionPane.showOptionDialog(
            frame,
            bodyMessage("confirm_restore"),
            titleMessage("confirm_restore"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            buttonStrings("confirm_restore", "cancel"),
            null
        ) != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            stderrMessage("restoring");
            copy(new File(Paths.sporeConfigDir, "ConfigManager.txt.Backup"),
                new File(Paths.sporeConfigDir, "ConfigManager.txt"));
            copy(new File(Paths.sporeConfigDir, "Properties.txt.Backup"),
                new File(Paths.sporeConfigDir, "Properties.txt"));
            copy(new File(Paths.sporeBinDir, "SporeApp.exe.Backup"),
                new File(Paths.sporeBinDir, "SporeApp.exe"));
            stderrMessage("restored");
            System.exit(0);
        } catch(Exception e) {
            uncaught(e);
        }
    }
    /** Save any changed settings, then exit.
     * (Triggered by a button press.) */
    public static void saveAndExit() {
        try {
            InputStream stream
                = SporeUplift.class.getResourceAsStream("ConfigManager.txt");
            byte[] b = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while(true) {
                int red = stream.read(b);
                if(red == -1) break;
                if(red != 0) {
                    sb.append(new String(b, 0, red, "ISO-8859-1"));
                }
            }
            if(Paths.hasGA) {
                sb.append("setOption OptionHighResTextures     $High\r\n\r\n");
            }
            sb.append("intProp frameLimitMS "+frameLimitMS+"\r\n"
            + "boolProp disableValidation "+disableValidation+"\r\n"
            + "intProp skinpaintTextureSize "+skinpaintTextureSize
                +" # default "+DEFAULT_SKINPAINT_TEXTURE_SIZE+"\r\n"
            + "floatProp skinpaintBumpHeight "+skinpaintBumpHeight
                +" # range 0-10, default "+DEFAULT_SKINPAINT_BUMP_HEIGHT+"\r\n"
            + "floatProp skinpaintGlossMultiplier "+skinpaintGlossMultiplier
                +" # range 0-2, neutral=1, default "+DEFAULT_SKINPAINT_GLOSS_MULTIPLIER+"\r\n"
            + "floatProp skinpaintPhongMultiplier "+skinpaintPhongMultiplier
                +" # range 0-2, default "
                +DEFAULT_SKINPAINT_PHONG_MULTIPLIER+"\r\n"
            + "floatProp skinpaintPartBumpScale "+skinpaintPartBumpScale
                +" # range 0-1, default "
                +DEFAULT_SKINPAINT_PART_BUMP_SCALE+"\r\n"
            + "boolProp skinpaintAmbOccEnabled "+skinpaintAmbOccEnabled
                +" # default "+DEFAULT_SKINPAINT_AMB_OCC_ENABLED+"\r\n"
            + "floatProp skinpaintAmbOccDiffuse "+skinpaintAmbOccDiffuse
                +" # range 0-1, default "
                +DEFAULT_SKINPAINT_AMB_OCC_DIFFUSE+"\r\n"
            + "floatProp skinpaintAmbOccSpecular "+skinpaintAmbOccSpecular
                +" # range 0-1, default "
                +DEFAULT_SKINPAINT_AMB_OCC_SPECULAR+"\r\n");
            b = sb.toString().getBytes("ISO-8859-1");
            Files.write(new File(Paths.sporeConfigDir, "ConfigManager.txt")
                        .toPath(), b);
            if(FourGig.possible) FourGig.set(want4gb);
            System.exit(0);
        } catch(Exception e) {
            uncaught(e);
        }
    }
    /** Wraps a message intended for a JOptionPane dialog in a prefix and
     * suffix that will make it HTML-y and linewrap-y. */
    static String bodyWrap(String message) {
        return MESSAGE_PREFIX+message+MESSAGE_SUFFIX;
    }
    /** Best-effort parses for booleanish values from ConfigManager.txt */
    static boolean parseBool(String v) {
        if(v.equals("true")) return true;
        else if(v.equals("false")) return false;
        else if(v.equals("$On")) return true;
        else if(v.equals("$Off")) return false;
        else return Integer.parseInt(v) != 0;
    }
    /** Gets a message with the given key, and optionally formats it with the
     * given arguments. */
    static String message(String key, Object... args) {
        try {
            String msg = messages.getString(key);
            if(args.length == 0) {
                return msg;
            } else {
                MessageFormat format
                    = new MessageFormat(msg,
                                        Locale.getDefault(Category.FORMAT));
                return format.format(args, new StringBuffer(), null).toString();
            }
        } catch(MissingResourceException e) {
            System.err.println("MISSING I18N KEY: "+e.getKey());
            return "«««"+e.getKey()+"»»»";
        }
    }
    /** Like message, but assumes "dialog.body." before the key, and wraps the
     * message in HTML for a dialog body. */
    static String bodyMessage(String key, Object... args) {
        return bodyWrap(message("dialog.body."+key, args));
    }
    /** Like message, but assumes "dialog.title." before the key. */
    static String titleMessage(String key, Object... args) {
        return message("dialog.title."+key, args);
    }
    /** Like message, but assumes "stderr." before the key, and prints the
     * message directly to stderr. */
    static void stderrMessage(String key, Object... args) {
        System.err.println(message("stderr."+key, args));
    }
    /** Converts the given list of buttons into dialog.button.* references, and
     * then fetches them. (For passing to JOptionPane.show*Dialog) */
    static String[] buttonStrings(String... names) {
        for(int n = 0; n < names.length; ++n) {
            names[n] = message("dialog.button."+names[n]);
        }
        return names;
    }
}
