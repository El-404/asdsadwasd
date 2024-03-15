package tetris;

enum Shape {
  ZShape(new int[][] { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }),
  SShape(new int[][] { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }),
  IShape(new int[][] { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }),
  TShape(new int[][] { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }),
  Square(new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }),
  LShape(new int[][] { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }),
  JShape(new int[][] { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }),
  RShape(new int[][] { { 0, 0 }, { -1, 0 }, { -1, -1 }, { 1, -1 } }),
  iShape(new int[][] { { -2, 0 }, { -1, 0 }, { 0, 0 }, { 2, 0 } }),
  jShape(new int[][] { { -1, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 } }),
  dShape(new int[][] { { -1, -1 }, { -1, 1 }, { 1, 1 }, { 1, -1 } });

  private Shape(int[][] shape) {
    this.shape = shape;
    pos = new int[4][2];
    reset();
  }

  void reset() {
    for (int i = 0; i < pos.length; i++) {
      pos[i] = shape[i].clone();
    }
  }

  final int[][] pos, shape;
}
