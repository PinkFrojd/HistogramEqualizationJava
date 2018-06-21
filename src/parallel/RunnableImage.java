package com.company;

import java.awt.image.WritableRaster;
import java.util.Arrays;

public class RunnableImage implements Runnable{
    public Thread t;
    private String threadName;
    private WritableRaster wr;
    private int pocetak, kraj;
    private int[] histogram;

    public RunnableImage(String name, WritableRaster wr, int[] histogram) {
        threadName = name;
        System.out.println("Creating " + threadName);
        this.wr = wr;
        this.histogram = histogram;
    }

    public void setPocetak(int pocetak) {
        this.pocetak = pocetak;
    }

    public void setKraj(int kraj) {
        this.kraj = kraj;
    }

    public void run() {
        System.out.println("Running " + threadName);

        for (int i = pocetak; i < kraj; i++) {
            for (int j = 0; j < wr.getHeight(); j++) {
                histogram[wr.getSample(i,j,0)]++;
            }
        }

        System.out.println("Thread " + threadName + " exiting");

    }

    public void start(){
        System.out.println("Starting " + threadName);

        if(t == null){
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
