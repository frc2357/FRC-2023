package com.team2357.frc2023.util;

import edu.wpi.first.wpilibj.Timer;

public class CountdownTimer extends Thread{

    @Override
    public void run() {
        Timer timer = new Timer();
        int i =10;
        timer.start();
        System.err.println("Please wait until countdown has passed! Robot may freak out if you dont.");
        while(i>0){
            System.err.print(i+" ");
            for(int a = i;a > 0;a--){
                System.err.print("-");
            }
            System.err.print("\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            i--;
        }
        System.err.println("The robot should be fine to use now, so give the thumbs up.");
    }
}