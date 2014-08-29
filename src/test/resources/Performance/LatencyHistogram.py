# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
import numpy as np
import os
import sys

def read_csv_dir(csv_dir):
    retval = []
    fnameList = []
    for fname in os.listdir(csv_dir):
        if '.csv' in fname:
            fnameList.append(fname)
    for fname in fnameList:
        csv_list = read_csv(csv_dir + "/" + fname)
        retval.append(csv_list)
    return retval


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
    
def convert_csv_list_to_dict(csv_list):
    retval = {}
    for record in csv_list:
        retval[(record[0], record[1])] = record[2]
    return retval
    
def convert_csv_dict_to_list(csv_dict):
    retval = []
    for interval in csv_dict:
        retval.append([interval[0], interval[1], csv_dict[interval]])
    return retval
    
def join_csvs(csv_lists):
    joined_dict = {}
    for csv_list in csv_lists:
        csv_dict = convert_csv_list_to_dict(csv_list)
        for interval in csv_dict:
            if interval not in joined_dict:
                joined_dict[interval] = csv_dict[interval]
            else:
                joined_dict[interval] += csv_dict[interval]
    joined_list = convert_csv_dict_to_list(joined_dict)
    joined_list.sort()
    return joined_list
    
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

def draw_histogram(csv_dir, save_dir, save_file_name, save, show):
    csv_lists = read_csv_dir(csv_dir)
    csv_list = join_csvs(csv_lists)
    center = make_bin_centers(csv_list)
    counts = make_bin_counts(csv_list)
    width = 0.7 * (center[1] - center[0])
    if save:
        plt.bar(center, counts, align='center', width=width)
        save_file = save_dir + "/" + save_file_name
        plt.title('latency histogram')
        plt.legend([save_file_name])
        figure = plt.gcf()
        figure.set_size_inches(12, 8)
        plt.savefig(save_file+'-histogram.png', dpi = 80)
    if show:
        plt.show()

if __name__ == "__main__":
    csv_dir = sys.argv[1]
    save_dir = sys.argv[2]
    save_file_name = sys.argv[3]
    save = bool(sys.argv[4])
    show = bool(sys.argv[5])
    draw_histogram(csv_dir, save_dir, save_file_name, save, show)
 
#csv_dir = "/home/tofi/git/streaming-performance/src/test/resources/testdata/autoperf-hadoop/results/WordCountLatencyMain/4_1_2_2_2_0_10/"
#save_dir = csv_dir
#draw_histogram(csv_dir, save_dir, True, True)
