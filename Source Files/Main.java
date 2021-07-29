package com.company;

// Author - Rohin Joseph(17461856) - Individual Assignment

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        Main main = new Main();
        //Project path to be used to point to the files
        String path = "C:/Users/rohin/OneDrive/Documents/MapReduceAssignment/src/com/company/";

        boolean groupedStrategy = false;

       long mappingTime;
       long groupingTime;
       long reducingTime;

      if (args.length < 5) {
        System.err.println("usage: java MapReduceFiles[Number of Lines Per Thread] [Size Of Group] file1.txt file2.txt file3.txt");
      }

       int numLinesPerThread = Integer.parseInt(args[0]);
       int groupSize = Integer.parseInt(args[1]);

        String input2 = args[2];
        File file1 = new File(path+input2);
        String input3 = args[3];
        File file2 = new File(path+input3);
        String input4 = args[4];
        File file3 = new File(path+input4);


        String file1Input = main.readWordsFromFile(file1);
        String file2Input = main.readWordsFromFile(file2);
        String file3Input = main.readWordsFromFile(file3);

        // ExecutorService exec = Executors.newFixedThreadPool(numOfThreads);

      // Initialise Hash Map
        Map<String, String> input = new HashMap<String, String>();

        // Initialise Inputs from 3 files
        input.put(file1.getName(), file1Input);
        input.put(file2.getName(), file2Input);
        input.put(file3.getName(), file3Input);

        // APPROACH #3: Distributed MapReduce
        final Map<String, Map<String, Integer>> output = new HashMap<String, Map<String, Integer>>();



