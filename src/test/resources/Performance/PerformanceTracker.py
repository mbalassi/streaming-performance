# -*- coding: utf-8 -*-
"""
Created on Wed Apr 30 15:40:17 2014

@author: gyfora
"""

import matplotlib.pyplot as plt
import pandas as pd
import os
import operator
import sys

linestyles = ['_', '-', '--', ':']
markers=['D','s', '|', '', 'x', '_', '^', ' ', 'd', 'h', '+', '*', ',', 'o', '.', '1', 'p', 'H', 'v', '>'];
colors = ['b', 'g', 'r', 'c', 'm', 'y', 'k']
def readFiles(csv_dir):
    counters=[]
				
    for fname in os.listdir(csv_dir):
        if '.csv' in fname:
            counters.append((fname.rstrip('.csv'),int(fname.rstrip('.csv').split('-')[-1])-1,pd.read_csv(os.path.join(csv_dir,fname),index_col='Time')))
    return counters
    
def plotCounter(csv_dir, sname='', smooth=5,savePath=''):
	counters= readFiles(csv_dir)
	addSpeed(counters)	 
    
        
	print counters
    
    
	selectedCounters=[]
	for (name, number, df) in counters:
		if sname in name:
			selectedCounters.append((name, number, df))
	if sname=='':
		sname='counters'
	save=savePath!=''	
	
	plotDfs(selectedCounters,smooth,save,savePath+'/'+sname)
								
def plotDfs(counters,smooth,save,saveFile):
        plt.figure(figsize=(12, 8), dpi=80)
        plt.title('Counter')
        for (name, number, df) in counters:			
            
            m=markers[number%len(markers)]    
              
            df.ix[:,0].plot(marker=m,markevery=10,markersize=10)
        plt.legend([x[0] for x in counters])
        if save:        
		plt.savefig(saveFile+'C.png')
									
									
        plt.figure(figsize=(12, 8), dpi=80)
        plt.title('dC/dT')
       
    
        for (name, number, df) in counters:
            
            m=markers[number%len(markers)]            
            
            pd.rolling_mean(df.speed,smooth).plot(marker=m,markevery=10,markersize=10)
        plt.legend([x[0] for x in counters])
        if save:
		plt.savefig(saveFile+'D.png')
								
def addSpeed(counters):
	for (tname, number, df) in counters:
		speed=[0]
		values=list(df.ix[:,0])
		for i in range(1,len(values)):
			speed.append(float(values[i]-values[i-1])/float(df.index[i]-df.index[i-1]+0.01))
		df['speed']=speed
	return counters
        
def plotThroughput(csv_dir,tasknames, smooth=5,savePath=''):
	if type(tasknames)!=list:
		tasknames=[tasknames]
	for taskname in tasknames:
		counters= readFiles(csv_dir)
		addSpeed(counters)
		selected={}
	    
		for (tname, number, df) in counters:
			if taskname in tname:
				if number in selected:
			                selected[number].append(df)
				else:
			                selected[number]=[df]
		plt.figure()    
		plt.title(taskname)
		for i in selected:
			if len(selected[i])>1:
				selected[i]=reduce(operator.add,selected[i])
			else:
				selected[i]=selected[i][0]
			m=markers[i%len(markers)]       
			selected[i].ix[:,0].plot(marker=m,markevery=10,markersize=10)
	
	        
		plt.legend(selected.keys())
		if savePath !='':
			plt.savefig(savePath+'/'+taskname+'C.png')    
		plt.figure()    
		plt.title(taskname+" - dC/dT")
		for i in selected:
			m=markers[i%len(markers)]       
			pd.rolling_mean(selected[i].speed,smooth).plot(marker=m,markevery=10,markersize=10)
	        
		plt.legend(selected.keys())
		if savePath !='':
			plt.savefig(savePath+'/'+taskname+'D.png') 
    
