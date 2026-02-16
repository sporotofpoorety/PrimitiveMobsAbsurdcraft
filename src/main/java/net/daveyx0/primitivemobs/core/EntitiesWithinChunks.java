package net.daveyx0.primitivemobs.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.util.ClassInheritanceMultiMap;

import net.daveyx0.primitivemobs.interfacemixins.IMixinEntityMob;


public final class EntitiesWithinChunks
{

//Get list of mobs within chunk radius
    public static ArrayList<EntityMob> getMobsInChunkRadius(World world, double x, double z, int chunkRadius)
    {
//List to fill with mobs
        ArrayList<EntityMob> mobsList = new ArrayList<>();

//This is what vanilla does to 
//convert xyz to chunk coords btw, first
//it floors the position then it bit shifts (faster way to divide by 16)
        int centerChunkX = MathHelper.floor(x) >> 4;
        int centerChunkZ = MathHelper.floor(z) >> 4;


//Just server sided
        if (!(world instanceof WorldServer))
        {
//Client side return empty list lol
            return mobsList;
        }

//Get world server
        WorldServer worldServer = (WorldServer) world;
//Get chunk provider
        ChunkProviderServer chunkProvider = worldServer.getChunkProvider();


//Iterate chunk X
        for (int currentChunkX = centerChunkX - chunkRadius; currentChunkX <= centerChunkX + chunkRadius; currentChunkX++)
        {
//And iterate chunk Z
            for (int currentChunkZ = centerChunkZ - chunkRadius; currentChunkZ <= centerChunkZ + chunkRadius; currentChunkZ++)
            {

//If chunk not loaded skip it
                if (!chunkProvider.chunkExists(currentChunkX, currentChunkZ))
                {
                    continue;                
                }

//Get loaded chunk from chunk provider
                Chunk currentChunk = chunkProvider.getLoadedChunk(currentChunkX, currentChunkZ);

//If chunk null then skip
                if (currentChunk == null)
                {
                    continue;
                }

//ClassInheritanceMultimap is 
//a weird Minecraft specific collection

//They're multimaps where 
//the keys correspond to classes,
//the entities are grouped under multiple keys
//by their inheritance tree, allowing getting class-sorted entity lists


//For each entity multimap in this chunk's list of multimaps
                for (ClassInheritanceMultiMap<Entity> sortedEntityList : currentChunk.getEntityLists())
                {
//For this specific multimap
                    for (Entity entity : sortedEntityList)
                    {
//Check if each entity is an instance of a mob
                        if (entity instanceof EntityMob)
                        {
//Use mixins to check if it's a tamed hostile mob
                            IMixinEntityMob entityMixin = (IMixinEntityMob) entity;
                            if(!entityMixin.isTamed())
                            {
//If it isn't, add it to the return list
                                mobsList.add((EntityMob) entity);
                            }
                        }
                    }
                }
            }
        }

        return mobsList;
    }
}


