/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.util.Random;
/**HOW TO PLAY
Cursor to control movement
press d to fast drop
press space to instant drop
press p to pause*/
public class Shape {
//holds 7 tetris shape names and a empty shape called NoShape
    protected enum Tetrominoe { NoShape, ZShape, SShape, LineShape, 
               TShape, SquareShape, LShape, MirroredLShape };

    private Tetrominoe pieceShape;
    private int coords[][];
    private int[][][] coordsTable;


    public Shape() {
        
        initShape();
    }
    
    private void initShape() {
//holds the actual coordinates of a tetris piece
        coords = new int[4][2];
        setShape(Tetrominoe.NoShape);
    }

    protected void setShape(Tetrominoe shape) {
//coordstable holds all possible coordinate values of the tetris pieces
//each set of coordinates except the first set(empty shape) represent a tetrominoe
         coordsTable = new int[][][] {
            { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
            { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
            { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
            { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
            { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
            { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
            { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        };
//for loops to put one row of the coordinate values from the coordsTable into the coords arry of a tetris piece
        for (int i = 0; i < 4 ; i++) {
            
            for (int j = 0; j < 2; ++j) {
            //ordinal() method returns the current position of the enum type in the enum object    
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        
        pieceShape = shape;
    }

    private void setX(int index, int x) { coords[index][0] = x; }
    private void setY(int index, int y) { coords[index][1] = y; }
    public int x(int index) { return coords[index][0]; }
    public int y(int index) { return coords[index][1]; }
    public Tetrominoe getShape()  { return pieceShape; }

    public void setRandomShape() {
        //set shape to be random
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        //set Tetrominoe values to get shape
        Tetrominoe[] values = Tetrominoe.values(); 
        setShape(values[x]);
    }

    public int minX() {
        
      int m = coords[0][0];
      
      for (int i=0; i < 4; i++) {
          
          m = Math.min(m, coords[i][0]);
      }
      
      return m;
    }


    public int minY() {
        
      int m = coords[0][1];
      
      for (int i=0; i < 4; i++) {
          
          m = Math.min(m, coords[i][1]);
      }
      
      return m;
    }

    public Shape rotateLeft() {
//The square dont need to rotate        
        if (pieceShape == Tetrominoe.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;
//for loop to rotate left
        for (int i = 0; i < 4; ++i) {
            
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        
        return result;
    }

    public Shape rotateRight() {
        
        if (pieceShape == Tetrominoe.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {

            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        
        return result;
    }
}