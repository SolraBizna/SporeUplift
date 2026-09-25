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

public class Properties {
    public static class NeededProperty {
        public final String name;
        public final String hash;
        public final String type;
        NeededProperty(String name, String hash, String type) {
            this.name = name;
            if(hash == null) {
                this.hash = "(hash("+name+"))";
            } else {
                this.hash = hash;
            }
            this.type = type;
        }
        public String toString() {
            return "property "+this.name+" "+this.hash+" "+this.type;
        }
    }
    public static final NeededProperty[] NEEDED_PROPERTIES = new NeededProperty[]{
        // properties from GA's Properties.txt
        new NeededProperty("OptionShadows", "0x0461709e", "uint32"),
        new NeededProperty("OptionTextureDetail", "0x0461709f", "uint32"),
        new NeededProperty("OptionEffects", "0x046170a0", "uint32"),
        new NeededProperty("OptionBakeQuality", "0x046170a3", "uint32"),
        new NeededProperty("OptionLighting", "0x046170a6", "uint32"),
        new NeededProperty("OptionPlanetQuality", "0x046170a7", "uint32"),
        new NeededProperty("OptionGameQuality", "0x05c9482d", "uint32"),
        new NeededProperty("OptionDOF", "0x4c0dd839", "uint32"),
        new NeededProperty("OptionHighResTextures", "0x08488f95", "uint32"),
        new NeededProperty("OptionPhotoRes", "0x0473b8cb", "uint32"), 
        new NeededProperty("OptionVideoRes", "0x0473b8cc", "uint32"),
        new NeededProperty("OptionAudioPerformance", "0x7d8ed666", "uint32"),
        new NeededProperty("OptionFullScreen", "0x046170a2", "uint32"),
        new NeededProperty("OptionFitToScreen", "0x046170a5", "uint32"),
        new NeededProperty("OptionDiskCacheSize", "0x046170a4", "uint32"),
        new NeededProperty("OptionScreenSize", "0x046170a1", "uint32"),
        new NeededProperty("OptionTutorialsEnabled", "0x04ea96cb", "uint32"),
        new NeededProperty("ScenarioStarEffectsEnabled", "0x08069a6c", "bool"),
        new NeededProperty("ScenarioGAEditorEffectsEnabled", "0x0806dbba", "bool"),
        new NeededProperty("ScenarioGAButtonClicked", "0x080d7de1", "bool"),
        new NeededProperty("ScenarioGAFirstLaunched", "0x080d97fe", "bool"),
        new NeededProperty("ScenarioSpaceGameLaunched", "0x080eaa93", "bool"),
        new NeededProperty("ScenarioCaptainEditorLaunched", "0x080d8460", "bool"),
        new NeededProperty("ScenarioAdventureEditorLaunched", "0x080d8470", "bool"),
        new NeededProperty("NumScenarioTutorialCompletedBuild", "0x07abf095", "uint32"),
        new NeededProperty("NumScenarioTutorialCompletedTerrain", "0x07abf09d", "uint32"),
        new NeededProperty("ScenarioPlayModeTutorialComplete", "0x07be69ad", "bool"),
        new NeededProperty("CaptainEditorTutorialCompleted", "0x07be27a2", "bool"),
        new NeededProperty("CaptainSporepediaTutorialCompleted", "0x07d1fe81", "bool"),
        new NeededProperty("OptionLoggedInYouTube", "0x05664a8b", "uint32"),
        new NeededProperty("OptionShowHints", "0x05b5bb5e", "uint32"), 
        new NeededProperty("OptionBuddiesOnly", "0x05de7b4a", "uint32"),
        new NeededProperty("OptionEdgeScroll", "0x0636ec26", "uint32"),
        new NeededProperty("OptionExpireOld", "0x0626f940", "uint32"),
        new NeededProperty("OptionArchiveExpired", "0x0626f9c0", "uint32"),
        new NeededProperty("OptionExpireDays", "0x0626f958", "uint32"),
        new NeededProperty("OptionDownloadSize", "0x0685a63c", "uint32"),
        new NeededProperty("OptionUpdateLimit", "0x0685a785", "uint32"),
        new NeededProperty("OptionUpdateLimitDays", "0x0685a821", "uint32"),
        new NeededProperty("OptionCaptureUI", "0x0631621a", "bool"),
        new NeededProperty("OptionExplainSporepedia", "0x0604a51a", "uint32"),
        new NeededProperty("OptionExplainPaintLikeThis", "0x0604a561", "uint32"),
        new NeededProperty("OptionCellControls", "0x0679b833", "uint32"),
        new NeededProperty("OptionCreatureControls", "0x0679b85e", "uint32"),
        new NeededProperty("OptionTribeControls", "0x0679b868", "uint32"),
        new NeededProperty("OptionCivControls", "0x0679b873", "uint32"),
        new NeededProperty("OptionSpaceControls", "0x0679b880", "uint32"),
        new NeededProperty("PrefsClearGraphicsCache", null, "bool"),
        new NeededProperty("AudioMasterVolume", "(hash(mastervolume))", "float"),
        new NeededProperty("AudioSFXVolume", "(hash(sfxvolume))", "float"),
        new NeededProperty("AudioMusicVolume", "(hash(musicvolume))", "float"),
        new NeededProperty("AudioVOXVolume", "(hash(voxvolume))", "float"),
        new NeededProperty("AudioSpeakerMode", "(hash(speakermode))", "uint32"),
        new NeededProperty("AudioMuteAll", "(hash(muteall))", "bool"),
        new NeededProperty("UserName", "0x040512ef", "string8"),
        new NeededProperty("Password", "0x040512f4", "string8"),
        new NeededProperty("PlayOffline", "0x040e3d98", "bool"),
        new NeededProperty("PromptOnStartup", "0x0440a514", "bool"),
        new NeededProperty("YTUserName", "0x05664bf5", "string8"),
        new NeededProperty("YTPassword", "0x05664bf6", "string8"),
        // YTPromptOnStartup is commented out in the original
        //new NeededProperty("YTPromptOnStartup", "0x05664bf7", "bool"),
        new NeededProperty("ShaderPath", null, "int"),
        new NeededProperty("EffectsInstancing", null, "bool"),
        new NeededProperty("MRT", "41", "bool"),
        new NeededProperty("AlwaysFullscreen", "0x05dd4647", "bool"),
        new NeededProperty("RenderTargetCorrection", "68", "bool"),
        new NeededProperty("dropShadowQualityText", null, "int"),
        new NeededProperty("dropShadowQualityImage", null, "int"),
        new NeededProperty("NumFramesToBuffer", "0x05c97448", "int"),
        new NeededProperty("terrainGenerateBrushesPerFrame", null, "int"),
        new NeededProperty("terrainGenerateSingleStep", null, "bool"),
        new NeededProperty("terrainGenerateTimeLimit", null, "int"),
        new NeededProperty("MacSpecificText", "0x061b67b6", "bool"),
        new NeededProperty("Support51Audio", "0x063ab656", "bool"),
        new NeededProperty("HasShownExportToolEULA", "0x087c4363", "bool"),
        // properties from the Internet
        new NeededProperty("frameLimitMS", "44", "int"),
        new NeededProperty("disableValidation", "0x055d7ca1", "int"),
        // properties from K2017's "Shiny Graphics Fix"
        new NeededProperty("skinpaintTextureSize", null, "int"),
        new NeededProperty("skinpaintBumpHeight", null, "float"),
        new NeededProperty("skinpaintGlossMultiplier", null, "float"),
        new NeededProperty("skinpaintPhongMultiplier", null, "float"),
        new NeededProperty("skinpaintPartBumpScale", null, "float"),
        new NeededProperty("skinpaintAmbOccEnabled", null, "bool"),
        new NeededProperty("skinpaintAmbOccDiffuse", null, "float"),
        new NeededProperty("skinpaintAmbOccSpecular", null, "float"),
    };
}
