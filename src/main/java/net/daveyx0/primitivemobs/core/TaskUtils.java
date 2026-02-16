package net.daveyx0.primitivemobs.core;

import java.util.ArrayList;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks;



public final class TaskUtils 
{

//Wildcard for bound generic class
    public static boolean mobHasTask(EntityLiving mob, Class<? extends EntityAIBase> taskClass)
    {
//EntityAITaskEntry is nested in 
//EntityAITasks, and it represents a singular task entry
        for (EntityAITasks.EntityAITaskEntry entry : mob.tasks.taskEntries) {
//If any of the task entries are instances of the task class provided
            if (taskClass.isInstance(entry.action)) {
//Return true
                return true;
            }
        }
        return false;
    }
    

    public static void mobRemoveTasksIfPresent(EntityLiving mob, ArrayList<Class<? extends EntityAIBase>> taskClasses) 
    {
        for(Class<? extends EntityAIBase> taskClass : taskClasses)
        {
            mob.tasks.taskEntries.removeIf(entry ->
                taskClass.isInstance(entry.action)
            );
        }
    }


    public static void mobRemoveTaskIfPresent(EntityLiving mob, Class<? extends EntityAIBase> taskClass) 
    {
        mob.tasks.taskEntries.removeIf(entry ->
            taskClass.isInstance(entry.action)
        );
    }

    public static void mobRemoveTargetTaskIfPresent(EntityLiving mob, Class<? extends EntityAITarget> targetTaskClass) 
    {
        mob.targetTasks.taskEntries.removeIf(entry ->
            targetTaskClass.isInstance(entry.action)
        );
    }
}
