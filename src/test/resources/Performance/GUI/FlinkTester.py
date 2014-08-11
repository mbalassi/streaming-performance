# -*- coding: utf-8 -*-

import subprocess
import zipfile
import re
import os

from Tkinter import *
import tkFileDialog

from PIL import ImageTk, Image


run_script_path = "../strato-run-and-compare.sh"
save_file = ".FlinkTesterSave"


def show_jar_classes(jar_file):
    """prints out .class files from jar_file"""
    zf = zipfile.ZipFile(jar_file, 'r')
    retval = []
    try:
        lst = zf.infolist()
        for zi in lst:
            fn = zi.filename
            if fn.endswith('.class'):
                retval.append(fn)
        return retval
    finally:
        zf.close()

def get_classes(jar_file):
    retval = []
    for classPath in show_jar_classes(jar_file):
        if not '$' in classPath:
            classPath = re.sub('/', '.', classPath)[:-6]
            className = classPath.split(".")[-1]
            retval.append([classPath, className])
    return retval
    
def get_classes_with_main(jar_file):
    cList = get_classes(jar_file)
    retval = []
    for c in cList:
        if c[1][-4:] == "Main":
            retval.append(c)
    return retval
    
def nth_parent_directory(dir_path, n = 1):
    return dir_path.split("/")[-1 * n - 1]
    
def get_results_from_results_folder(folder_path):
    retval = []
    for root, dirs, files in os.walk(folder_path):
        if nth_parent_directory(root, 1) == nth_parent_directory(folder_path, 0):
            className = nth_parent_directory(root, 0)
            retval.append([className, root, sorted(dirs)])
    return retval
    
def replace_spaces(value):
    retval = re.sub("\s+", "_", value)
    if retval[-1] == "_":
        retval = retval[:-1]
    return retval

def list_equal(list1, list2):
    if len(list1) != len(list2):
        return False
    else:
        for i in range(len(list1)):
            if list1[i] != list2[i]:
                return False
        return True
        

def list_in_list(small, big):
    for l in big:
        if list_equal(l, small):
            return True
    return False


############## TKINTER #################



