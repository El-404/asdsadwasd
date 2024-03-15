package tetris;

import java.awt.*;

final class Config {

  static final Color[] colors = {
    Color.green,
    Color.red,
    Color.blue,
    Color.pink,
    Color.orange,
    Color.cyan,
    Color.magenta,
    Color.BLACK,
    new Color(0,128,128),
    new Color(255, 0, 0),
    new Color(0, 255, 0)
  };

  static final Font mainFont = new Font("Monospaced", Font.BOLD, 48);
  static final Font smallFont = mainFont.deriveFont(Font.BOLD, 18);

  static final Dimension dim = new Dimension(640, 640);

  static final Rectangle gridRect = new Rectangle(46, 47, 308, 517);
  static final Rectangle previewRect = new Rectangle(387, 47, 200, 200);
  static final Rectangle titleRect = new Rectangle(100, 85, 252, 100);
  static final Rectangle clickRect = new Rectangle(50, 375, 252, 40);

  static final int blockSize = 30;
  static final int nRows = 18;
  static final int nCols = 12;
  static final int topMargin = 50;
  static final int leftMargin = 20;
  static final int scoreX = 400;
  static final int scoreY = 330;
  static final int titleX = 130;
  static final int titleY = 150;
  static final int clickX = 120;
  static final int clickY = 400;
  static final int previewCenterX = 467;
  static final int previewCenterY = 97;

  static final Stroke largeStroke = new BasicStroke(5);
  static final Stroke smallStroke = new BasicStroke(2);

  static final Color squareBorder = Color.white;
  static final Color titlebgColor = Color.white;
  static final Color textColor = Color.black;
  static final Color bgColor = new Color(0xDDEEFF);
  static final Color gridColor = new Color(0xBECFEA);
  static final Color gridBorderColor = new Color(0x7788AA);
}
