package audio;

import javax.sound.sampled.*;
import java.io. File;
import java.io. IOException;
import java.util. HashMap;
import java.util.Map;

/**
 * Manages all audio playback in the game including background music and sound effects.
 * Supports looping background music and one-shot sound effects.
 */
public class AudioManager {
    // BGM 관련
    private Clip currentBGM;
    private String currentBGMName;
    private float bgmVolume = 0.7f; // 0.0 ~ 1.0
    
    // SFX 캐시 (미리 로드하여 지연 시간 최소화)
    private Map<String, Clip> sfxCache;
    private float sfxVolume = 0.8f; // 0.0 ~ 1.0
    
    // 싱글톤 인스턴스
    private static AudioManager instance;
    
    // 오디오 활성화 여부
    private boolean bgmEnabled = true;
    private boolean sfxEnabled = true;
    
    private AudioManager() {
        sfxCache = new HashMap<>();
        preloadSoundEffects();
    }
    
    /**
     * Get the singleton instance of AudioManager. 
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Preload all sound effects into memory for instant playback.
     */
    private void preloadSoundEffects() {
        // 효과음 파일들을 미리 로드
        String[] sfxFiles = {
            "jump",
            "platform_break",
            "bubble_collect",
            "oxygen_warning",
            "piece_collect",
            "death"
        };
        
        for (String sfx : sfxFiles) {
            try {
                loadSoundEffect(sfx);
            } catch (Exception e) {
                System.err.println("Failed to preload sound effect: " + sfx + " - " + e.getMessage());
            }
        }
    }
    
    /**
     * Load a sound effect file into the cache.
     * Expected file path:  audio/sfx/{name}.wav
     */
    private void loadSoundEffect(String name) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String path = "src/audio/sfx/" + name + ".wav";
        File audioFile = new File(path);
        
        if (!audioFile. exists()) {
            System.err.println("Sound effect file not found: " + path);
            return;
        }
        
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        
        sfxCache.put(name, clip);
        System.out.println("Loaded sound effect: " + name);
    }
    
    /**
     * Play a sound effect. 
     * @param name The name of the sound effect (without .wav extension)
     */
    public void playSFX(String name) {
        if (!sfxEnabled) return;
        
        Clip clip = sfxCache.get(name);
        if (clip == null) {
            System. err.println("Sound effect not found in cache: " + name);
            return;
        }
        
        // 이미 재생 중이면 처음부터 다시 시작
        if (clip.isRunning()) {
            clip.stop();
        }
        
        clip.setFramePosition(0);
        setVolume(clip, sfxVolume);
        clip.start();
    }
    
    /**
     * Play background music for a specific stage.
     * BGM will loop continuously until stopped or changed.
     * @param stageName The name of the stage
     */
    public void playBGM(String stageName) {
        if (!bgmEnabled) return;
        
        // 같은 BGM이 이미 재생 중이면 아무것도 하지 않음
        if (currentBGMName != null && currentBGMName.equals(stageName)) {
            return;
        }
        
        // 기존 BGM 정지
        stopBGM();
        
        try {
            String path = "src/audio/bgm/" + getBGMFileName(stageName) + ".wav";
            File audioFile = new File(path);
            
            if (!audioFile.exists()) {
                System.err.println("BGM file not found: " + path);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            currentBGM = AudioSystem.getClip();
            currentBGM.open(audioStream);
            
            setVolume(currentBGM, bgmVolume);
            currentBGM.loop(Clip.LOOP_CONTINUOUSLY);
            currentBGMName = stageName;
            
            System.out.println("Playing BGM for stage: " + stageName);
            
        } catch (Exception e) {
            System.err.println("Failed to play BGM for stage " + stageName + ": " + e.getMessage());
        }
    }
    
    /**
     * Get the BGM filename for a given stage name.
     * Maps stage names to audio file names.
     */
    private String getBGMFileName(String stageName) {
        // 스테이지 이름에 따라 BGM 파일명 매핑
        switch (stageName) {
            case "Gold Coast":
                return "stage1_gold_coast";
            case "Emerald Sea":
                return "stage2_emerald_sea";
            case "Shattered Wreckage":
                return "stage3_shattered_wreckage";
            case "Deep Water":
                return "stage4_deep_water";
            case "Labyrinth": 
                return "stage5_labyrinth";
            case "Rival Battle":
                return "stage6_rival_battle";
            case "Remote Island":
                return "stage7_remote_island";
            default:
                return "default_bgm";
        }
    }
    
    /**
     * Stop the currently playing background music.
     */
    public void stopBGM() {
        if (currentBGM != null && currentBGM.isRunning()) {
            currentBGM.stop();
            currentBGM. close();
        }
        currentBGM = null;
        currentBGMName = null;
    }
    
    /**
     * Pause the currently playing background music. 
     */
    public void pauseBGM() {
        if (currentBGM != null && currentBGM.isRunning()) {
            currentBGM. stop();
        }
    }
    
    /**
     * Resume the paused background music. 
     */
    public void resumeBGM() {
        if (currentBGM != null && ! currentBGM.isRunning() && bgmEnabled) {
            currentBGM.start();
        }
    }
    
    /**
     * Set the volume of a clip.
     * @param clip The audio clip
     * @param volume Volume level (0.0 to 1.0)
     */
    private void setVolume(Clip clip, float volume) {
        if (clip == null) return;
        
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            
            // Convert linear volume (0.0-1.0) to decibels
            float gain;
            if (volume <= 0.0f) {
                gain = min;
            } else {
                // Logarithmic scale for more natural volume control
                gain = min + (max - min) * volume;
                // Alternative: 20.0f * (float) Math.log10(volume);
            }
            
            gainControl.setValue(Math.max(min, Math.min(max, gain)));
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to set volume: " + e.getMessage());
        }
    }
    
    /**
     * Set the background music volume.
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setBGMVolume(float volume) {
        this.bgmVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentBGM != null) {
            setVolume(currentBGM, bgmVolume);
        }
    }
    
    /**
     * Set the sound effects volume.
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    /**
     * Enable or disable background music.
     */
    public void setBGMEnabled(boolean enabled) {
        this.bgmEnabled = enabled;
        if (! enabled) {
            stopBGM();
        }
    }
    
    /**
     * Enable or disable sound effects.
     */
    public void setSFXEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }
    
    /**
     * Check if BGM is enabled.
     */
    public boolean isBGMEnabled() {
        return bgmEnabled;
    }
    
    /**
     * Check if SFX is enabled.
     */
    public boolean isSFXEnabled() {
        return sfxEnabled;
    }
    
    /**
     * Clean up all audio resources.
     * Should be called when the game is closing.
     */
    public void cleanup() {
        stopBGM();
        
        for (Clip clip : sfxCache.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        sfxCache.clear();
    }
    
    /**
     * Reload all sound effects (useful after adding new audio files).
     */
    public void reloadSoundEffects() {
        for (Clip clip : sfxCache.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        sfxCache.clear();
        preloadSoundEffects();
    }
}