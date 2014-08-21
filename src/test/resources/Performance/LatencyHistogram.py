# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
import numpy as np

def read_csv(csv_path):
    retval = []
    with open(csv_path, "rt") as f:
        f.readline()
        line = f.readline().rstrip("\n")
        while line != "":
            l = line.split(",")
            l = map(int, l)
            retval.append(l)
            line = f.readline().rstrip("\n")
    retval.sort()
    return retval
    
def make_bin_centers(csv_list):
    retval = []
    for l in csv_list:
        center = (l[0] + l[1]) / 2.0
        retval.append(center)
    return retval
    
def make_bin_counts(csv_list):
    retval = []
    for l in csv_list:
        count = l[2]
        retval.append(count)
    return retval

def draw_histogram(csv_path):
    csv_list = read_csv(csv_path)
    center = make_bin_centers(csv_list)
    counts = make_bin_counts(csv_list)
    width = 0.7 * (center[1] - center[0])
    plt.bar(center, counts, align='center', width=width)
    plt.show()
 

draw_histogram("/home/tofi/git/streaming-performance/src/test/resources/testdata/flink-0.6-incubating-SNAPSHOT-streaming-new/10_10_10_10_10_0_10/histogramPart-10_10_10_10_10_0_10-1427379.csv")   
#draw_histogram("/home/tofi/git/streaming-performance/src/test/resources/testdata/histogramPart-1_1_1_1_1_1_10-4826814-1cnt.csv")
