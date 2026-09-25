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
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Paths {
    private static final Preferences prefs = Preferences.userNodeForPackage(SporeUplift.class);
    public static File sporeDir = null;
    public static File sporeDataDir = null;
    public static File sporeConfigDir = null;
    public static File sporeBinDir = null;
    public static boolean hasGA;
    public static boolean isSteam;
    /**
     * Try to find Spore. If I can't, then ask the user where it is. If I
     * ultimately can't find Spore, exit the process.
     */
    public static void findSpore() {
        String fromPrefs = prefs.get("sporeDir", null);
        if(fromPrefs != null) {
            validateSpore(new File(fromPrefs));
        }
        if(sporeDir == null) {
            validateSpore(new File("."));
        }
        if(sporeDir == null) {
            if(System.getProperty("os.name").startsWith("Windows")) {
                if(sporeDir == null) {
                    validateSpore(new File("C:\\GOG Games"));
                }
                if(sporeDir == null) {
                    validateSpore(new File("C:\\Program Files (x86)"));
                }
                if(sporeDir == null) {
                    validateSpore(new File("C:\\Program Files"));
                }
                if(sporeDir == null) {
                    validateSpore(new File("C:\\Program Files (x86)\\Steam\\steamapps\\common"));
                }
                if(sporeDir == null) {
                    validateSpore(new File("C:\\Program Files\\Steam\\steamapps\\common"));
                }
            } else {
                String home = System.getProperty("user.home");
                if(home != null) {
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.wine/drive_c/GOG Games"));
                    }
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.wine/drive_c/Program Files (x86)"));
                    }
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.wine/drive_c/Program Files"));
                    }
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.bottles/spore/drive_c/GOG Games"));
                    }
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.bottles/spore/drive_c/Program Files (x86)"));
                    }
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.bottles/spore/drive_c/Program Files"));
                    }
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.steam/steamapps/common/SPORE"));
                    }
                    if(sporeDir == null) {
                        validateSpore(new File(home+"/.steam/steam/steamapps/common/SPORE"));
                    }
                }
            }
        }
        if(sporeDir != null) return;
        chooseSpore();
        if(sporeDir == null) {
            JOptionPane.showMessageDialog(
                SporeUplift.frame,
                SporeUplift.bodyMessage("cannot_find_spore"),
                SporeUplift.titleMessage("cannot_find_spore"),
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
        prefs.put("sporeDir", sporeDir.toString());
    }
    static void chooseSpore() {
        while(true) {
            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle(SporeUplift.titleMessage("where_is_spore"));
            int res = fc.showDialog(SporeUplift.frame, SporeUplift.buttonStrings("select_spore")[0]);
            if(res != JFileChooser.APPROVE_OPTION) System.exit(0);
            File chosenPath = fc.getSelectedFile();
            if(validateSpore(chosenPath)) return;
            JOptionPane.showOptionDialog(
                null,
                SporeUplift.bodyMessage("does_not_smell_like_spore"),
                SporeUplift.titleMessage("cannot_find_spore"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                SporeUplift.buttonStrings("try_again", "cancel"),
                null
            );
        }
    }
    static boolean validateSpore(File path) {
        hasGA = false;
        isSteam = false;
        SporeUplift.stderrMessage("find.candidate", path.toString());
        if(!path.exists()) {
            SporeUplift.stderrMessage("find.reject.does_not_exist");
            return false;
        }
        if(!path.isDirectory()) {
            SporeUplift.stderrMessage("find.reject.notdir");
            return false;
        }
        String filename = path.getName().toLowerCase(Locale.ENGLISH);
        if(filename.equals("sporebin") || filename.equals("sporebinep1")
        || filename.equals("data") || filename.equals("dataep1")
        || filename.equals("sporemodloader")
        || filename.equals("support") || filename.equals("bp1content")) {
            SporeUplift.stderrMessage("find.might_be_inside");
            path = path.getParentFile();
            if(path == null) {
            SporeUplift.stderrMessage("find.reject.could_not_go_up");
                return false;
            }
            filename = path.getName().toLowerCase(Locale.ENGLISH);
        }
        if(filename.equals("spore")) {
            // This looks like it could be the Spore directory.
        } else if(filename.equals(".")) {
            // We're flying blind.
            SporeUplift.stderrMessage("find.search_cwd");
            File sporePath = searchForSporeIn(path);
            if(sporePath != null) {
                SporeUplift.stderrMessage("find.search_cwd.neighbor");
                path = sporePath;
            } else {
                File parentPath = new File(path, "..");
                sporePath = searchForSporeIn(parentPath);
                if(sporePath != null) {
                    SporeUplift.stderrMessage("find.search_cwd.cwd");
                    // DO NOT set path!
                }
                else {
                    sporePath = searchForSporeIn(new File(parentPath, ".."));
                    if(sporePath != null) {
                        SporeUplift.stderrMessage("find.search_cwd.parent");
                        path = parentPath;
                    }
                }
            }
        } else {
            SporeUplift.stderrMessage("find.search_child");
            File sporePath = searchForSporeIn(path);
            if(sporePath != null) path = sporePath;
        }
        String[] contents = path.list();
        File vanillaBinPath = null;
        File gaBinPath = null;
        File vanillaDataPath = null;
        File gaDataPath = null;
        boolean sawSteam = false;
        for(int n = 0; n < contents.length; ++n) {
            filename = contents[n].toLowerCase(Locale.ENGLISH);
            if(filename.equals("sporebin")) {
                vanillaBinPath = new File(path, contents[n]);
            }
            if(filename.equals("sporebinep1")) {
                gaBinPath = new File(path, contents[n]);
            }
            if(filename.equals("data")) {
                vanillaDataPath = new File(path, contents[n]);
            }
            if(filename.equals("dataep1")) {
                gaDataPath = new File(path, contents[n]);
            }
            if(filename.endsWith("vdf")) {
                sawSteam = true;
            }
        }
        if(gaBinPath != null && gaDataPath != null) {
            sporeDir = path;
            sporeConfigDir = new File(gaDataPath, "Config");
            sporeBinDir = gaBinPath;
            hasGA = true;
        } else if(gaBinPath != null || gaDataPath != null) {
            SporeUplift.stderrMessage("confusing_ga");
            JOptionPane.showMessageDialog(
                SporeUplift.frame,
                SporeUplift.bodyMessage("confusing_ga", SporeUplift.ISSUE_URL),
                SporeUplift.titleMessage("confused_by_spore"),
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        } else if(vanillaBinPath != null && vanillaDataPath != null) {
            sporeDir = path;
            sporeConfigDir = new File(vanillaDataPath, "Config");
            sporeBinDir = vanillaBinPath;
            hasGA = false;
        } else if(vanillaBinPath != null || vanillaDataPath != null) {
            SporeUplift.stderrMessage("find.reject.partial");
            return false;
        } else {
            SporeUplift.stderrMessage("find.reject.absent");
            return false;
        }
        if(!sporeConfigDir.exists() || !sporeConfigDir.isDirectory()) {
            SporeUplift.stderrMessage("find.reject.dir_not_dir",
                                      sporeConfigDir.toString());
            sporeDir = null;
            sporeConfigDir = null;
            sporeBinDir = null;
            return false;
        }
        File sporeAppPath = new File(sporeBinDir, "SporeApp.exe");
        File sporeAppBackupPath = new File(sporeBinDir, "SporeApp.exe.Backup");
        if(!sporeAppPath.exists() && !sporeAppBackupPath.exists()) {
            SporeUplift.stderrMessage("find.reject.file_not_file",
                                      sporeAppPath.toString());
            sporeDir = null;
            sporeConfigDir = null;
            sporeBinDir = null;
            return false;
        }
        SporeUplift.stderrMessage("find.accept", sporeDir.toString(),
                                  sporeBinDir.toString(),
                                  sporeConfigDir.toString());
        isSteam = sawSteam;
        return true;
    }
    static File searchForSporeIn(File path) {
        System.err.println("  Searching for a SPORE directory in "+path.toString());
        String[] contents = path.list();
        for(int n = 0; n < contents.length; ++n) {
            if(contents[n].toLowerCase(Locale.ENGLISH).equals("spore")) {
                path = new File(path, contents[n]);
                System.err.println("    (found it)");
                return path;
            }
        }
        return null;
    }
}
