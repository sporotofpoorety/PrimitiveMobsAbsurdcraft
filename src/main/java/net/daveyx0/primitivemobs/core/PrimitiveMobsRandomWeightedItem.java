package net.daveyx0.primitivemobs.core;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;


public class PrimitiveMobsRandomWeightedItem {
//Trying to generalize
//Should take a list of items and a list of weights then return a random one    
    public static <itemType> itemType getRandomWeightedItem(ArrayList<itemType> items, ArrayList<Integer> weights)
    {
//Ranges for picking weighted items 
        ArrayList<Integer> probabilityStops = new ArrayList<>();
//Total current weight value
        int probabilityStop = 0;

//For each weight make a list of probability stops
        for(int weight : weights)
        {
            probabilityStop += weight;
            probabilityStops.add(probabilityStop);
        }

//Pick a random value
        int randomPickedValue = ThreadLocalRandom.current().nextInt(probabilityStop) + 1;

//Use to check picked value's index amongst the probability stops
        int pickedValIndex = -1;

//If probability stops list not empty
        if(!probabilityStops.isEmpty())
        {
//For each probability stop
            for(int i = 0; i < probabilityStops.size(); i++)
            {
//If picked value falls into current range
                if(randomPickedValue <= probabilityStops.get(i))
                {
//Set picked value's index and stop search
                    pickedValIndex = i;
                    break;
                }
            }
        }

//Exception handling for no valid index
        if(pickedValIndex >= 0)
        {
            return items.get(pickedValIndex);        
        }
        else
        {
            return null;
        }
    }
}
