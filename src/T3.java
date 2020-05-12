import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class T3 extends JFrame {

    //Driver method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                new T3();
            }
        });
    }
    
    public static final int ROWS = 3;  
    public static final int COLS = 3;

    public static final int CELL_SIZE = 100; // cell width and height (square)
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 8;                   // Grid-line's width
    public static final int GRID_WIDHT_HALF = GRID_WIDTH / 2; // Grid-line's half-width
    
    public static final int CELL_PADDING = CELL_SIZE / 6;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
    public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width

    // Use an enumeration (inner class) to represent the various states of the game
    public enum Status {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }
    private Status current;  // the current game state

    // Use an enumeration (inner class) to represent the Points and cell contents
    public enum Point {
        EMPTY, CROSS, NOUGHT
    }
    private Point Player;  // the current player

    private Point[][] board   ; // Game board of ROWS-by-COLS cells
    private Draw canvas; // Drawing canvas (JPanel) for the game board
    private JLabel bar;  // Status Bar

    
    public T3() {
        canvas = new Draw();  // Construct a drawing canvas (a JPanel)
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // The canvas (JPanel) fires a MouseEvent upon mouse-click
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int x = e.getX();
                int y = e.getY();
                // Get the row and column clicked
                int rowSelected = y / CELL_SIZE;
                int colSelected = x / CELL_SIZE;

                if (current == Status.PLAYING) {
                    if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                            && colSelected < COLS && board[rowSelected][colSelected] == Point.EMPTY) {
                        board[rowSelected][colSelected] = Player; // Make a move
                        Game(Player, rowSelected, colSelected); // update state
                        // Switch player
                        Player = (Player == Point.CROSS) ? Point.NOUGHT : Point.CROSS;
                    }
                } else {       // game over
                    Reset(); // restart the game
                }
                // Refresh the drawing canvas
                repaint();  // Call-back paintComponent().
            }
        });

        // Setup the status bar (JLabel) to display status message
        bar = new JLabel("  ");
        bar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        bar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(bar, BorderLayout.PAGE_END); // same as SOUTH

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // pack all the components in this JFrame
        setTitle("Tic Tac Toe");
        setVisible(true);  // show this JFrame

        board = new Point[ROWS][COLS]; // allocate array
        Reset(); // initialize the game board contents and game variables
    }

    /** Initialize the game-board contents and the status */
    public void Reset() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = Point.EMPTY; // all cells empty
            }
        }
        current = Status.PLAYING; // ready to play
        Player = Point.CROSS;       // cross plays first
    }

    /** Update the current after the player with "thePoint" has placed on
     (rowSelected, colSelected). */
    public void Game(Point thePoint, int rowSelected, int colSelected) {
        if (Win(thePoint, rowSelected, colSelected)) {  // check for win
            current = (thePoint == Point.CROSS) ? Status.CROSS_WON : Status.NOUGHT_WON;
        } else if (Tie()) {  // check for draw
            current = Status.DRAW;
        }
        // Otherwise, no change to current state (still Status.PLAYING).
    }

    /** Return true if it is a draw (i.e., no more empty cell) */
    public boolean Tie() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (board[row][col] == Point.EMPTY) {
                    return false; // an empty cell found, not draw, exit
                }
            }
        }
        return true;  // no more empty cell, it's a draw
    }

    /** Return true if the player with "thePoint" has won after placing at
     (rowSelected, colSelected) */
    public boolean Win(Point thePoint, int rowSelected, int colSelected) {
        return (board[rowSelected][0] == thePoint  // 3-in-the-row
                && board[rowSelected][1] == thePoint
                && board[rowSelected][2] == thePoint
                || board[0][colSelected] == thePoint      // 3-in-the-column
                && board[1][colSelected] == thePoint
                && board[2][colSelected] == thePoint
                || rowSelected == colSelected            // 3-in-the-diagonal
                && board[0][0] == thePoint
                && board[1][1] == thePoint
                && board[2][2] == thePoint
                || rowSelected + colSelected == 2  // 3-in-the-opposite-diagonal
                && board[0][2] == thePoint
                && board[1][1] == thePoint
                && board[2][0] == thePoint);
    }

    /**
     *  Inner class Draw (extends JPanel) used for custom graphics drawing.
     */
    class Draw extends JPanel {
        @Override
        public void paintComponent(Graphics g) {  // invoke via repaint()
            super.paintComponent(g);    // fill background
            setBackground(Color.WHITE); // set its background color

            // Draw the grid-lines
            g.setColor(Color.LIGHT_GRAY);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDHT_HALF,
                        CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDHT_HALF, 0,
                        GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }

            // Draw the Points of all the cells if they are not empty
            // Use Graphics2D which allows us to set the pen's stroke
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));  // Graphics2D only
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board[row][col] == Point.CROSS) {
                        g2d.setColor(Color.RED);
                        int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                        int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                        g2d.drawLine(x1, y1, x2, y2);
                        g2d.drawLine(x2, y1, x1, y2);
                    } else if (board[row][col] == Point.NOUGHT) {
                        g2d.setColor(Color.BLUE);
                        g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                    }
                }
            }

            // Print status-bar message
            if (current == Status.PLAYING) {
                bar.setForeground(Color.BLACK);
                if (Player == Point.CROSS) {
                    bar.setText("Red's Turn");
                } else {
                    bar.setText("Blue's Turn");
                }
            } else if (current == Status.DRAW) {
                bar.setForeground(Color.RED);
                bar.setText("It's a Stalemate! Click to play again.");
            } else if (current == Status.CROSS_WON) {
                bar.setForeground(Color.RED);
                bar.setText("'Red' Won! Click to play again.");
            } else if (current == Status.NOUGHT_WON) {
                bar.setForeground(Color.RED);
                bar.setText("'Blue' Won! Click to play again.");
            }
        }
    }

    
}