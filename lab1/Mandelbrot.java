import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;

import javax.imageio.ImageIO;

public class Mandelbrot {
    
    int width;
    int height;
    double[] xcorners;
    double[] ycorners;
    int maxIterations;
    double[] c_re;
    double[] c_im;


     // Funkcja działająca jak linspace w NumPy: generuje N równo oddalone punkty
    
     public static double[] linspace(double min, double max, int points) {  
        double[] d = new double[points];  
        for (int i = 0; i < points; i++){  
            d[i] = min + i * (max - min) / (points - 1);  
        }  
        return d;  
    } 

    public Mandelbrot(int w, int h){
        width = w;
        height = h;
        xcorners = new double[]{-2.1, 0.6};
        ycorners = new double[]{-1.2, 1.2};
        maxIterations = 200;

        c_re = linspace(xcorners[0], xcorners[1], width);
        c_im = linspace(ycorners[0], ycorners[1], height);
    }

    public int condition(double re, double im){
        Complex z_0 = new Complex(0, 0);
        Complex C = new Complex(re, im);
        Complex z_1;
        for(int i = 0; i < maxIterations; i++){
            z_1 = z_0.times(z_0).plus(C);
            if(z_1.abs() >= 2) return i;
            z_0 = z_1;
        }
        return maxIterations;
    }
   
    public static BufferedImage MandelbrotImg(Mandelbrot m){
        BufferedImage img = new BufferedImage(m.width, m.height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < m.width; i++) {
            for (int j = 0; j < m.height; j++) {
                Color color = new Color((m.maxIterations - m.condition(m.c_re[i], m.c_im[j]))%255, 0, (m.maxIterations + m.condition(m.c_re[i], m.c_im[j]))%255);
                img.setRGB(i, j, color.getRGB());
            }
        }
        return img;
    }
    // Funkcja uśredniająca czas wykonania
    public static double test_time(Mandelbrot m, int N){
        long time = 0;
        for (int i = 0; i < N; i++){
            long startTime = System.nanoTime();
            BufferedImage img_test = MandelbrotImg(m);
            long totalTime = System.nanoTime() - startTime;
            time += totalTime;
        }
        long avg_time = time/N;
        return (double) avg_time / 1_000_000_000;

    }


    public static void main(String[] args) throws IOException {
        int value = 32;
        Mandelbrot m = new Mandelbrot(value, value);
        BufferedImage img = MandelbrotImg(m);
        File f = new File("Mandelbrot" + value + ".png");
        ImageIO.write(img, "PNG", f);

        int pixels[] = {32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        
        ArrayList<Double> times = new ArrayList<>();

        for (int i:pixels){
            Mandelbrot test = new Mandelbrot(i, i);
            double t = test_time(test, 20);
            times.add(t);
        }

        FileWriter writer = new FileWriter("output.txt"); 
        for(Double s: times) {
            writer.write(s + System.lineSeparator());
        }
        writer.close();
    }
}