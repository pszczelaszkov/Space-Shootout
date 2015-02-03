package spaceShootout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class ServerLink 
{
    
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private String host;
    private int port;
            
    ServerLink(String host,int port)
    {
        this.host = host;
        this.port = port;
        socket = null;
        out = null;
        in = null;
    }
    
    boolean connect()
    {
        try
        {
            socket = new Socket(host,port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } 
        catch (UnknownHostException e) 
        {
            System.out.println(e.getMessage());
            return false;
        } 
        catch (IOException e) 
        {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    void disconnect()
    {
        try 
        {
            socket.close();
        } 
        catch (IOException e) 
        {
            Logger.getLogger(e.getMessage());
        }       
        socket = null;
        out = null;
        in = null;
    }
    
    String getStatus()
    {
        send((char)0x01 + "ENDSTREAM");
        return read();
    }
    
    String getNewToken()
    {
        send((char)0x02 + "ENDSTREAM");
        return read();
    }
    
    boolean updateSession(String token,int level)
    {
        send((char)0x03 + token + ":" + String.valueOf(level) + "ENDSTREAM");
        return Boolean.parseBoolean(read());
    }
    
    String getArrowPosition(String token)
    {
        send((char)0x04 + token + "ENDSTREAM");
        return read();
    }
    
    int getTopLevel()
    {
        send((char)0x05 + "ENDSTREAM");
        return Integer.parseInt(read());
    }
    
    void send(String message)
    {
        out.flush();
        out.println(message);
    }
    
    String read()
    {
        try
        {
                return in.readLine();
        }
        catch(Exception e)
        {  
                System.out.println(e.getMessage());      	
                return null;
        }
    }
}
