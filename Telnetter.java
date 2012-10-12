/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapper;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.InputStream;
import java.io.PrintStream;

public class Telnetter 
{
    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;
    private String prompt = "#";
    private String server;
    private String user;
    private String password;    


    public Telnetter(String server, String user, String password)
    {
        try 
        {
            // Connect to the specified server
            telnet.connect(server, 23);

            // Get input and output stream references
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            // Log the user on
            //readUntil("login: ");
            //write(user);
            //readUntil("Password: ");
            //write(password);

            // Advance to a prompt
            readUntil(prompt + " ");
            write(password);
        } catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    public void su(String password)
    {
        try
        {
            write("cat /dev/ttyPA0");
            //readUntil("Password: ");
            //write(password);
            //prompt = "#";
            readUntil(prompt + " ");
        } catch (Exception e) 
            {
                 e.printStackTrace();
            }
    }

    public String readUntil(String pattern)
    {
        try {
            char lastChar = pattern.charAt(pattern.length()-1);
            StringBuffer sb = new StringBuffer();
            boolean found = false;
            char ch = (char) in.read();
            while (true) 
            {
                System.out.print(ch);
                sb.append(ch);
                if (ch == lastChar)
                {
                    if (sb.toString().endsWith(pattern)) 
                    {
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
        }

        public void write(String value) {
        try 
        {
            out.println(value);
            out.flush();
            System.out.println(value);
        } catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    public String sendCommand(String command)
    {
        try 
        {
            write(command);
            return readUntil(prompt + " ");
        } catch (Exception e) 
            {
                 e.printStackTrace();
            }
        return null;
    }

    public void disconnect()
    {
        try 
        {
            telnet.disconnect();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

     public static void main(String[] args) 
    {
        try
        {
            Telnetter telnet = new Telnetter("192.168.0.1","username","password");
            telnet.sendCommand("ls ");
            telnet.disconnect();
        } catch (Exception e) 
            {
                e.printStackTrace();
            }
    }
}
