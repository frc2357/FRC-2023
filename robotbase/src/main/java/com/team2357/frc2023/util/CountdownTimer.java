package com.team2357.frc2023.util;

import edu.wpi.first.wpilibj.Timer;

public class CountdownTimer extends Thread{

    @Override
    public void run() {
        Timer timer = new Timer();
        Integer i =0;
        timer.start();
        System.out.println("Please wait until countdown has passed! Robot may freak out if you dont.\n10 ----------");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println("9 ---------");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println("8 --------");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println("7 -------");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println("6 ------");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }System.out.println("5 -----");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }System.out.println("4 ----");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }System.out.println("3 ---");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }System.out.println("2 --");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }System.out.println("1 -");i++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }System.out.println("The robot should be fine to use now, so give thumbs up.");
    }
}