import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class MandelJob implements Runnable{

    Mandelbrot m;
    static int processors = Runtime.getRuntime().availableProcessors();
    int pixel;
   
    double xstart = -2.1;
    double xstop = 0.6;
    double ystart = -1.2;
    double ystop = 1.2;

    double[] x = Mandelbrot.linspace(xstart, xstop, processors - 1);
    double[] y = Mandelbrot.linspace(ystart, ystop, processors - 1);



    public MandelJob(int pixel){
        this.pixel = pixel;
    }

    public void run() {
        for (int i = 0; i < (processors / 2); i++) {
                for (int j = 0; j < (processors / 2); j++) {
                    final int k = i;
                    final int l = j;
                    this.m = new Mandelbrot(pixel / processors, pixel / processors, x[k], x[k+1], y[l], y[l+1]);
                    Mandelbrot.MandelbrotImg(this.m);
                }
            }
    }

    public Mandelbrot result(){
        return this.m;
    }


}
public class MandelbrotPool {
    static int processors, val;
    static double xstart, xstop, ystart, ystop;
    static double[] x,y;
    static ExecutorService ex;

public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {

    int[] pixels = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
    // int[] pixels = {16, 32};
    ArrayList<Double> times = new ArrayList<>();
    int iterations = 10;

    processors = Runtime.getRuntime().availableProcessors();

    
    ex = Executors.newFixedThreadPool(processors);
    // ExecutorService ex;
    LinkedList<Future<MandelJob>> jobs = new LinkedList<>();
    MandelJob job;

    for (int p:pixels){
        long startTime = System.nanoTime();
        
        for (int n = 0; n < iterations; n++) {

            // ex = Executors.newFixedThreadPool(processors);
            job = new MandelJob(p); 
            jobs.add((Future<MandelJob>) ex.submit(job));

        }
        for(var res : jobs){
            res.get();
        }
        
        long totalTime = System.nanoTime() - startTime;
        long time = totalTime/iterations;
        times.add((double) time / 1_000_000_000);
        
    }
    

    FileWriter writer = new FileWriter("output-pools-new01-01.txt"); 
        for(Double s: times) {
            writer.write(s + System.lineSeparator());
        }
        writer.close();
    
        
        System.out.println("Process finished");
        ex.shutdown();
        ex.awaitTermination(10, TimeUnit.SECONDS);
    }
   
}
