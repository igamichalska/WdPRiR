import java.awt.image.BufferedImage;
import java.awt.Color;

// Zmodyfikowana wersja Mandelbrot z lab1

public class Mandelbrot {
    
    
    int width;
    int height;

    int maxIterations;
    double[] c_re;
    double[] c_im;

    double xcornerstart, xcornerstop, ycornerstart, ycornerstop;


     // Funkcja działająca jak linspace w NumPy: generuje N równo oddalone punkty
    
     public static double[] linspace(double min, double max, int points) {  
        double[] d = new double[points];  
        for (int i = 0; i < points; i++){  
            d[i] = min + i * (max - min) / (points - 1);  
        }  
        return d;  
    } 

    public Mandelbrot(int w, int h, double xcorstart, double xcorstop, double ycorstart, double ycorstop){
        width = w;
        height = h;
        maxIterations = 200;

        xcornerstart = xcorstart;
        xcornerstop = xcorstop;
        ycornerstart = ycorstart;
        ycornerstop = ycorstop;
        c_re = linspace(xcornerstart, xcornerstop, width);
        c_im = linspace(ycornerstart, ycornerstop, height);
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
            MandelbrotImg(m);
            long totalTime = System.nanoTime() - startTime;
            time += totalTime;
        }
        long avg_time = time/N;
        return (double) avg_time / 1_000_000_000;

    }

}