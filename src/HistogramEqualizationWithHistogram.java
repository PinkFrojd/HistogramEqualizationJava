import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import javax.imageio.ImageIO;

public class HistogramEqualizationWithHistogram {

    public HistogramEqualizationWithHistogram() {}

    public HistogramEqualizationWithHistogram(String in) {
        try {

            File f1 = new File(in);
            File f2 = new File("equalized_" + in);

            BufferedImage image1 = getGrayscaleImage(ImageIO.read(f1));
            BufferedImage image2 = equalize(image1);
            ImageIO.write(image2, "png", f2);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(2);
        }
    }

    public HistogramEqualizationWithHistogram(String in, String out){
        try {

            File f1 = new File(in);
            File f2 = new File(out);

            BufferedImage image1 = getGrayscaleImage(ImageIO.read(f1));
            BufferedImage image2 = equalize(image1);
            ImageIO.write(image2, "png", f2);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(2);
        }
    }

    private BufferedImage getGrayscaleImage(BufferedImage src) {

        BufferedImage gImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        WritableRaster wr = src.getRaster();
        WritableRaster gr = gImg.getRaster();

        for (int i = 0; i < wr.getWidth(); i++) {
            for (int j = 0; j < wr.getHeight(); j++) {
                gr.setSample(i,j,0, wr.getSample(i, j, 0));
            }
        }

        gImg.setData(gr);
        return gImg;

    }

    private BufferedImage equalize(BufferedImage src) {
        BufferedImage nImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        WritableRaster wr = src.getRaster();
        WritableRaster er = nImg.getRaster();
        int totpix = wr.getHeight() * wr.getWidth();
        int[] histogram = new int[256];


        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                histogram[wr.getSample(x, y, 0)]++;
            }
        }

        write_histogram(histogram);

        int[] chistogram = new int[256];
        chistogram[0] = histogram[0];
        for (int i = 1; i < 256; i++) {
            chistogram[i] = chistogram[i-1] + histogram[i];
        }

        float[] arr = new float[256];
        for (int i = 0; i < 256; i++) {
            arr[i] = (float) (chistogram[i] * 255.0 / (float)totpix);
        }

        write_histogram_cpf(arr);

        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                int nVal = (int) arr[wr.getSample(x,y,0)];
                er.setSample(x,y,0,nVal);
            }
        }

        nImg.setData(er);
        return nImg;
    }

    private void write_histogram(int[] arr){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("histogram.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println("color, frequency");
        for (int i = 0; i < 256; i++) {
            writer.println(i + ", " + arr[i]);
        }
        writer.close();
    }

    private void write_histogram_cpf(float[] arr){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("histogram_equalized.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println("color, frequency");
        for (int i = 0; i < 256; i++) {
            writer.println(i + ", " + (int) arr[i]);
        }
        writer.close();
    }

    public static void main(String[] args){

        HistogramEqualizationWithHistogram he;

        switch (args.length){
            case 0:
                System.out.println("\nHISTOGRAM EQUALIZATION");
                System.out.println("  Generates image with contrast adjustment using image's histogram");
                System.out.println("USAGE:");
                System.out.println("  java: HistogramEqualizationWithHistogram <input_image> <output_image>");
                System.out.println("  java: HistogramEqualizationWithHistogram <input_image>");
                he = new HistogramEqualizationWithHistogram();
                System.exit(1); // stdout
                break;
            case 1:
                he = new HistogramEqualizationWithHistogram(args[0]);
                break;
            case 2:
                he = new HistogramEqualizationWithHistogram(args[0], args[1]);
                break;
            default:
                System.out.println("Too much arguments...");
                System.out.println("ABORTING");
                System.exit(1); // stdout
                break;
        }
    }
}