def plotTimer(csv_dir,smooth=5,std=50):
    dataframes= readFiles(csv_dir)
    
    plt.figure(figsize=(12, 8), dpi=80)
    plt.title('Timer')
    
    for dataframe in dataframes:
        
        m=markers[dataframe[1]%len(markers)]  
          
        pd.rolling_mean(dataframe[2].ix[:,0],smooth).plot(marker=m,markevery=10,markersize=10)
    plt.legend([x[0] for x in dataframes])
    
    plt.figure(figsize=(12, 8), dpi=80)
    plt.title('Standard deviance')

    for dataframe in dataframes:
        
        m=markers[dataframe[1]%len(markers)]   
         
        pd.rolling_std(dataframe[2].ix[:,0],std).plot(marker=m,markevery=10,markersize=10)
    plt.legend([x[0] for x in dataframes])
    
def findInterval(num):
    interv = 1
    diff = 1
    current = 1
    while True:
        if num < current + diff:
            return interv
        elif (current + diff) % (diff * 10) == 0:
            interv = interv * 10
            current = interv
            diff = diff * 10
        else:
            current += diff
            

def csvJoin(csv_dir, sname = ''):
    fnameList = []
    for fname in os.listdir(csv_dir):
        if '.csv' in fname and sname in fname:
            if "-0" not in fname:
                fnameList.append(fname)
            
    if len(fnameList) == 0:
        return
    
    l = fnameList[0].split('-')
    newFileName = l[0] + '-' + l[1] + "-0" + ".csv"

    firstLine = ""
    fObjList = []    
    for fname in fnameList:
        fObjList.append(open(csv_dir + "/" + fname, "rt"))
        firstLine = fObjList[-1].readline()
        
    newFile = open(csv_dir + "/" + newFileName, "wt")    
    newFile.write(firstLine)    
    
    currentInterval = 0
    first = True
    stop = False
    while True:
        lines = []
        for fObj in fObjList:
            line = fObj.readline()
            if line == "":
                stop = True
                break
            else:
                lines.append(line)
                
        if stop:
            break        
        
        if first:
            intervalDiff = findInterval(int(lines[0].split(',')[0]))
            first = False
        
        label = ""
        valuePairs = []
        for line in lines:
            l = line.split(',')
            time = int(l[0])
            counter = int(l[1])
            label = l[2]
            valuePairs.append((time, counter))
        
        newCounter = 0
        for pair in valuePairs:
            newCounter += pair[1]
            
        newLine = ""
        newLine += str(currentInterval)
        newLine += ","
        newLine += str(newCounter)
        newLine += ","
        newLine += label
        newFile.write(newLine)
            
        currentInterval += intervalDiff
        
        
    for fObj in fObjList:
        fObj.close()
    newFile.close()
    
def joinAndPlotAll(csv_dir, smooth, save_dir):
    fnameList = []
    for fname in os.listdir(csv_dir):
        if '.csv' in fname:
            fnameList.append(fname)   
    
    argsList = []
    for fname in fnameList:
        l = fname.split('-')
        if l[1] not in argsList:
            argsList.append(l[1])
    
    for args in argsList:
        csvJoin(csv_dir, args)
        plotCounter(csv_dir, args + '-0', smooth, save_dir)
    
    plotCounter(csv_dir, '-0', smooth, save_dir)
    
def doAllFolders(folder_path, smooth):
    for fname in os.listdir(folder_path):
        path = folder_path + "/" + fname
        joinAndPlotAll(path, smooth, path)        

csv_dir = sys.argv[1]
smooth = int(sys.argv[2])
save_dir = sys.argv[3]
mode = "singleFolder"
if len(sys.argv) > 4:
    mode = sys.argv[4]

if mode == "singleFolder":
    joinAndPlotAll(csv_dir, smooth, save_dir)
elif mode == "multipleFolders":
    doAllFolders(csv_dir, smooth)

#folder = '../testdata/0.6/newList'
#doAllFolders(folder, 5)
#joinAndPlotAll(folder, 5, folder)
#csvJoin('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '1_1_4_2_2_2')
#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '-0', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')
#print findInterval(1005)


#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '8_2_2_2', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')
#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '4_2_2_2-flush', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')
#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '4_2_2_2-noflush', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')
#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '4_4_4_2', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')
#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '4_4_2_4', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')
#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '4_2_4_4', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')
#plotCounter('/home/tofi/git/stratosphere-streaming/src/test/resources/testdata', '4_4_4_4', 5, '/home/tofi/git/stratosphere-streaming/src/test/resources/testdata')