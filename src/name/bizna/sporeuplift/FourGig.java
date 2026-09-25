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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JOptionPane;

/** Does the same thing as NTCore's 4GB patcher. Hopefully. */
public class FourGig {
    public static boolean possible = false;
    public static boolean wasOffered = false;
    public static boolean isSet = false;
    public static void check() throws Exception {
        File sporeAppPath = new File(Paths.sporeBinDir, "SporeApp.exe");
        byte[] header = new byte[0x180];
        RandomAccessFile f = new RandomAccessFile(sporeAppPath, "rws");
        if(f.read(header) != header.length) {
            f.close();
            // not i18n: user should never see this
            throw new Exception("Couldn't read enough bytes from SporeApp.exe.");
        }
        if(header[0] != 0x4D || header[1] != 0x5A || header[0x120] != 0x50 || header[0x121] != 0x45 || header[0x124] != 0x4C || header[0x125] != 0x01) {
            SporeUplift.stderrMessage("untouchable");
            JOptionPane.showMessageDialog(
                SporeUplift.frame,
                SporeUplift.bodyMessage("untouchable"),
                SporeUplift.titleMessage("file_error", "SporeApp.exe"),
                JOptionPane.WARNING_MESSAGE
            );
            f.close();
            return;
        }
        f.close();
        isSet = (header[0x136] & 0x20) == 0x20;
        if(Paths.isSteam) return;
        possible = true;
        // Near the beginning of all Windows executable is the string "This
        // program cannot be run in DOS mode." This is printed if you try to
        // run a Windows executable on DOS. Modifying this string is harmless.
        // If we touch this file, to enable or disable the 4GB patch, we will
        // replace the "O" in "DOS" with a "0" (zero). Thus, if the file says
        // "DOS" here, we can assume that we haven't touched the file. We will
        // automatically enable the 4GB patch the first time we touch the file.
        // (That logic is elsewhere.) This song and dance is needed so that the
        // user can *disable* the 4GB patch *if they want to*, but it can still
        // be enabled automatically if they don't care.
        wasOffered = header[0x6D] == 0x30;
        // Note: The DOS stub is not part of the checksum. …Right?
    }
    public static void set(boolean use4gb) throws Exception {
        if(!possible) {
            throw new Exception("FourGig.set called when the patch was not possible!");
        }
        if(use4gb == isSet) return; // nothing to do
        File sporeAppPath = new File(Paths.sporeBinDir, "SporeApp.exe");
        byte[] header = new byte[0x180];
        RandomAccessFile f = new RandomAccessFile(sporeAppPath, "rws");
        if(f.read(header) != header.length) {
            f.close();
            // not i18n: user should never see this
            throw new Exception("Couldn't read enough bytes from SporeApp.exe.");
        }
        if(header[0] != 0x4D || header[1] != 0x5A || header[0x120] != 0x50 || header[0x121] != 0x45 || header[0x124] != 0x4C || header[0x125] != 0x01) {
            SporeUplift.stderrMessage("untouchable");
            JOptionPane.showMessageDialog(
                SporeUplift.frame,
                SporeUplift.bodyMessage("untouchable"),
                SporeUplift.titleMessage("file_error", "SporeApp.exe"),
                JOptionPane.WARNING_MESSAGE
            );
            f.close();
            return;
        }
        boolean isSet = (header[0x136] & 0x20) == 0x20;
        if(isSet != FourGig.isSet) {
            f.close();
            // not i18n: user should never see this
            throw new Exception("SporeApp.exe was modified while we were away!");
        }
        // Replace the "O" in "This program cannot be run in DOS mode." with a
        // "0" (zero) to indicate that we have touched this file at least once.
        if(header[0x6D] == 0x5F) header[0x6D] = 0x30;
        if(use4gb) {
            SporeUplift.stderrMessage("applying_4gb");
            header[0x136] |= 0x20;
            // update the checksum :|
            // (Windows doesn't check the checksum, but update it anyway.)
            // ((also, it's literally a check sum.))
            ByteBuffer bb = ByteBuffer.wrap(header);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.position(0x178);
            int checksum = bb.getInt();
            checksum += 0x20;
            bb.position(0x178);
            bb.putInt(checksum);
        } else {
            SporeUplift.stderrMessage("removing_4gb");
            header[0x136] &= ~0x20;
            // update the checksum :|
            // (Windows doesn't check the checksum, but update it anyway.)
            // ((also, it's literally a check sum.))
            ByteBuffer bb = ByteBuffer.wrap(header);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.position(0x178);
            int checksum = bb.getInt();
            checksum -= 0x20;
            bb.position(0x178);
            bb.putInt(checksum);
        }
        f.seek(0);
        f.write(header);
        f.close();
        wasOffered = true;
        FourGig.isSet = use4gb;
    }
}
