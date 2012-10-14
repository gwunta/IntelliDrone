
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Intellidrone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * The TaskList class provides methods to add, remove and access one or more Tasks easily.
 * 
 * @author Grant Boxall
 * @version Version 1.0 - 24/09/2012
 */
public class TaskList
{
   private ArrayList<Task> tasklist;

   /**
    * @description Basic constructor
    */
   public TaskList()
   {   
   }
   /**
    *  Adds a new Task to the TaskList when all of the parameters of the task are known and/or need to be specified
    * @param latitude - The latitude of the target area of the item for deployment or pickup
    * @param longi - The longitude of the target area of the item for deployment or pickup
    * @param act - The action to to be performed by the drone when it reaches the latitude and longitude specified.  Allowed values are 'Pickup', 'Dropoff' and 'Return'
    * @param file - The filename of the image of the item to be picked up, deployed or returned at this location
    * @param stat - The status of the this task (queued, in progress or completed)
    */
   public void addTask(Double latitude, Double longi, String act, String file, String stat)
   {
       Task tempTask = new Task(latitude,longi,act,file,stat);
       tasklist.add(tempTask);
   }
   /**
    * Adds a new Task object to the TaskList when the status of the Task is either not known or not required
    * @param latitude - The latitude of the target area of the item for deployment or pickup
    * @param longi - The longitude of the target area of the item for deployment or pickup
    * @param act - The action to to be performed by the drone when it reaches the latitude and longitude specified.  Allowed values are 'Pickup', 'Dropoff' and 'Return'
    * @param file - The filename of the image of the item to be picked up, deployed or returned at this location
    */
   public void addTask(Double latitude, Double longi, String act, String file)
   {
       Task tempTask = new Task(latitude,longi,act,file);
       tasklist.add(tempTask);
   }
    /**
    * Adds a new Task object to the TaskList where the basic essential elements of a Task object are required
    * @param latitude - The latitude of the target area of the item for deployment or pickup
    * @param longi - The longitude of the target area of the item for deployment or pickup
    * @param file - The filename of the image of the item to be picked up, deployed or returned at this location
    */
   public void addTask(Double latitude, Double longi, String file)
   {
       Task tempTask = new Task(latitude,longi,file);
       tasklist.add(tempTask);
   }
   /**
    * Adds a previously created Task object to the TaskList
    * @param tsk - A Task object to be added to the TaskList 
    */
   public void addTask(Task tsk)
   {
       tasklist.add(tsk);
   }
   /**
    * Retrieves a Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @return A Task object located at the specified index 
    */
   public Task getTask(int index)
   {
       return tasklist.get(index);
   }
   /**
    * Returns the Latitude element value of the Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @return The latitude element value of the Task object at the specified index
    */
   public Double getLat(int index)
   {
       return tasklist.get(index).getLat();
   }
   /**
    * Sets the Latitude element value of the Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @param latitude The Latitude element value to be set
    */
   public void setLat(int index,Double latitude)
   {
       tasklist.get(index).setLat(latitude);
   }
    /**
    * Returns the Longitude element value of the Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @return The longitude element value of the Task object at the specified index
    */
   public Double getLong(int index)
   {
       return tasklist.get(index).getLong();
   }
   /**
    * Sets the Longitude element value of the Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @param longi - The Longitude element value to be set
    */
   public void setLong(int index,Double longi)
   {
       tasklist.get(index).setLong(longi);
   }
   /**
    * Returns the Action element value of the Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @return The Action element value of the Task object at the specified index
    */
   public String getAction(int index)
   {
       return tasklist.get(index).getAction();
   }
   /**
    * Sets the Action element value of the Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @param act - The Action element value to be set
    */
   public void setAction(int index,String act)
   {
       tasklist.get(index).setAction(act);
   }
   /**
    * Returns the filename element value of the Task object at a specified location in the TaskList.  This represents the image of the item that the drone is to search for
    * @param index - The index number referencing the required Task object
    * @return The filename element value of the Task object at the specified index
    */
   public String getfilename(int index)
   {
       return tasklist.get(index).getFilename();
   }
   /**
    * Sets the filename element value of the Task object at a specified location in the TaskList
    * @param index - The index number referencing the required Task object
    * @param filename - The filename element value to be set
    */
   public void setFilename(int index,String file)
   {
       tasklist.get(index).setFilename(file);
   }
   /**
    * Returns the status element value of the Task object at a specified location in the TaskList.
    * @param index - The index number referencing the required Task object
    * @return The status element value of the Task object at the specified index
    */
   public String getStatus(int index)
   {
       return tasklist.get(index).getStatus();
   }
   /**
    * Sets the status element value of the Task object at a specified location in the TaskList.  Allowable values are 'queued', 'in progress' or 'completed' 
    * @param index - The index number referencing the required Task object
    * @param stat - The status element value to be set
    */
   public void setStatus(int index,String stat)
   {
       tasklist.get(index).setStatus(stat);
   }
   /**
    * Retrieves a list of waypoints (latitude and longitude) compiled from each Task object in the TaskList
    * @return An ArrayList of  waypoints.  Note that the waypoints are represented as separate entries in the arraylist.  That is, the first entry in the arraylist is latitude of a single Task object, while the second entry is the longitude of the same Task object
    */
   public ArrayList<Double> getCoords()
   {
       ArrayList<Double> coords = new ArrayList();
       Iterator<Task> itr = tasklist.iterator();
       while(itr.hasNext())
        {
            Task tempTask = itr.next();
            coords.add(tempTask.getLat());
            coords.add(tempTask.getLong());
        }
       return coords;
   }
   
   public int getSize()
   {
       return tasklist.size();
   }

}
