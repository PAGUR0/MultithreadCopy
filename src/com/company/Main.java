package com.company;

import java.io.*;

public class Main {

    public static void main(String[] args) {

        File[] fileCopy = {new File("Copy.txt"), new File("Copy_2.txt")};
        File[] filePaste = {new File("Paste.txt"), new File("Paste.txt")};

        SynchronizedLockerExample lockerExample = new SynchronizedLockerExample();

        ThreadMul thread_1 = new ThreadMul(fileCopy[0], filePaste[0], lockerExample);
        ThreadMul thread_2 = new ThreadMul(fileCopy[1], filePaste[1], lockerExample);

        System.out.println("Mul\tTime (ms)");

        Boolean Mul = false;
        long startTime = System.currentTimeMillis();
        try {
            copyFileUsingStream(fileCopy[0], filePaste[0]);
            copyFileUsingStream(fileCopy[1], filePaste[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(Mul + "\t" + (endTime - startTime));

        startTime = System.currentTimeMillis();
        Mul = true;

        thread_1.start();
        thread_2.start();
        endTime = System.currentTimeMillis();
        System.out.println(Mul + "\t" + (endTime - startTime));

        try {
            thread_1.join();
            thread_2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Locker: " + lockerExample.getSharedResource());
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    static class ThreadMul extends Thread{

        File copy;
        static int n_run = 0;
        static int n = 0;
        File paste;
        SynchronizedLockerExample locker;

        ThreadMul(File copy, File paste, SynchronizedLockerExample locker) {
            this.copy = copy;
            this.paste = paste;
            this.locker = locker;
            n += 1;
        }


        @Override
        public void run() {
            try {
                copyFileUsingStream(copy, paste);
            } catch (IOException e) {
                e.printStackTrace();
            }
            n_run += 1;
            if(n_run > (n/2)){
                locker.increment();
            }
            else{
                locker.decrement();
            }
        }
    }

    public static class SynchronizedLockerExample {
        private int sharedResource = 0;

        public synchronized void increment() {
            sharedResource++;
        }

        public synchronized void decrement() {
            sharedResource--;
        }

        public synchronized int getSharedResource() {
            return sharedResource;
        }
    }
}
