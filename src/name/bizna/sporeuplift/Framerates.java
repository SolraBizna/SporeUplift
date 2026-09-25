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

public class Framerates {
    public static final class FrameratePair {
        public final int framerate;
        public final int delay;
        FrameratePair(int framerate) {
            this.framerate = framerate;
            this.delay = 1000 / framerate;
        }
        public String toString() {
            if(framerate == 30) return "30Hz (default)";
            else return framerate+"Hz";
        }
    }
    public static final FrameratePair[] FRAMERATES = new FrameratePair[]{
        new FrameratePair(30),
        new FrameratePair(50),
        new FrameratePair(60),
        new FrameratePair(72),
        new FrameratePair(90),
        new FrameratePair(120),
        new FrameratePair(144),
        new FrameratePair(160),
        new FrameratePair(200),
        new FrameratePair(250),
        new FrameratePair(500),
        new FrameratePair(1000),
    };
}
