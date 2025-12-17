package net.daveyx0.primitivemobs.core;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;



public class PrimitiveMobsItemIdsToItemStacks {

//Converts a list of item Ids to a set of ItemStacks
    public static ArrayList<ItemStack> itemIdsToItemStacks(ArrayList<String> itemIds)
    {
//ItemStack list to add stuff to
        ArrayList<ItemStack> itemStacks = new ArrayList<>();

//If item Ids not empty
        if(!itemIds.isEmpty())
        {
//For each id
            for(String id : itemIds)
            {
//Fetch Item object from Id then fetch default ItemStack
                itemStacks.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(id)).getDefaultInstance());                
            }
            return itemStacks;
        } 
        else 
        {
            return new ArrayList<>();
        }
    }
}
