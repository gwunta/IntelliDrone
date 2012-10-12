
package mapper;

/**
 *
 * @author Grant Boxall
 */

public class Task
{
   private double lat;
   private double longitude;
   private String action;
   private String filename;
   private String status;

   //Constructor
   public Task(Double latitude, Double longi, String act, String file, String stat)
   {
       this.lat = latitude;
       this.longitude = longi;
       this.action = act;
       this.filename = file;
       this.status = stat;
   }
   public Task(Double latitude, Double longi, String act, String file)
   {
       this.lat = latitude;
       this.longitude = longi;
       this.action = act;
       this.filename = file;
   }
   public Task(Double latitude, Double longi, String file)
   {
       this.lat = latitude;
       this.longitude = longi;
       this.filename = file;
   }
   public Double getLat()
   {
       return lat;
   }
   public void setLat(Double latitude)
   {
       this.lat = latitude;
   }
   public Double getLong()
   {
       return longitude;
   }
   public void setLong(Double longi)
   {
       this.longitude = longi;
   }
   public String getAction()
   {
       return action;
   }
   public void setAction(String act)
   {
       this.action = act;
   }
   public String getFilename()
   {
       return filename;
   }
   public void setFilename(String file)
   {
       this.filename = file;
   }
   public String getStatus()
   {
       return status;
   }
   public void setStatus(String stat)
   {
       this.status = stat;
   }
}
