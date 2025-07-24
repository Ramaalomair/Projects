/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerConnection implements Runnable{
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;
    private FirstFrame frame1; 
    private WaitingRoom frame2;
    boolean isFull;

    public ServerConnection (Socket s , FirstFrame F, WaitingRoom frame) throws IOException{
        server=s;
        frame1 = F;
        frame2 = frame;
        if (frame1 == null) {
        System.err.println("Frame1 object is null in ServerConnection constructor!");
    } else {
        System.out.println("Frame1 object initialized correctly.");
    }
        in= new BufferedReader (new InputStreamReader(server.getInputStream())); 
        out=new PrintWriter(server.getOutputStream(),true); 
    }
    @Override
    public void run(){
        
            String serverResponse;
            try {
                
              

                while((serverResponse = in.readLine()) != null){
                System.out.println("Recieved from Server: " + serverResponse);

                
                
                 if(serverResponse.contains("isNotFull")){
                        isFull= false;
                        frame2.setVisible(true);
                        System.out.println("is not full assigned as:  " + isFull);
                        while((serverResponse = in.readLine()) != null){//reading names from server because the list is not full
                              System.out.println("names from Server: " + serverResponse);
                              if(serverResponse.startsWith("player")){
                               frame2.playertUpdated(serverResponse.substring(6));
                              
                              }
                 }
                 }
                 else{
                 if(serverResponse.contains("isFull")){
                    System.err.println("room is full in run() method!");
                    frame1.waitingRoomIsFull();
                    System.out.println("the display option pane is full");

 
                 }
                 }
                     
                
                if(serverResponse.startsWith("client")){
                 if(frame1 != null){
                   frame1.ClientUpdated(serverResponse.substring(6));}
                 else{
                    System.err.println("frame1 is null in run() method!");
                    
 
                 }
                     
                }
                
               }
                
                
               
               
                    
                
            } catch (IOException ex) {
                ex.printStackTrace();
            
            }
            finally{
                try{
                    in.close();
                    
                }catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
    }
    
}