class JarHandler:

    def __init__(self, master, side):
        self.frame = Frame(master)
        self.topFrame = Frame(self.frame)
        self.topTopFrame = Frame(self.topFrame)
        self.topBottomFrame = Frame(self.topFrame)
        self.middleFrame = Frame(self.frame)
        self.bottomFrame = Frame(self.frame)
        self.moreBottomFrame = Frame(self.frame)
        
        self.topFrame.pack(side=TOP)
        self.middleFrame.pack()
        self.bottomFrame.pack()
        self.moreBottomFrame.pack(side=BOTTOM)
        self.frame.pack(side=side)
        self.topTopFrame.pack(side=TOP)
        self.topBottomFrame.pack(side=BOTTOM)
        
        self.browseButton = Button(self.topTopFrame, text="Browse", command=self.loadFile, width=10)
        self.browseButton.pack(side=LEFT)
        
        self.addButton = Button(self.topTopFrame, text="Add", command=self.addSelectedClassPaths, width=10)
        self.addButton.pack(side=LEFT)
        
        self.onlyClassesWithMain = IntVar()
        self.classCheckButton = Checkbutton(self.topTopFrame, text="OnlyMain", variable=self.onlyClassesWithMain,
            onvalue=1, offvalue=0)
        self.classCheckButton.pack(side=LEFT)
        
        self.jarName = "none"
        self.loadedJars = []
        
        self.jarScrollbar = Scrollbar(self.topBottomFrame, orient=VERTICAL)
        self.jarListBox = Listbox(self.topBottomFrame, width=30, height=20, selectmode=MULTIPLE, 
                                  yscrollcommand=self.jarScrollbar.set, exportselection=0)
        self.jarScrollbar.config(command=self.jarListBox.yview)
        self.jarScrollbar.pack(side=RIGHT, fill=Y)
        self.jarListBox.pack()
        
        self.addedScrollbar = Scrollbar(self.middleFrame, orient=VERTICAL)
        self.addedListBox = Listbox(self.middleFrame, width=30, height=10, selectmode=MULTIPLE, 
                                  yscrollcommand=self.addedScrollbar.set, exportselection=0)
        self.addedScrollbar.config(command=self.addedListBox.yview)
        self.addedScrollbar.pack(side=RIGHT, fill=Y)
        self.addedListBox.pack()
        
        self.addedClassPaths = []
        
        self.deleteAddedButton = Button(self.bottomFrame, text="Remove", command=self.deleteFromAddedAndCustom, width=10)
        self.deleteAddedButton.pack(side=LEFT)
        
        self.customArgButton = Button(self.bottomFrame, text="Custom arg", command=self.addCustomArg, width=10)
        self.customArgButton.pack(side=LEFT)
        
        self.argText = Text(self.moreBottomFrame, width=30, height=5)
        self.argText.insert(INSERT, "")
        self.argText.mark_set("sentinel", INSERT)
        self.argText.mark_gravity("sentinel", LEFT)
        self.argText.pack(side=TOP)
        
        self.customArgClasses = []
        
        self.customArgScrollbar = Scrollbar(self.moreBottomFrame, orient=VERTICAL)
        self.customArgScrollbar2 = Scrollbar(self.moreBottomFrame, orient=HORIZONTAL)
        self.customArgListBox = Listbox(self.moreBottomFrame, width=30, height=10, selectmode=MULTIPLE, 
                                  yscrollcommand=self.customArgScrollbar.set, 
                                  xscrollcommand=self.customArgScrollbar2.set, exportselection=0)
        self.customArgScrollbar.config(command=self.customArgListBox.yview)
        self.customArgScrollbar.pack(side=RIGHT, fill=Y)
        self.customArgScrollbar2.config(command=self.customArgListBox.xview)
        self.customArgScrollbar2.pack(side=BOTTOM, fill=X)
        self.customArgListBox.pack(side=BOTTOM)
        
    def loadFile(self):
        self.jarName = tkFileDialog.askopenfilename(filetypes=(("Java archive files", "*.jar"),
                                           ("All files", "*.*") ))
        if not self.jarName:
            self.jarName = ""
        else:
            self.browseJar()
            
    def browseJar(self):
        if self.onlyClassesWithMain.get() == 1:
            self.loadedJars = get_classes_with_main(self.jarName)
        else:
            self.loadedJars = get_classes(self.jarName)
        jarNames = [l[1] for l in self.loadedJars]
        self.jarListBox.delete(0, END)
        for item in jarNames:
            self.jarListBox.insert(END, item)
            
    def getSelectedIndicesJarList(self):
        return self.jarListBox.curselection()
        
    def getSelectedIndicesAddedList(self):
        return self.addedListBox.curselection()
        
    def getSelectedIndicesCustomArg(self):
        return self.customArgListBox.curselection()
        
    def addSelectedClassPaths(self):
        indices = self.getSelectedIndicesJarList()
        for i in indices:
            self.addSelected(self.loadedJars[int(i)])
            
    def addSelected(self, selected):
        if selected[0] not in self.addedClassPaths:
            self.addedClassPaths.append(selected[0])
            self.addedListBox.insert(END, selected[1])
            
    def deleteFromAddedAndCustom(self):
        selectedIndices = self.getSelectedIndicesAddedList()
        removeIndices = [int(selectedIndices[i]) - i for i in range(len(selectedIndices))]
        for i in removeIndices:
            self.addedClassPaths.pop(i)
            self.addedListBox.delete(i)
            
        selectedIndices = self.getSelectedIndicesCustomArg()
        removeIndices = [int(selectedIndices[i]) - i for i in range(len(selectedIndices))]
        for i in removeIndices:
            self.customArgClasses.pop(i)
            self.customArgListBox.delete(i)
    
    def addCustomArg(self):
        indices = self.getSelectedIndicesAddedList()
        for i in indices:
            self.addSingleCustomArg(int(i))
            
    def addSingleCustomArg(self, i):
        classPath = self.addedClassPaths[i]
        className = self.addedListBox.get(i)
        arg = self.argText.get("sentinel", END).rstrip('\n')
        newCustom = [classPath, arg]
        newCustomString = className + " -> " + replace_spaces(arg)
        
        if not list_in_list(newCustom, self.customArgClasses):
            self.customArgClasses.append(newCustom)
            self.customArgListBox.insert(END, newCustomString)
            
    def getClasses(self):
        return self.addedClassPaths
        
    def getClassesCustomArgs(self):
        return self.customArgClasses
        
    def getJar(self):
        return self.jarName
            
            
