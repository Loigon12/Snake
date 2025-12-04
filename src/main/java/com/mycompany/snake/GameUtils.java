/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.snake;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;

/**
 * Clase de utilidad para operaciones comunes del juego:
 * - Gestión de puntuación máxima persistente.
 * - Reproducción de efectos de sonido.
 * 
 * @author Sebastian Londoño
 */
public class GameUtils {

    private static final String HIGHSCORE_FILE = "highscore.txt";
    private static final String APPLE_SOUND_PATH = "/sounds/apple.wav";

    /**
     * Guarda la puntuación máxima en un archivo de texto.
     * 
     * @param score Puntuación a guardar (debe ser >= 0)
     * @throws IllegalArgumentException si la puntuación es negativa
     */
    public static void saveHighScore(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("La puntuación no puede ser negativa.");
        }
        try (PrintWriter writer = new PrintWriter(new File(HIGHSCORE_FILE))) {
            writer.println(score);
        } catch (IOException e) {
            System.err.println("No se pudo guardar la puntuación máxima: " + e.getMessage());
        }
    }

    /**
     * Carga la puntuación máxima almacenada. Si no existe el archivo o hay error,
     * devuelve 0.
     * 
     * @return Puntuación máxima guardada, o 0 si no existe.
     */
    public static int loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line = reader.readLine();
            return line != null ? Integer.parseInt(line.trim()) : 0;
        } catch (IOException | NumberFormatException e) {
            System.err.println("No se encontró puntuación previa (usando 0). Error: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Reproduce un sonido breve (efecto de sonido no bloqueante).
     * Soporta archivos WAV en 16-bit PCM (estándar para efectos cortos).
     */
    public static void playSound(String soundPath) {
        try {
            URL soundURL = GameUtils.class.getResource(soundPath);
            if (soundURL == null) {
                System.err.println("Sonido no encontrado: " + soundPath);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            // Liberar recursos tras reproducción (evita leaks)
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            System.err.println("Error al reproducir sonido: " + e.getMessage());
        }
    }

    /**
     * Reproduce el sonido de manzana usando la ruta predefinida.
     */
    public static void playAppleSound() {
        playSound(APPLE_SOUND_PATH);
    }
}
