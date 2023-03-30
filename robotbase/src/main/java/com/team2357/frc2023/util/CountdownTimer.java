package com.team2357.frc2023.util;

import edu.wpi.first.wpilibj.Timer;

public class CountdownTimer extends Thread{

    @Override
    public void run() {
        Timer timer = new Timer();
        Integer i =0;
        timer.start();
        System.out.println("Please wait until countdown has passed! Robot may freak out if you dont.");
        while(true){
            System.out.print(10-i+" ");
            for(int a =10; a >i;a--){
                System.out.print("-");
            }
            System.out.print("\n");
            if(i>=9){
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