class ExistingResultHandler:

    def __init__(self, master, side):

        self.frame = Frame(master)
        self.topFrame = Frame(self.frame)
        self.topTopFrame = Frame(self.topFrame)
        self.topBottomFrame = Frame(self.topFrame)
        self.topLeftFrame = Frame(self.topBottomFrame)
        self.topRightFrame = Frame(self.topBottomFrame)
        self.bottomFrame = Frame(self.frame)
        self.bottomTopFrame = Frame(self.bottomFrame)
        self.bottomBottomFrame = Frame(self.bottomFrame)  
        
        self.frame.pack(side=side)
        self.topFrame.pack(side=TOP)
        self.bottomFrame.pack(side=BOTTOM)
        self.topLeftFrame.pack(side=LEFT)
        self.topRightFrame.pack(side=RIGHT)
        self.topTopFrame.pack(side=TOP)
        self.topBottomFrame.pack(side=BOTTOM)
        self.bottomTopFrame.pack(side=TOP)
        self.bottomBottomFrame.pack(side=BOTTOM)
        
        self.browseResultsDirButton = Button(self.topTopFrame, text="Browse", command=self.loadResultsDirectory, width=10)
        self.browseResultsDirButton.pack(side=LEFT)
        
        self.addButton = Button(self.topTopFrame, text="Add", command=self.getSelectedResultsPath, width=10)
        self.addButton.pack(side=RIGHT)
        
        self.resultsDirName = ""
        self.existingResults = []
        
        self.resScrollbar = Scrollbar(self.topLeftFrame, orient=VERTICAL)
        self.resultsListBox = Listbox(self.topLeftFrame, width=30, height=30, 
                                      selectmode=EXTENDED, yscrollcommand=self.resScrollbar.set, 
                                      exportselection=0)
        self.resScrollbar.config(command=self.resultsListBox.yview)
        self.resScrollbar.pack(side=RIGHT, fill=Y)
        self.resultsListBox.pack(side=LEFT)
        
        self.current = None
        self.poll()
        
        self.selected = []
        
        self.argScrollbar = Scrollbar(self.topRightFrame, orient=VERTICAL)
        self.argsListBox = Listbox(self.topRightFrame, width=30, height=30, selectmode=MULTIPLE, 
                                   yscrollcommand=self.argScrollbar.set, exportselection=0)
        self.argScrollbar.config(command=self.argsListBox.yview)
        self.argScrollbar.pack(side=RIGHT, fill=Y)
        self.argsListBox.pack(side=LEFT)
        
        self.removeButton = Button(self.bottomTopFrame, text="Remove", command=self.removeAdded, width=10)
        self.removeButton.pack(side=TOP)
        
        self.addedScrollbar = Scrollbar(self.bottomBottomFrame, orient=VERTICAL)
        self.addedListBox = Listbox(self.bottomBottomFrame, width=62, height=16, selectmode=MULTIPLE, 
                                   yscrollcommand=self.addedScrollbar.set, exportselection=0)
        self.addedScrollbar.config(command=self.addedListBox.yview)
        self.addedScrollbar.pack(side=RIGHT, fill=Y)
        self.addedListBox.pack(side=LEFT)
        
    def poll(self):
        now = self.resultsListBox.curselection()
        if now != self.current:
            self.selectionFromResultList(now)
            self.current = now
        self.frame.after(250, self.poll)
        
    def selectionFromResultList(self, selection):
        if len(selection) > 0:
            self.populateArgsList(self.existingResults[int(selection[0])])
            
    def populateArgsList(self, selectedClass):
        args = selectedClass[2]
        self.argsListBox.delete(0, END)
        for item in args:
            self.argsListBox.insert(END, item)
        
    def loadResultsDirectory(self):
        self.resultsDirName = tkFileDialog.askdirectory()
        if not self.resultsDirName:
            self.resultsDirName = ""
        else:
            self.browseResultsDir()
            
    def browseResultsDir(self):
        self.existingResults = get_results_from_results_folder(self.resultsDirName)
        resultNames = [l[0] for l in self.existingResults]
        self.resultsListBox.delete(0, END)
        self.argsListBox.delete(0, END)
        for item in resultNames:
            self.resultsListBox.insert(END, item)
        
    def getSelectedIndicesArgs(self):
        return self.argsListBox.curselection()
        
    def getSelectedIndicesAdded(self):
        return self.addedListBox.curselection()
        
    def getSelectedIndexClasses(self):
        return int(self.resultsListBox.curselection()[0])
        
    def getSelectedClass(self):
        index = self.getSelectedIndexClasses()
        return self.existingResults[index]
        
    def getSelectedResultsPath(self):
        for i in self.getSelectedIndicesArgs():
            self.getSelectedResultPath(int(i))
            
    def getSelectedResultPath(self, i):
        selectedClass = self.getSelectedClass()
        className = selectedClass[0]
        classPath = selectedClass[1]
        arg = selectedClass[2][i]
        
        newSelection = classPath + "/" + arg
        selectedName = className + " -> " + arg
        
        if newSelection not in self.selected:
            self.selected.append(newSelection)
            self.addedListBox.insert(END, selectedName)
    
    def removeAdded(self):
        selectedIndices = self.getSelectedIndicesAdded()
        removeIndices = [int(selectedIndices[i]) - i for i in range(len(selectedIndices))]
        for i in removeIndices:
            self.selected.pop(i)
            self.addedListBox.delete(i)
            
    def getExistingResults(self):
        return self.selected
        
class GraphHandler:

    def __init__(self, master, side):

        self.frame = Frame(master)
        self.frame.pack(side=side)
        self.text = Text(self.frame)
        self.text.insert(INSERT, "GraphHandler")
        self.text.pack()
        
        
