/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mazesolvergui;

/**
 *
 * @author anasbaig
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MazeSolverGUI extends JFrame {
    private final int CELL_SIZE = 30;
    private final int MAZE_SIZE = 10;
    private final char WALL = '#';
    private final char START = 'S';
    private final char END = 'E';
    private final char PATH = '.';
    private final char VISITED = 'V';

    private char[][] maze;
    private JLabel[][] labels;
    private JButton solveButton;
    private JComboBox<String> mazeTypeCombo;
    private JButton generateButton;
    private JSlider delaySlider;

    private int delay = 500; // Default delay in milliseconds
    private Timer timer;

    public MazeSolverGUI() {
        setTitle("Maze Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        maze = new char[MAZE_SIZE][MAZE_SIZE];
        labels = new JLabel[MAZE_SIZE][MAZE_SIZE];

        JPanel mazePanel = new JPanel();
        mazePanel.setLayout(new GridLayout(MAZE_SIZE, MAZE_SIZE));

        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                maze[i][j] = '.';
                labels[i][j] = new JLabel(Character.toString(maze[i][j]));
                labels[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                labels[i][j].setOpaque(true);
                labels[i][j].setBackground(Color.WHITE);
                mazePanel.add(labels[i][j]);
            }
        }
        solveButton = new JButton("Solve Maze");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveMaze();
//        solveButton = new JButton("Solve Maze");
//        solveButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                solveMaze();
            }
        });

        mazeTypeCombo = new JComboBox<>(new String[]{"Random Maze", "Block Maze"});
        generateButton = new JButton("Generate Maze");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMaze();
            }
        });

        delaySlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, delay);
        delaySlider.setMajorTickSpacing(100);
        delaySlider.setPaintTicks(true);
        delaySlider.setPaintLabels(true);
        delaySlider.addChangeListener(e -> {
            delay = delaySlider.getValue();
            timer.setDelay(delay);
        });

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(solveButton);
        controlPanel.add(mazeTypeCombo);
        controlPanel.add(generateButton);
        controlPanel.add(new JLabel("Delay:"));
        controlPanel.add(delaySlider);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mazePanel, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        initializeMaze();
    }

    private void initializeMaze() {
        char[][] mazeLayout = {
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
                {'#', 'S', '.', '#', '#', '#', '#', '#', '#', '#'},
                {'#', '.', '.', '#', '#', '#', '#', '#', '.', '#'},
                {'#', '.', '#', '#', '#', '#', '#', '#', '.', '#'},
                {'#', '.', '#', '#', '#', '#', '#', '#', '.', '#'},
                {'#', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', '.', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', '.', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', 'E', '#'},
                {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
        };

        for (int i = 0; i < MAZE_SIZE; i++) {
            System.arraycopy(mazeLayout[i], 0, maze[i], 0, MAZE_SIZE);
        }

        updateMazeGUI();
    }

    private void updateMazeGUI() {
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                labels[i][j].setText(Character.toString(maze[i][j]));
                if (maze[i][j] == WALL) {
                    labels[i][j].setBackground(Color.BLACK);
                } else if (maze[i][j] == START) {
                    labels[i][j].setBackground(Color.GREEN);
                } else if (maze[i][j] == END) {
                    labels[i][j].setBackground(Color.RED);
                } else {
                    labels[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    private void generateMaze() {
        String mazeType = (String) mazeTypeCombo.getSelectedItem();
        if (mazeType.equals("Random Maze")) {
            generateRandomMaze();
        } else if (mazeType.equals("Block Maze")) {
            generateBlockMaze();
        }
    }

    private void generateRandomMaze() {
        Random random = new Random();
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                if (random.nextInt(5) == 0) { // 1 in 5 chance to create a wall
                    maze[i][j] = WALL;
                } else {
                    maze[i][j] = '.';
                }
            }
        }
        maze[0][1] = START; // Set start
        maze[MAZE_SIZE - 1][MAZE_SIZE - 2] = END; // Set end
        updateMazeGUI();
    }

    private void generateBlockMaze() {
        for (int i = 0; i < MAZE_SIZE; i++) {
            for (int j = 0; j < MAZE_SIZE; j++) {
                if ((i == 0 && j != 1) || (i == MAZE_SIZE - 1 && j != MAZE_SIZE - 2)) {
                    maze[i][j] = WALL;
                } else {
                    maze[i][j] = '.';
                }
            }
        }
        maze[0][1] = START; // Set start
        maze[MAZE_SIZE - 1][MAZE_SIZE - 2] = END; // Set end
        updateMazeGUI();
    }

    private void solveMaze() {
        solveButton.setEnabled(false);
        timer = new Timer(delay, new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // The call to initializeMaze() has been removed from here
                if (step < MAZE_SIZE * MAZE_SIZE) {
                    int x = step / MAZE_SIZE;
                    int y = step % MAZE_SIZE;
                    if (maze[x][y] == PATH || maze[x][y] == END) {
                        maze[x][y] = VISITED;
                        updateMazeGUI();
                    } else if (maze[x][y] == START) {
                        solveMazeDFS(x, y);
                        timer.stop(); // Stop the timer after the maze is solved
                        solveButton.setEnabled(true);
                    }
                    step++;
                } else {
                    timer.stop(); // Stop the timer when all steps are completed
                    solveButton.setEnabled(true);
                }
            }
        });
        timer.start();
    }

    private void solveMazeDFS(int x, int y) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValid(newX, newY) && (maze[newX][newY] == PATH || maze[newX][newY] == END)) {
                maze[newX][newY] = VISITED;
                updateMazeGUI();
                solveMazeDFS(newX, newY);
            }
        }
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < MAZE_SIZE && y >= 0 && y < MAZE_SIZE && maze[x][y] != WALL && maze[x][y] != VISITED;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MazeSolverGUI mazeSolverGUI = new MazeSolverGUI();
                mazeSolverGUI.setVisible(true);
            }
        });
    }
}


                         