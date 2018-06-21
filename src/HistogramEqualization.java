import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HistogramEqualization {

    public HistogramEqualization() {}

    /**
     * @
     * @param in Accepts path of image as argument from CLI
     */
    public HistogramEqualization(String in) {
        try {

            /**
             * Input image to be equalized
             */
            File f1 = new File(in);

            /**
             * Output image with prefix equalized
             */
            File f2 = new File("equalized_" + in);

            /**
             * image1 is BufferedImage. It means that image is buffer of image data(temporary memory)
             * getGrayscaleImage accepts as argument BufferedImage
             */
            BufferedImage image1 = getGrayscaleImage(ImageIO.read(f1));

            /**
             * image2 is also new image returned from equalize method
             */
            BufferedImage image2 = equalize(image1);

            /**
             * We write temporary buffer image data from image2, with format .png
             * (Better alternative than GIF or JPG for high colour lossless images)
             * and store it in f2 path provided or calculated automatically
             */
            ImageIO.write(image2, "png", f2);

        } catch (IOException e) { // Exception to be caught
            System.out.println(e.getMessage());
            System.exit(2); // stderr
        }
    }

    public HistogramEqualization(String in, String out){
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

    /**
     *
     * @param src BufferedImage to be processed
     * @return New BufferImage calculated from src temporary image
     */
    private BufferedImage getGrayscaleImage(BufferedImage src) {

        /**
         * Constructs new image from src (input) image with width, height, and type of Byte_Gray
         * BufferedImage.TYPE_BYTE_GRAY means that every pixel will be represented with one byte
         * ranging from 0 to 255 (2^0 to 2^8)
         */
        BufferedImage gImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        /**
         * We can't directly write to BufferedImage so we construct WritableRaster
         * to provide pixel writing capabilities
         */
        WritableRaster wr = src.getRaster();
        WritableRaster gr = gImg.getRaster();

        /**
         * Loop through matrix of pixels
         */
        for (int i = 0; i < wr.getWidth(); i++) {
            for (int j = 0; j < wr.getHeight(); j++) {
                /**
                 * We set pixel of WritableRaster matrix with row and column (i, j)
                 * Third argument, b, is band. that is to set area to that color of pixel
                 * Last argument is pixel value we retrive from src image at (i, j) position
                 */
                gr.setSample(i,j,0, wr.getSample(i, j, 0));
            }
        }

        /**
         * We write matrix of WritableRaster gr to BufferedImage gImg and return it
         */
        gImg.setData(gr);
        return gImg;

    }

    private BufferedImage equalize(BufferedImage src) {

        /**
         * Same as before, we construct new data buffer of image src
         */
        BufferedImage nImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        /**
         * To manipulate pixels
         */
        WritableRaster wr = src.getRaster();
        WritableRaster er = nImg.getRaster();

        /**
         *
         */
        int totpix = wr.getHeight() * wr.getWidth();

        /**
         * Since there are 256 colors of grayscale image, we construct new array of pixel colors
         */
        int[] histogram = new int[256];

        /**
         * Loop through WritableRaster matrix and store each pixel value in array and increment by 1.
         * With this we get array that holds frequency of each pixel value.
         * 0 -> 5
         * 1 -> 11
         * 2 -> 55
         * 40 -> 123
         */
        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                histogram[wr.getSample(x, y, 0)]++;
            }
        }

        /**
         * Loop through array of frequencies of colors and calculate cumulative array
         */
        int[] chistogram = new int[256];
        chistogram[0] = histogram[0];
        for (int i = 1; i < 256; i++) {
            chistogram[i] = chistogram[i-1] + histogram[i];
        }

        /**
         * Final loop calculates cumulative distributive function.
         * We take each Gray level value, for example 110, calculate it's cumulative distributive function,
         * and multiplay by levels of gray - 1 (256 levels - 1)
         * array will start with something close to 0, and go up to 1.
         */
        float[] arr = new float[256];
        for (int i = 0; i < 256; i++) {
            arr[i] = (float) (chistogram[i] * 255.0 / (float)totpix);
        }

        /**
         * We map New Gray level values into old Gray Level values
         */
        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                // We cast from float to int beucase Gray level value must be int values from 0 - 255
                int nVal = (int) arr[wr.getSample(x,y,0)];
                er.setSample(x,y,0,nVal);
            }
        }

        /**
         * Write to BufferedImage and return
         */
        nImg.setData(er);
        return nImg;
    }

    public static void main(String[] args){

        HistogramEqualization he;

        /**
         * startTime, java internal calculation of nanoseconds
         */
        long startTime = System.nanoTime();
        long endTime, totalTime;
        switch (args.length){
            case 0:
                System.out.println("\nHISTOGRAM EQUALIZATION");
                System.out.println("  Generates image with contrast adjustment using image's histogram");
                System.out.println("USAGE:");
                System.out.println("  java: HistogramEqualizationWithHistogram <input_image> <output_image>");
                System.out.println("  java: HistogramEqualizationWithHistogram <input_image>");
                he = new HistogramEqualization();
                System.exit(1); // stdout
                break;
            case 1:
                he = new HistogramEqualization(args[0]);
                break;
            case 2:
                he = new HistogramEqualization(args[0], args[1]);
                break;
            default:
                System.out.println("Too much arguments...");
                System.out.println("ABORTING");
                System.exit(1); // stdout
                break;
        }
        endTime = System.nanoTime();
        totalTime = endTime - startTime;
        System.out.println("Total runtime of program: " + totalTime / 1000000000.0 + " seconds");

    }
}
