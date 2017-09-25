

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MooseHighlighter {



    class Figure implements Comparable {
        int area;
        int anyX;
        int anyY;

        public Figure(int area, int anyX, int anyY) {
            this.area = area;
            this.anyX = anyX;
            this.anyY = anyY;
        }

        @Override
        public String toString() {
            return "Figure{" +
                    "area=" + area +
                    ", anyX=" + anyX +
                    ", anyY=" + anyY +
                    '}';
        }

        @Override
        public int compareTo(Object o) {
            return area - ((Figure) o).area;
        }
    }

    private BufferedImage img;
    private String output="result.png";

    private int areaCounter;
    private List<Figure> figures = null;

    private boolean[][] binaryMatrixOfMoosesPixels;
    private boolean[][] storyMatrix;

    private final int BLACK = -16777216;
    private final Color GREEN_COLOR = Color.green;
    private final Color RED_COLOR = Color.red;

    public MooseHighlighter(String path) {
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        figures = new ArrayList<Figure>();
        binaryMatrixOfMoosesPixels = new boolean[img.getWidth()][img.getHeight()];
        storyMatrix = new boolean[img.getWidth()][img.getHeight()];
        initMatrixes();
    }

    public void setOutput(String output) {
        this.output=output;
    }


    private void initMatrixes() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (img.getRGB(x, y) == BLACK) { //is black
                    binaryMatrixOfMoosesPixels[x][y] = true;
                } else binaryMatrixOfMoosesPixels[x][y] = false;
            }
        }
        initStoryMatrix();

    }

    private void initStoryMatrix() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                storyMatrix[x][y] = false;
            }
        }
    }

    public void searchMoose() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (binaryMatrixOfMoosesPixels[x][y]) {
                    if (figures.isEmpty()) {
                        resetCounter();
                        count(x, y);
                        figures.add(new Figure(areaCounter, x, y));
                    } else {
                        if (!storyMatrix[x][y]) {//point wasn't visited
                            resetCounter();
                            count(x, y);
                            figures.add(new Figure(areaCounter, x, y));
                        }
                    }
                }
            }
        }

    }

    private int maxColerationSearching() {
        int maxColeration = 0;
        int filter = 0;
        for (int i = 1; i < figures.size(); i++) {
            int temp = Math.abs(figures.get(i - 1).area - figures.get(i).area);
            if (maxColeration < temp) {
                maxColeration = temp;
                filter = figures.get(i - 1).area;
            }
        }
        return filter;
    }


    public void drawNewPicture() throws IOException {
        figures=figures.stream().sorted().collect(Collectors.toList());
        List<Figure> notMoose = figures.stream().sorted().filter(x -> x.area <= maxColerationSearching()).collect(Collectors.toList());
        List<Figure> moose = figures.stream().sorted().filter(x -> x.area > maxColerationSearching()).collect(Collectors.toList());
        initStoryMatrix();
        for (Figure figure : notMoose
                ) {

            drawFigure(RED_COLOR, figure.anyX, figure.anyY);
        }

        for (Figure figure : moose
                ) {
            drawFigure(GREEN_COLOR, figure.anyX, figure.anyY);
        }
        ImageIO.write(img, "PNG", new File(output));
    }


    private void drawFigure(Color color, int x, int y) {
        if (isValidPoint(x, y)) {//ArrayOutOfIndexException possible
            if (binaryMatrixOfMoosesPixels[x][y]) { //is black
                if (!storyMatrix[x][y]) {// and wasn't written at storyMatrix yet
                    if (isBoardPixel(x, y)) {// board point
                        img.setRGB(x, y, color.getRGB());
                        storyMatrix[x][y] = true;
                        drawFigure(color,x, y + 1);//up
                        drawFigure(color,x, y - 1);//down
                        drawFigure(color,x + 1, y);//right
                        drawFigure(color,x - 1, y);//left
                    }
                }
            }
        } else return;
    }

    private boolean isBoardPixel(int x, int y) {
        boolean result = false;
        if (isValidPoint(x, y)) {
            for (int xx = -1; xx < 2; xx++) {
                for (int yy = -1; yy < 2; yy++) {
                    if (isValidPoint(x + xx, y + yy)) {
                        if (!binaryMatrixOfMoosesPixels[x + xx][y + yy]) {//is not black
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean isValidPoint(int x, int y){
        boolean result=false;
        if(x>=0&&x<img.getWidth()&&y>=0&&y<img.getHeight()) {
            result =true;
        }
        return result;
    }

    //counting area
    private void count(int x, int y){
        if(isValidPoint(x,y)){//ArrayOutOfIndexException possible
            if(binaryMatrixOfMoosesPixels[x][y]) { //is black
                if (!storyMatrix[x][y]) { // and wasn't written at storyMatrix yet
                    areaCounter++;
                    storyMatrix[x][y] = true;
                    count(x,y+1);//up
                    count(x,y-1);//down
                    count(x+1,y);//right
                    count(x-1,y);//left

                }
            }
        }
        else return;
    }

    public void resetCounter(){
      areaCounter=0;
    }
}
