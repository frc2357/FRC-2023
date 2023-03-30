package com.team2357.frc2023.util;

import edu.wpi.first.wpilibj.Timer;

public class CountdownTimer extends Thread{

    @Override
    public void run() {
        Timer timer = new Timer();
        int i =10;
        timer.start();
        System.out.println("Please wait until countdown has passed! Robot may freak out if you dont.");
        while(true){
            System.out.print(10-i+" ");
            while(i > 0){
                System.out.print("-");
                i--;
            }
            System.out.print("\n");
            if(i<1){
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            i++;
        }
        System.out.println("The robot should be fine to use now, so give the thumbs up.");
    }
}