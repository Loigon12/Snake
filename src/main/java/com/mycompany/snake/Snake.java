/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Implementación del clásico juego Snake con interfaz gráfica Swing.
 *
 * Diseñado con enfoque en rendimiento, claridad y experiencia de usuario.
 *
 * @author Sebastian Londoño
 * @version 1.2
 */
public class Snake extends JPanel implements ActionListener, KeyListener {

    // === Constantes de configuración ===
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int GRID_SIZE = 25;
    private static final int COLUMNS = BOARD_WIDTH / GRID_SIZE;
    private static final int ROWS = BOARD_HEIGHT / GRID_SIZE;
    private static final int INITIAL_BODY_PARTS = 6;
    private static final int TIMER_DELAY_MS = 120;

    // === Estado del juego ===
    private final int[] x = new int[COLUMNS * ROWS];
    private final int[] y = new int[COLUMNS * ROWS];
    private int bodyParts;
    private int applesEaten;
    private int appleX, appleY;
    private char direction;
    private boolean running;
    private Timer gameTimer;
    private final Random random;

    // === Componentes UI ===
    private int highScore;
    private ControlPanel controlPanel;

    /**
     * Constructor principal. Inicializa lógica y UI.
     */
    public Snake() {
        random = new Random();
        initializeGameLogic();
        initializeUI();
        loadHighScore();
        startGame();
    }

    /**
     * Inicializa el estado del juego (sin afectar UI).
     */
    private void initializeGameLogic() {
        bodyParts = INITIAL_BODY_PARTS;
        applesEaten = 0;
        direction = 'R';
    }

    /**
     * Configura la interfaz gráfica del tablero (sin controles superpuestos).
     */
    private void initializeUI() {
    setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    setMinimumSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    setMaximumSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    setSize(BOARD_WIDTH, BOARD_HEIGHT); // Estandarizar el tamaño

    setBackground(Color.decode("#0d0d0d"));
    setFocusable(true);
    addKeyListener(this);
}

    /**
     * Carga la puntuación máxima desde almacenamiento persistente.
     */
    private void loadHighScore() {
        highScore = GameUtils.loadHighScore();
    }

    /**
     * Inicia una nueva partida.
     */
    public void startGame() {
        bodyParts = INITIAL_BODY_PARTS;
        applesEaten = 0;
        direction = 'R';
        running = true;

        // Inicializa serpiente centrada horizontalmente
        for (int i = 0; i < bodyParts; i++) {
            x[i] = (COLUMNS / 2 - i) * GRID_SIZE;
            y[i] = (ROWS / 2) * GRID_SIZE;
        }

        spawnApple();
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new Timer(TIMER_DELAY_MS, this);
        gameTimer.start();
    }

    /**
     * Reinicia el juego (invocado desde ControlPanel o tecla 'R').
     */
    public void restartGame() {
        if (!running) {
            startGame();
            repaint();
        }
    }

