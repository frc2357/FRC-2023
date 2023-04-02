package com.team2357.frc2023.util;

public class CountdownTimer extends Thread {

    @Override
    public void run() {

        System.err.println("Please wait until countdown has passed! Robot may freak out if you dont.");
        for (int i = 10; i > 0; i--) {
            System.err.print(i + " ");
            for (int a = i; a > 0; a--) {
                System.err.print("-");
            }
            System.err.print("\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

        }
        System.err.println("The robot should be fine to use now, so give the thumbs up.");
    }
}