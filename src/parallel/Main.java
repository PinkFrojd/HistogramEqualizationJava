package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException{

        long t1, t2;
        t1 = System.nanoTime();
        int[] histogram = new int[256];
        int[] chistogram = new int[256];
        float[] arr = new float[256];

        // ./images/slika.jpg Resource folder
        File file_to_be_equalized = new File(args[0]); // Command line argument ALT+SHIFT+F10 -> EDIT -> Program  args

        // Image to to saved as equalized
        File equalized_file = new File("./images/EQUALIZED.jpg");

        // Common resource data
        ImageData image = new ImageData(ImageIO.read(file_to_be_equalized));
        WritableRaster w = image.image.getRaster();
        WritableRaster eq = image.image.getRaster();

        int total_pixels = w.getHeight() * w.getWidth();

        RunnableImage R1 = new RunnableImage("Thread-1", w, histogram);
        R1.setPocetak(0);
        R1.setKraj(w.getWidth() / 2);

        RunnableImage R2 = new RunnableImage("Thread-2", w, histogram);
        R2.setPocetak(w.getWidth() / 2);
        R2.setKraj(w.getWidth());

        R1.start();
        R2.start();

        try {
            R1.t.join();
            R2.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        equalize(histogram, chistogram, arr, total_pixels);

        for (int i = 0; i < eq.getWidth(); i++) {
            for (int j = 0; j < eq.getHeight(); j++) {
                int nVal = (int) arr[eq.getSample(i,j,0)];
                eq.setSample(i,j,0,nVal);
            }
        }

        t2 = System.nanoTime();

        System.out.println((t2 - t1) / 1000000000.0 + " s");

        BufferedImage eq_i = new BufferedImage(eq.getWidth(), eq.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        eq_i.setData(eq);
        ImageIO.write(eq_i, "png", equalized_file);
    }

    private static void equalize( int[] histogram, int[] chistogram, float[] arr, int totpix ) {

        chistogram[0] = histogram[0];

        for (int i = 1; i < 256; i++) {
                chistogram[i] = chistogram[i-1] + histogram[i];
        }

        for (int i = 0; i < 256; i++) {
            arr[i] = (float) (chistogram[i] * 255.0 / (float)totpix);
        }

    }


}
