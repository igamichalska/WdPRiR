import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class MandelbrotThreads extends Thread{

    static int processors, val;
    static double xstart, xstop, ystart, ystop;
    double[] x, y;
    double x_1, x_2, y_1, y_2;

    public MandelbrotThreads(double xstart, double xstop, double ystart, double ystop, int processors, int i, int j, int p){
        x = Mandelbrot.linspace(xstart, xstop, processors - 1);
        y = Mandelbrot.linspace(ystart, ystop, processors - 1);
        x_1 = x[i];
        x_2 = x[i + 1];
        y_1 = y[j];
        y_2 = y[j + 1];
        val = p;
    }

    public void run() {
        Mandelbrot test = new Mandelbrot(val, val, xstart, xstop, ystart, ystop);
        Mandelbrot.MandelbrotImg(test);
    }
    

    public static void main(String[] args) throws IOException, InterruptedException {
    
        processors = Runtime.getRuntime().availableProcessors(); // u mnie 4
        int iterations = 10;
        xstart = -2.1;
        xstop = 0.6;
        ystart = -1.2;
        ystop = 1.2;

        int[] pixels = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        ArrayList<Double> times = new ArrayList<>();
        

        for (int p:pixels){
            long startTime = System.nanoTime();
            for (int n = 0; n < iterations; n++) {
                for (int j = 0; j < (processors / 2); j++) {
                    for (int i = 0; i < (processors / 2); i++) {
                        MandelbrotThreads t = new MandelbrotThreads(xstart, xstop, ystart, ystop, processors, i, j, p/processors);
                        t.start();
                        t.join();
                    }        
                }
            }
            long totalTime = System.nanoTime() - startTime;
            long time = totalTime/iterations;
            times.add((double) time / 1_000_000_000);

        }

        FileWriter writer = new FileWriter("output-threads.txt"); 
        for(Double s: times) {
            writer.write(s + System.lineSeparator());
        }
        writer.close();
     }
}