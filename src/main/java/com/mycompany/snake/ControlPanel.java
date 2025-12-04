/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.snake;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de controles superpuesto (overlay) para el juego.
 * Contiene botones como "Reiniciar", configurables y no intrusivos.
 * 
 * @author Sebastian Londoño
 */
public class ControlPanel extends JPanel {

    private final JButton restartButton;

    /**
     * Constructor. Inicializa controles con estilo moderno.
     */
    public ControlPanel(Runnable onRestart) {
        setOpaque(false); // Transparente para no tapar el fondo
        setLayout(null);  // Posicionamiento absoluto

        restartButton = createRestartButton(onRestart);
        add(restartButton);

        // Ajustar tamaño mínimo para que quepa el botón
        setPreferredSize(new Dimension(120, 40));
    }

    /**
     * Crea un botón de reinicio estilizado con hover y sombra sutil.
     */
    private JButton createRestartButton(Runnable onRestart) {
        JButton button = new JButton("↻");
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Estilo base
        button.setBackground(new Color(40, 40, 40, 220)); // Negro semi-transparente
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        // Hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 40, 40, 220));
            }
        });

        button.addActionListener(e -> onRestart.run());
        return button;
    }

    /**
     * Posiciona los controles (llamado manualmente tras conocer tamaño del tablero).
     * 
     * @param boardWidth Ancho del tablero de juego
     * @param boardHeight Alto del tablero de juego
     */
    public void layoutControls(int boardWidth, int boardHeight) {
        int buttonWidth = 50;
        int buttonHeight = 40;
        int margin = 12;

        restartButton.setBounds(
            boardWidth - buttonWidth - margin,
            margin,
            buttonWidth,
            buttonHeight
        );
    }
}