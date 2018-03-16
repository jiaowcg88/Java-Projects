package edu.nyu.cs9053.homework5;

import java.util.Random;

public class RoastedSweetPotato implements Recipe {
    
    private double remainingSecondsUnitlDone;
    
    private static final int VOLUME_CUBICINCHES = 6000;
    
    private double completedPercentage;
    
    private static Oven oven;
    
    protected RoastedSweetPotato(Oven oven) {
        this.initializeFromOven(oven);
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
        double remainingTime =(double) 1/10 * ovenTemperature;
        if (unit == Time.Minutes) {
            completedPercentage += (double)amount/remainingTime;
            remainingSecondsUnitlDone = (1-completedPercentage) * remainingTime * 60;
        } else {
            completedPercentage += (double) amount/(remainingTime * 60);
            remainingSecondsUnitlDone = (1-completedPercentage) * remainingTime * 60;
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