//         * MAP:
 //        */
        final List<MappedItem> mappedItems = new LinkedList<MappedItem>();
        final MapCallback<String, MappedItem> mapCallback = new MapCallback<String, MappedItem>()
        {
            @Override
            public synchronized void mapDone(String file, List<MappedItem> results) {
                mappedItems.addAll(results);
            }
        };

        // Map Cluster Initialisation
        List<Thread> mapCluster = new ArrayList<Thread>(input.size());

        Map<String, List<String>> splitted = new HashMap<String, List<String>>();
        for (String i : input.keySet()) {
                List<String> lines = format(input.get(i), numLinesPerThread);
                splitted.put(i, lines);

        }

        long preMappingTime = System.currentTimeMillis();  // Start counting mapping time

        Iterator<Map.Entry<String, String>> inputIter = input.entrySet().iterator();
        while (inputIter.hasNext()) {
            Map.Entry<String, String> entry = inputIter.next();
            final String file = entry.getKey();
            final String contents = entry.getValue();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    map(file, contents, mapCallback);
                }
            });
            // Add Thread t to List of threads.
            mapCluster.add(t);
            t.start();
        }

        // Iterate through the threads
        for (Thread t : mapCluster) {
            t.join();
        }

      //  while (!exec.isTerminated());
        long postMappingTime = System.currentTimeMillis(); // Finish counting
        mappingTime = postMappingTime - preMappingTime; // Calculate time taken for mapping

        // GROUP:
        long preGroupingTime = System.currentTimeMillis();
        Map<String, List<String>> groupedItems = new HashMap<String, List<String>>();

        Iterator<MappedItem> mappedIter = mappedItems.iterator();
        while (mappedIter.hasNext()) {
            MappedItem item = mappedIter.next();
            String word = item.getWord();
            String file = item.getFile();
            List<String> list = groupedItems.get(word);
            if (list == null) {
                list = new LinkedList<String>();
                groupedItems.put(word, list);
            }
            list.add(file);
        }
        long postGroupingTime = System.currentTimeMillis();
        groupingTime = postGroupingTime - preGroupingTime;

         // REDUCE:

        // Initialise Callback
        final ReduceCallback<String, String, Integer> reduceCallback = new ReduceCallback<String, String, Integer>() {
            @Override
            public synchronized void reduceDone(String k, Map<String, Integer> v) {
                output.put(k, v);
            }
        };

        List<List<Map.Entry<String, List<String>>>> blocks= new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> groupIter = groupedItems.entrySet().iterator();
        List<Map.Entry<String, List<String>>> temp= new ArrayList<>();
        int i =0;


        // Group Iterations
        while(groupIter.hasNext()) {
            Map.Entry<String, List<String>> entry = groupIter.next();
            temp.add(entry);
            i++;

            if(i%groupSize == 0){
                blocks.add(new ArrayList<>(temp));
                temp.clear();
            }
        }
        if(!temp.isEmpty()){
            blocks.add(temp);
        }

        // Re-initialise executor object.
     //   exec = Executors.newFixedThreadPool(5);
        long preReduceTime = System.currentTimeMillis();

        List<Thread> reduceCluster = new ArrayList<Thread>(groupedItems.size());

        Iterator<List<Map.Entry<String, List<String>>>> blockIter = blocks.iterator();
        while (blockIter.hasNext()) {
           List<Map.Entry<String, List<String>>> entry = blockIter.next();
          //  final String word = entry.getKey();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i =0; i<entry.size();i++){
                        final String word = entry.get(i).getKey();
                        final List<String> list = entry.get(i).getValue();
                        reduce(word, list, reduceCallback);
                    }
                }
            });
            // Add thread t
            reduceCluster.add(t);
            t.start();
        }

        // IIterate and execute each thread
        for (Thread t : reduceCluster) {
           // exec.execute(t);
       t.join();
        }

          //while (!exec.isTerminated());
      long postReduceTime = System.currentTimeMillis(); //
      reducingTime = postReduceTime - preReduceTime;

        System.out.println("Resulting Map Reduce: \n"+ output); // Print out Output
        //System.out.println("Number of threads being run \n-> " + numThreads);
        ; // Calculate Reduce Time

        long totalExecTime = reducingTime + groupingTime +mappingTime;

        System.out.println("\nMapping Time: " + mappingTime + " Milliseconds");
        System.out.println("\nGrouping Time: " + groupingTime + " Milliseconds");
        System.out.println("\nReducing Time: " + reducingTime + " Milliseconds");
        System.out.println("\nTotal Execution Time: " + totalExecTime + " Milliseconds (Modified Strategy)");


    }

    private static List<String> format(String input, int numLinesSplit){
        List<String> output = new ArrayList<>();

        BufferedReader bReader = new BufferedReader(new StringReader(input));
        String divider = "";
        String line;

        int count = 0;
        try {
            while((line = bReader.readLine()) != null){
                divider += line + "\n";
                count += 1;
                if(count%numLinesSplit == 0){
                    output.add(divider);
                    divider = "";
                }
            }
            if(!divider.equals("")){
                output.add(divider);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }


    public static interface MapCallback<E, V> {

        public void mapDone(E key, List<V> values);
    }

    public static void map(String file, String contents, MapCallback<String, MappedItem> callback) {
        String[] words = contents.trim().split("\\s+");
        List<MappedItem> results = new ArrayList<MappedItem>(words.length);
        for (String word : words) {
            results.add(new MappedItem(word, file));
        }
        callback.mapDone(file, results);
    }

    public static interface ReduceCallback<E, K, V> {

        public void reduceDone(E e, Map<K, V> results);
    }

    public static void reduce(String word, List<String> list, ReduceCallback<String, String, Integer> callback) {

        Map<String, Integer> reducedList = new HashMap<String, Integer>();
        for (String file : list) {
            Integer occurrences = reducedList.get(file);
            if (occurrences == null) {
                reducedList.put(file, 1);
            } else {
                reducedList.put(file, occurrences.intValue() + 1);
            }
        }
        callback.reduceDone(word, reducedList);
    }



    // Returns a string of every word in a file.
    public String readWordsFromFile(File file) throws IOException {

        StringBuilder sb = new StringBuilder();
        Scanner scan1 = null;
        try {
            scan1 = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scan1.hasNextLine()) {
            Scanner scan2 = new Scanner(scan1.nextLine());
            while (scan2.hasNext()) {
                String s = scan2.next();
                sb.append(s + " ");
            }
        }
        return sb.toString();
    }

    private static class MappedItem {

        private final String word;
        private final String file;

        public MappedItem(String word, String file) {
            this.word = removeSymbols(word); // Remove the symbols from each word
            this.file = file;
        }

        public String getWord() {
            return word;
        }

        public String getFile() {
            return file;
        }

            @Override
            public String toString() {
                return "[\"" + word + "\",\"" + file + "\"]";
            }
            public String removeSymbols(String word){
                word = word.toLowerCase();
                word = word.replaceAll("[.,?!:;*£$&(){}@/`_+=-]", ""); // Use regex to remove the additional symbols
                word = word.replace("\"", "");
                word = word.replace("'", "");

                return word;
            }
        }


    }
