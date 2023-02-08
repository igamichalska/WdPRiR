# https://code.activestate.com/recipes/577680-multi-threaded-mandelbrot-fractal/

import threading
import os
from PIL import Image
import time
import matplotlib.pyplot as plt
import pandas as pd

iterations = 200
xstart = -2.1
xstop = 0.6 
ystart = -1.2 
ystop = 1.2 

threads = os.cpu_count()

class MandelbrotThread(threading.Thread):
    def __init__(self, k, width, height, wh):
        self.k = k
        self.width = width
        self.height = height
        self.wh = wh
        threading.Thread.__init__(self)

    def run(self):
        for i in range(k, wh, threads):
            kx = i % width
            ky = int(i / width)
            a = xstart + (xstop - xstart) * kx / (width - 1.0)
            b = ystart + (ystop - ystart) * ky / (height - 1.0)
            x = a
            y = b
            for kc in range(iterations):
                x0 = x * x - y * y + a
                y = 2.0 * x * y + b
                x = x0                
                if x * x + y * y > 4:
                    # colors
                    red = kc
                    green = (kc % 8) * 32
                    blue = (kc % 16) * 16
                    
                    image.putpixel((kx, ky), (red, green, blue))
                    break

if __name__ == "__main__":
    times = []
    pixels = [16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192]
    for p in pixels:
        width = p
        height = p
        wh = width * height
        image = Image.new("RGB", (width, height))
        time_start = time.time()
        for i in range(10):
            tArr = []
            for k in range(threads): # create all threads
                tArr.append(MandelbrotThread(k, width, height, wh))
            for k in range(threads): # start all threads
                tArr[k].start()
            for k in range(threads): # wait until all threads finished
                tArr[k].join()
        time_stop = time.time() - time_start
        times.append(time_stop / 10)
    df = pd.DataFrame(times, columns = ['times'])
    df.to_csv('python-threads.csv', index = False)

    file = open("java-threads.txt", "r")
    java_threads = []
    for line in file:
        java_threads.append(float(line.strip('\n')))
    plt.scatter(pixels, java_threads, label = 'Java')
    plt.scatter(pixels, times, label = 'Python')
    plt.loglog()
    plt.legend()
    plt.savefig('plot.pdf')