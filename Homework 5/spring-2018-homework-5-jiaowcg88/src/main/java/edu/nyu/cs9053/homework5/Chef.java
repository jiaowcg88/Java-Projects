package edu.nyu.cs9053.homework5;

/**
 * User: blangel
 */
public class Chef {
    
    public static void main(String[] args) {
        double rate = 0.5d;
        Oven oven = new Oven(300);
        PotRoast potRoast = new PotRoast(oven);
        Baguette baguette = new Baguette(oven, rate);
        RoastedSweetPotato roastedSweetPotato = new RoastedSweetPotato(oven);
        Baguette baguette2 = new Baguette(oven, rate);
        SousChef sousChef = new SousChef(oven);
        prepare(potRoast, sousChef, oven);
        prepare(baguette, sousChef, oven);
        prepare(roastedSweetPotato, sousChef, oven);
        prepare(baguette2, sousChef, oven);
    }
    
    public static void prepare(Recipe recipe, SousChef sousChef, Oven oven) {
        sousChef.prepare(recipe, new RecipeReadyCallback() {
            @Override public void recipeReadyToCook(Recipe recipe) {
                oven.cook(recipe, new Timer() {
                    @Override public void update(Time unit, int amount, int ovenTemperature) {
                        if (recipe.isRecipeDone()) {
                            oven.takeOut(recipe);
                        } else {
                            recipe.adjust(unit, amount, ovenTemperature);
                            continueCooking(recipe, oven);  
                        }
                    }
                }, true);                   
            }
        }); 
    }
        
    public static void continueCooking(Recipe recipe, Oven oven){
        oven.cook(recipe, new Timer() {
            @Override public void update(Time unit, int amount, int ovenTemperature) {
                recipe.adjust(unit, amount, ovenTemperature);
                checkRecipeReady(recipe, oven);
            }
        }, false);  
    }
    
    public static void checkRecipeReady(Recipe recipe, Oven oven) {
        if (recipe.isRecipeDone()) {
            oven.takeOut(recipe);
        } else {
            continueCooking(recipe, oven);
        }
    }      
}