    /**
     * Genera una manzana en posición aleatoria, evitando colisión con la
     * serpiente.
     */
    private void spawnApple() {
        boolean overlaps;
        do {
            overlaps = false;
            appleX = random.nextInt(COLUMNS) * GRID_SIZE;
            appleY = random.nextInt(ROWS) * GRID_SIZE;

            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    overlaps = true;
                    break;
                }
            }
        } while (overlaps);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    /**
     * Dibuja todos los elementos del juego.
     */
    private void render(Graphics g) {
        if (running) {
            drawGrid(g);
            drawApple(g);
            drawSnake(g);
            drawScore(g);
        } else {
            drawGameOver(g);
        }
    }

    /**
     * Dibuja la cuadrícula de fondo y el borde del área jugable.
     */
    private void drawGrid(Graphics g) {
        // Cuadrícula sutil
        g.setColor(new Color(30, 30, 30));
        for (int i = 0; i <= COLUMNS; i++) {
            g.drawLine(i * GRID_SIZE, 0, i * GRID_SIZE, BOARD_HEIGHT);
        }
        for (int i = 0; i <= ROWS; i++) {
            g.drawLine(0, i * GRID_SIZE, BOARD_WIDTH, i * GRID_SIZE);
        }

        // Borde visible (2px rojo oscuro)
        g.setColor(new Color(80, 20, 20));
        g.drawRect(0, 0, BOARD_WIDTH - 1, BOARD_HEIGHT - 1);
        g.drawRect(1, 1, BOARD_WIDTH - 3, BOARD_HEIGHT - 3);
    }

    /**
     * Dibuja la manzana con brillo.
     */
    private void drawApple(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, GRID_SIZE, GRID_SIZE);
        g.setColor(Color.WHITE);
        g.fillOval(appleX + GRID_SIZE / 3, appleY + GRID_SIZE / 4, GRID_SIZE / 4, GRID_SIZE / 4);
    }

    /**
     * Dibuja la serpiente (cabeza brillante, cuerpo definido).
     */
    private void drawSnake(Graphics g) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(new Color(50, 205, 50)); // Cabeza: verde lima
            } else {
                g.setColor(new Color(34, 139, 34)); // Cuerpo: verde bosque
            }
            g.fillRect(x[i], y[i], GRID_SIZE, GRID_SIZE);

            g.setColor(Color.DARK_GRAY);
            g.drawRect(x[i], y[i], GRID_SIZE, GRID_SIZE);
        }
    }

    /**
     * Muestra puntuación actual y récord en esquina superior izquierda.
     */
    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String text = "Manzanas: " + applesEaten + "  Record: " + highScore;
        g.drawString(text, 12, 24);
    }

    /**
     * Muestra pantalla de Game Over con resumen y sugerencia.
     */
    private void drawGameOver(Graphics g) {
        // Capa semi-transparente
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Segoe UI", Font.BOLD, 48));
        String gameOver = "¡Game Over!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(gameOver, (BOARD_WIDTH - fm.stringWidth(gameOver)) / 2, BOARD_HEIGHT / 2 - 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        String score = "Puntuación: " + applesEaten;
        fm = g.getFontMetrics();
        g.drawString(score, (BOARD_WIDTH - fm.stringWidth(score)) / 2, BOARD_HEIGHT / 2 + 20);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        String tip = "Presiona 'R' para reiniciar";
        fm = g.getFontMetrics();
        g.drawString(tip, (BOARD_WIDTH - fm.stringWidth(tip)) / 2, BOARD_HEIGHT / 2 + 60);
    }

    /**
     * Mueve la serpiente una celda en la dirección actual.
     */
    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' ->
                y[0] -= GRID_SIZE;
            case 'D' ->
                y[0] += GRID_SIZE;
            case 'L' ->
                x[0] -= GRID_SIZE;
            case 'R' ->
                x[0] += GRID_SIZE;
        }
    }

    /**
     * Verifica colisión con manzana y actúa en consecuencia.
     */
    private void checkAppleCollision() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            spawnApple();
            GameUtils.playAppleSound();
        }
    }

    /**
     * Verifica colisiones con bordes o cuerpo propio.
     */
    private void checkCollisions() {
        // Bordes
        if (x[0] < 0 || x[0] >= BOARD_WIDTH || y[0] < 0 || y[0] >= BOARD_HEIGHT) {
            endGame();
            return;
        }

        // Cuerpo
        for (int i = 1; i < bodyParts; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                endGame();
                return;
            }
        }
    }

    /**
     * Finaliza el juego y actualiza récord si corresponde.
     */
    private void endGame() {
        running = false;
        gameTimer.stop();

        if (applesEaten > highScore) {
            highScore = applesEaten;
            GameUtils.saveHighScore(highScore);
        }
    }

    // === ActionListener (temporizador) ===
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkAppleCollision();
            checkCollisions();
        }
        repaint();
    }

    // === KeyListener ===
    @Override
    public void keyPressed(KeyEvent e) {
        // Reinicio rápido con 'R' tras Game Over
        if (!running && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
            return;
        }

        // Movimiento (solo durante el juego)
        if (!running) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        /* No usado */ }

    @Override
    public void keyReleased(KeyEvent e) {
        /* No usado */ }

    // === Punto de entrada (usa JLayeredPane para overlay) ===
    /**
     * Punto de entrada de la aplicación. Configura el JFrame con JLayeredPane
     * para superponer controles.
     */
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Snake — Sebastian Londoño");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Snake game = new Snake();
        game.controlPanel = new ControlPanel(game::restartGame);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        layeredPane.setSize(BOARD_WIDTH, BOARD_HEIGHT); // ← ¡Importante!

        layeredPane.add(game, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(game.controlPanel, JLayeredPane.PALETTE_LAYER);

        frame.add(layeredPane);
        frame.pack();

        game.controlPanel.layoutControls(BOARD_WIDTH, BOARD_HEIGHT);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    });
}
}
