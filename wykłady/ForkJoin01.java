package WdPRiR.wykłady;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ForkJoin01 {
    public static ExecutorService p = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static class FindMaxCallable implements Callable<Double> {
        private ArrayList<Double> array;
        private int start;
        private int end;

        public FindMaxCallable(ArrayList<Double> array, int start, int end){
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public Double call() throws Exception {
            double max = Double.MIN_VALUE;
            for (int i = start; i < end; i++) {
                max = array.get(i) > max ? array.get(i) : max;
            }
            return max;
        }

    }

    public static double findMaxPool(ArrayList<Double> array) throws ExecutionException, InterruptedException{
        
        int processors = Runtime.getRuntime().availableProcessors();
        int chunk = array.size() / processors;
        if(array.size() < processors) {
            processors = 1;
            chunk = array.size();
        }

        LinkedList<Future<Double>> results = new LinkedList();
        for(int i = 0; i < processors; ++i){
            results.add(p.submit(new FindMaxCallable(array, i* chunk, i == processors - 1 ? array.size() : (i + 1) * chunk)));
        }
        double max = Double.MIN_VALUE;
        for(var future : results){
            double result = future.get();
            max = result > max ? result : max;

        }
        return max;

    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Random random = new Random();
        ArrayList<Double> array = new ArrayList<>();
        for (int i = 0; i < 1 << 5; i++) {
            array.add(random.nextDouble());
        }
        Collections.sort(array);

        for(var e : array){
            System.out.println(e);
        }
        System.out.println("LOL " + findMaxPool(array));
        p.shutdown();
        p.awaitTermination(1, TimeUnit.DAYS);

    }
}
