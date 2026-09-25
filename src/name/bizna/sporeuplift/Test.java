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

public abstract class Test {
    public abstract boolean test() throws Exception;
    public abstract String getTestName();
    public final String getPassText() {
        return SporeUplift.message("test.pass."+getTestName());
    }
    public final String getFailText() {
        return SporeUplift.message("test.fail."+getTestName());
    }
    public String getFailURI() {
        return null;
    }
    public boolean isApplicable() {
        return true;
    }
    public static final Test[] TESTS = new Test[]{
        // Galactic Adventures
        new Test(){
            // Related note: OptionHighResTextures may be a GA-only option.
            // Investigate what happens if you start vanilla Spore with
            // that option present. Other than that, no incompatibilities
            // are expected.
            @Override public boolean test() {
                return Paths.hasGA;
            }
            @Override public String getTestName() {
                return "ga";
            }
        },
        // Spore ModAPI
        new Test(){
            @Override public boolean test() throws Exception {
                return new File(Paths.sporeBinDir, "dinput8.dll").exists();
            }
            @Override public String getTestName() {
                return "modapi";
            }
            @Override public String getFailURI() {
                return "https://launcherkit.sporecommunity.com/";
            }
            @Override public boolean isApplicable() {
                return !Paths.isSteam;
            }
        },
        // SporeCrashFix
        new Test(){
            @Override public boolean test() {
                File f = new File(Paths.sporeDir, "SporeModLoader");
                f = new File(f, "ModLibs");
                f = new File(f, "SporeCrashFix.dll");
                return f.exists();
            }
            @Override public String getTestName() {
                return "crashfix";
            }
            @Override public String getFailURI() {
                return "https://github.com/Rosalie241/SporeCrashFix/releases/latest";
            }
            @Override public boolean isApplicable() {
                return !Paths.isSteam;
            }
        },
        // Complain about Steam
        new Test(){
            @Override public boolean test() {
                return false;
            }
            @Override public String getTestName() {
                return "steambad";
            }
            @Override public boolean isApplicable() {
                return Paths.isSteam;
            }
        },
    };
}
