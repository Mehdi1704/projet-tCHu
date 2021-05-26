package ch.epfl.tchu.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {


    /**
     * Joue un enregistrement audio.
     *
     * @param audioFilePath chemin pour le fichier audio.
     */
    public static void play(String audioFilePath, Boolean still) {
        try {
            InputStream audioFile = new BufferedInputStream(Objects.requireNonNull(AudioPlayer.class.getResourceAsStream(audioFilePath)));

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip audioClip = (Clip) AudioSystem.getLine(info);

            audioClip.open(audioStream);

            audioClip.start();
            if (still)
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);


        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException | NullPointerException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
    }
}
