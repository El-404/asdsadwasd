package tetris;

import static java.lang.Math.*;
import static java.lang.String.format;
import static tetris.Config.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Tetris extends JPanel implements Runnable {

  enum Dir {
    right(1, 0),
    down(0, 1),
    left(-1, 0);

    Dir(int x, int y) {
      this.x = x;
      this.y = y;
    }

    final int x, y;
  }

  public static final int EMPTY = -1;
  public static final int BORDER = -2;

  Shape fallingShape;
  Shape nextShape;

  // position of falling shape
  int fallingShapeRow;
  int fallingShapeCol;

  final int[][] grid = new int[nRows][nCols];

  Thread fallingThread;
  final Scoreboard scoreboard = new Scoreboard();
  static final Random rand = new Random();

  public Tetris() {
    setPreferredSize(dim);
    setBackground(bgColor);
    setFocusable(true);

    initGrid();
    selectShape();

    addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          if (scoreboard.isGameOver()) {
            startNewGame();
            repaint();
          }
        }
      }
    );

    addKeyListener(
      new KeyAdapter() {
        boolean fastDown;

        @Override
        public void keyPressed(KeyEvent e) {
          if (scoreboard.isGameOver()) return;

          switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
              if (canRotate(fallingShape)) rotate(fallingShape);
              break;
            case KeyEvent.VK_LEFT:
              if (canMove(fallingShape, Dir.left)) move(Dir.left);
              break;
            case KeyEvent.VK_RIGHT:
              if (canMove(fallingShape, Dir.right)) move(Dir.right);
              break;
            case KeyEvent.VK_DOWN:
                if(canMove(fallingShape, Dir.down)) move(Dir.down);
                break;
            case KeyEvent.VK_SPACE:
              if (!fastDown) {
                fastDown = true;
                while (canMove(fallingShape, Dir.down)) {
                  move(Dir.down);
                  repaint();
                }
                shapeHasLanded();
              }
              break;
              case KeyEvent.VK_R:
                scoreboard.setGameOver();
                startNewGame();
                break;
              case KeyEvent.VK_C:
                shapeHasLanded();
                break;
          }
          repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
          fastDown = false;
        }
      }
    );
  }

  void selectShape() {
    fallingShapeRow = 1;
    fallingShapeCol = 5;
    fallingShape = nextShape;
    Shape[] shapes = Shape.values();
    nextShape = shapes[rand.nextInt(shapes.length)];
    if (fallingShape != null) fallingShape.reset();
  }

  void startNewGame() {
    stop();
    initGrid();
    selectShape();
    scoreboard.reset();
    (fallingThread = new Thread(this)).start();
  }

  void stop() {
    if (fallingThread != null) {
      Thread tmp = fallingThread;
      fallingThread = null;
      tmp.interrupt();
    }
  }

  void initGrid() {
    for (int r = 0; r < nRows; r++) {
      Arrays.fill(grid[r], EMPTY);
      for (int c = 0; c < nCols; c++) {
        if (c == 0 || c == nCols - 1 || r == nRows - 1) grid[r][c] = BORDER;
      }
    }
  }

  @Override
  public void run() {
    while (Thread.currentThread() == fallingThread) {
      try {
        Thread.sleep(scoreboard.getSpeed());
      } catch (InterruptedException e) {
        return;
      }

      if (!scoreboard.isGameOver()) {
        if (canMove(fallingShape, Dir.down)) {
          move(Dir.down);
        } else {
          shapeHasLanded();
        }
        repaint();
      }
    }
  }

  void drawStartScreen(Graphics2D g) {
    g.setFont(mainFont);

    g.setColor(titlebgColor);
    g.fill(titleRect);
    g.fill(clickRect);

    g.setColor(textColor);
    g.drawString("Tetris", titleX, titleY);

    g.setFont(smallFont);
    g.drawString("click to start", clickX, clickY);
  }

  void drawSquare(Graphics2D g, int colorIndex, int r, int c) {
    g.setColor(colors[colorIndex]);
    g.fillRect(
      leftMargin + c * blockSize,
      topMargin + r * blockSize,
      blockSize,
      blockSize
    );

    g.setStroke(smallStroke);
    g.setColor(squareBorder);
    g.drawRect(
      leftMargin + c * blockSize,
      topMargin + r * blockSize,
      blockSize,
      blockSize
    );
  }

  void drawUI(Graphics2D g) {
    // grid background
    g.setColor(gridColor);
    g.fill(gridRect);

    // the blocks dropped in the grid
    for (int r = 0; r < nRows; r++) {
      for (int c = 0; c < nCols; c++) {
        int idx = grid[r][c];
        if (idx > EMPTY) drawSquare(g, idx, r, c);
      }
    }

    // the borders of grid and preview panel
    g.setStroke(largeStroke);
    g.setColor(gridBorderColor);
    g.draw(gridRect);
    g.draw(previewRect);

    // scoreboard
    int x = scoreX;
    int y = scoreY;
    g.setColor(textColor);
    g.setFont(smallFont);
    g.drawString(format("hiscore  %6d", scoreboard.getTopscore()), x, y);
    g.drawString(format("level    %6d", scoreboard.getLevel()), x, y + 30);
    g.drawString(format("lines    %6d", scoreboard.getLines()), x, y + 60);
    g.drawString(format("score    %6d", scoreboard.getScore()), x, y + 90);

    // preview
    int minX = 5, minY = 5, maxX = 0, maxY = 0;
    for (int[] p : nextShape.pos) {
      minX = min(minX, p[0]);
      minY = min(minY, p[1]);
      maxX = max(maxX, p[0]);
      maxY = max(maxY, p[1]);
    }
    double cx = previewCenterX - ((minX + maxX + 1) / 2.0 * blockSize);
    double cy = previewCenterY - ((minY + maxY + 1) / 2.0 * blockSize);

    g.translate(cx, cy);
    for (int[] p : nextShape.shape) drawSquare(
      g,
      nextShape.ordinal(),
      p[1],
      p[0]
    );
    g.translate(-cx, -cy);
  }

  void drawFallingShape(Graphics2D g) {
    int idx = fallingShape.ordinal();
    for (int[] p : fallingShape.pos) drawSquare(
      g,
      idx,
      fallingShapeRow + p[1],
      fallingShapeCol + p[0]
    );
  }

  @Override
  public void paintComponent(Graphics gg) {
    super.paintComponent(gg);
    Graphics2D g = (Graphics2D) gg;
    g.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    drawUI(g);

    if (scoreboard.isGameOver()) {
      drawStartScreen(g);
    } else {
      drawFallingShape(g);
    }
  }

  boolean canRotate(Shape s) {
    if (s == Shape.Square) return false;

    int[][] pos = new int[4][2];
    for (int i = 0; i < pos.length; i++) {
      pos[i] = s.pos[i].clone();
    }

    for (int[] row : pos) {
      int tmp = row[0];
      row[0] = row[1];
      row[1] = -tmp;
    }

    for (int[] p : pos) {
      int newCol = fallingShapeCol + p[0];
      int newRow = fallingShapeRow + p[1];
      if (grid[newRow][newCol] != EMPTY) {
        return false;
      }
    }
    return true;
  }

  void rotate(Shape s) {
    if (s == Shape.Square) return;

    for (int[] row : s.pos) {
      int tmp = row[0];
      row[0] = row[1];
      row[1] = -tmp;
    }
  }

  void move(Dir dir) {
    fallingShapeRow += dir.y;
    fallingShapeCol += dir.x;
  }

  boolean canMove(Shape s, Dir dir) {
    for (int[] p : s.pos) {
      int newCol = fallingShapeCol + dir.x + p[0];
      int newRow = fallingShapeRow + dir.y + p[1];
      if (grid[newRow][newCol] != EMPTY) return false;
    }
    return true;
  }

  void shapeHasLanded() {
    addShape(fallingShape);
    if (fallingShapeRow < 2) {
      scoreboard.setGameOver();
      scoreboard.setTopscore();
      stop();
    } else {
      scoreboard.addLines(removeLines());
    }
    selectShape();
  }

  int removeLines() {
    int count = 0;
    for (int r = 0; r < nRows - 1; r++) {
      for (int c = 1; c < nCols - 1; c++) {
        if (grid[r][c] == EMPTY) break;
        if (c == nCols - 2) {
          count++;
          removeLine(r);
        }
      }
    }
    return count;
  }

  void removeLine(int line) {
    for (int c = 0; c < nCols; c++) grid[line][c] = EMPTY;

    for (int c = 0; c < nCols; c++) {
      for (int r = line; r > 0; r--) grid[r][c] = grid[r - 1][c];
    }
  }

  void addShape(Shape s) {
    for (int[] p : s.pos) grid[fallingShapeRow + p[1]][fallingShapeCol + p[0]] =
      s.ordinal();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setTitle("Tetris");
      f.setResizable(false);
      f.add(new Tetris(), BorderLayout.CENTER);
      f.pack();
      f.setLocationRelativeTo(null);
      f.setVisible(true);
    });
  }
}
