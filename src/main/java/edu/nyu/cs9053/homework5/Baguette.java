package edu.nyu.cs9053.homework5;

import java.lang.*;

public class Baguette implements Recipe {
    
    private static final int VOLUME_CUBICINCHES = 2000;
    
    private final double rate;
    
    private double remainingSecondsUnitlDone;
    
    private double completedPercentage;
    
    private static Oven oven;
    
    protected Baguette(Oven oven, double rate) {
        this.rate = rate;
        this.initializeFromOven(oven);
    }
    
    public double getRate() {
        return rate;
    }
    
    @Override
    public void initializeFromOven(Oven oven) {
        this.oven = oven;
    }

    @Override
    public int getVolumeCubicInches() {
        return VOLUME_CUBICINCHES;
    }

    @Override
    public Double getRemainingSecondsUntilDone() {
        return remainingSecondsUnitlDone;
    }

    @Override
    public void adjust(Time unit, int amount, int ovenTemperature) {
        double leftTime = Math.log (ovenTemperature/0.01) / rate * 60;
        if (unit == Time.Minutes) {
            completedPercentage += (double) amount * 60 /leftTime;
            remainingSecondsUnitlDone = (1-completedPercentage) * leftTime;
        } else {
            completedPercentage += (double) amount/(leftTime);
            remainingSecondsUnitlDone = (1-completedPercentage) * leftTime;
        }
    }

    @Override
    public boolean isRecipeDone() {
        if (remainingSecondsUnitlDone >= 0) {
            return false;
        }
        return true;
    } 
}