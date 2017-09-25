import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
            String path = "moose.png";
            MooseHighlighter mooseHighlighter = new MooseHighlighter(path);
            mooseHighlighter.setOutput("result1.png");
            mooseHighlighter.searchMoose();
            mooseHighlighter.drawNewPicture();

            path = "moose2.png";
            mooseHighlighter = new MooseHighlighter(path);
            mooseHighlighter.setOutput("result2.png");
            mooseHighlighter.searchMoose();
            mooseHighlighter.drawNewPicture();
            System.out.println("Result.png should apear");
    }
}