class OutputHandler:

    def __init__(self, master, side, jarHandler, existingResultHandler):

        self.jarHandler = jarHandler
        self.existingResultHandler = existingResultHandler

        self.frame = Frame(master)
        self.frame.pack(side=side)
        
        self.browseButton = Button(self.frame, text="Browse", command=self.selectSaveDir, width=10)
        self.browseButton.pack(side=TOP)        
        
        self.runButton = Button(self.frame, text="Run", command=self.runScript, width=10)
        self.runButton.pack(side=TOP)        
        
        imageFile = "feather-small.gif"
        self.img2 = ImageTk.PhotoImage(Image.open(imageFile))
        self.panel2 = Label(self.frame, image = self.img2)
        self.panel2.pack(side=LEFT)        
        
        self.text = Text(self.frame)
        self.text.insert(INSERT, "OutputHandler")
        self.text.pack(side=LEFT)
        
        self.img = ImageTk.PhotoImage(Image.open(imageFile))
        self.panel = Label(self.frame, image = self.img)
        self.panel.pack(side=RIGHT)
        
        self.saveDir = ""
        self.defaultTestParams = "TODO"
        self.testLength = "TODO"
        
    def selectSaveDir(self):
        self.saveDir = tkFileDialog.askdirectory()
        if not self.saveDir:
            self.saveDir = ""
        
    def makeClassArgs(self):
        s = ""
        classes = self.jarHandler.getClasses()
        classesCust = self.jarHandler.getClassesCustomArgs()
        
        s += self.makeClassArgsDefaultString(classes)
        s += self.makeClassArgsCustomString(classesCust)
        
        return s
        
    def makeClassArgsDefaultString(self, classes):
        s = ""
        for classPath in classes:
            s += "-c " + classPath + " "
        return s
    
    def makeClassArgsCustomString(self, classesCust):
        s = ""
        for l in classesCust:
            classPath = l[0]
            arg = l[1]
            s += "-c " + classPath + " -a " + arg + " "
        return s
        
    def makeExistingResultArgs(self):
        s = ""        
        existingResults = self.existingResultHandler.getExistingResults()
        
        s += self.makeExistingArgsString(existingResults)
        return s
        
    def makeExistingArgsString(self, existingResults):
        s = ""
        for resultPath in existingResults:
            s += "-d " + resultPath + " "
        return s
        
    def makeArgs(self):
        s = ""        
        s += self.makeClassArgs()
        s += self.makeExistingResultArgs()
        return s
        
    def makeScript(self):
        s = ""
        s += run_script_path + " "
        s += self.jarHandler.getJar() + " "
        s += self.saveDir + " "
        s += self.defaultTestParams + " "
        s += self.testLength + " "
        s += self.makeArgs()
        return s
        
    def runScript(self):
        script = self.makeScript()
        print script
        
class FrameContainer:

    def __init__(self, master):

        self.frame = Frame(master)
        self.frame.pack()
        
        self.top = Frame(self.frame)
        self.bottom = Frame(self.frame)
        self.jarHandler = JarHandler(self.top, LEFT)
        self.existingResultHandler = ExistingResultHandler(self.top, LEFT)
        self.graphHandler = GraphHandler(self.top, LEFT)
        self.outputHandler = OutputHandler(self.bottom, BOTTOM, self.jarHandler, self.existingResultHandler)
        self.top.pack(side=TOP)
        self.bottom.pack(side=BOTTOM)
        

root = Tk()
root.title('Flink tester')

app = FrameContainer(root)

if os.path.isfile(save_file):
    with open(save_file, "rt") as f:
        jarPath = f.readline().rstrip("\n")
        resultsDir = f.readline().rstrip("\n")
        onlyClassesWithMain = int(f.readline().rstrip("\n"))
        saveDir = f.readline().rstrip("\n")

        if onlyClassesWithMain == 1:
            app.jarHandler.classCheckButton.select()

        if jarPath != '':
            app.jarHandler.jarName = jarPath
            app.jarHandler.browseJar()
        
        if resultsDir != '':
            app.existingResultHandler.resultsDirName = resultsDir
            app.existingResultHandler.browseResultsDir()
            
        if saveDir != '':
            app.outputHandler.saveDir = saveDir
            
root.mainloop()
#root.destroy()

with open(save_file, "wt") as f:
    jarPath = app.jarHandler.jarName
    resultsDir = app.existingResultHandler.resultsDirName
    onlyClassesWithMain = str(app.jarHandler.onlyClassesWithMain.get())
    saveDir = str(app.outputHandler.saveDir)    
    
    f.write(jarPath)
    f.write("\n")
    f.write(resultsDir)
    f.write("\n")
    f.write(onlyClassesWithMain)
    f.write("\n")
    f.write(saveDir)
