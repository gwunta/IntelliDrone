
package Intellidrone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.StringTokenizer;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;



/**
 * The Telint class provides a telnet interface for the GPS and Magnet classes.
 *
 * @author Audey Isaacs
 * @version 0.1
 *
 * Changelog:
 * 0.1 Created class and some methods
 *
 */
public class Telint
 implements Runnable 
{

	static TelnetClient tcin = null;
	static TelnetClient tcout = null;
	InputStream input = null;
	OutputStream inputout = null;
	OutputStream output = null;
	
	
	public Telint(String ip, int port)
	 throws InterruptedException, IOException 
	{
	
		tcin = new TelnetClient();
		tcout = new TelnetClient();
		
		try 
		{
			tcin.connect(ip, port);
			tcout.connect(ip, port);
		} catch (Exception e) 
		{
			System.out.println("Caught" + e.toString());
		}
		
		input = tcin.getInputStream();
		inputout = tcin.getOutputStream();
		output = tcout.getOutputStream();
		
		Thread.sleep(3000);
		
		byte[] buff = new byte[1024];
		String initstr = "cat /dev/ttyPA0 \n";
                buff = initstr.getBytes("UTF-16LE"); 
		inputout.write(buff);
		inputout.flush();
		
		/*String initstro = "cat > /dev/ttyPA0 \n";
		buff = initstro.getBytes("UTF-16LE"); 
		output.write(buff);
		output.flush();*/
		
		//(new Thread(this)).start();
	}
	
	public void close() 
	{
	
		try 
		{
			tcin.disconnect();
			tcout.disconnect();
		
		} 
		catch (IOException e) 
		{
			
			System.out.println("IOException " + e);
		}	
	}
	
	public synchronized String recv() 
	{
	
		String str = new String("none");
		
		try 
		{
			if(input.available() > 0) 
			{
				byte[] inbuff = new byte[input.available()];
				input.read(inbuff, 0, input.available());
				str = new String(inbuff);
			}
		} 
		catch (Exception e) {}
		
		return str;
	}
	
	public synchronized void send(String strsend) 
	{
		byte[] buff = new byte[1024];

		try
		{
			String str = "echo \"" + strsend + "\" > /dev/ttyPA0 \n";
			
			buff = str.getBytes("UTF-16LE"); 
			output.write(buff);
			output.flush();
		}
		catch (Exception e) 
		{
			System.out.println("Caught Exception" + e.toString());
		}			
	}
	
	public void run() 
	{
		
		try 
		{
			Thread.sleep(1000);
		} catch (Exception e) 
		{
			return;
		}
	}
}	
