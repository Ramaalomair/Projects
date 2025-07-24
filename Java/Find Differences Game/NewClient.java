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
import java.util.ArrayList;

/**
 *
 * @author aloma
 */
class NewClient implements Runnable
{
private Socket clientSocket;
private BufferedReader in;
private PrintWriter out;
private ArrayList<NewClient> clients;
public ArrayList<NewClient> players;
String clientName;


  public NewClient (Socket c,ArrayList<NewClient> clients, ArrayList<NewClient> players ) throws IOException
  {
    this.clientSocket = c;
    this.clients=clients;
    this.players = players;
    in= new BufferedReader (new InputStreamReader(clientSocket.getInputStream())); 
    out=new PrintWriter(clientSocket.getOutputStream(),true); 
  }
  @Override
  
  public void run ()
  {
   try{
        //read client's name from server
        clientName=in.readLine(); 
        System.out.println(clientName + " has Connected");
        
        //update the updated list to all clients
                outToAll();
       
        String msg;
        //read the message from player
        while((msg = in.readLine()) != null){
         System.out.println(clientName + " " + msg);
        
         // if player want to join the room
        if(msg.startsWith("tryToJoinRoom")){
            joinplayers();

        }
        }
   
    
}
   catch (IOException e){
       System.err.println("IO exception in new client class");
       System.err.println(e.getStackTrace());
   }
finally{
       try {
           //Remove the client because it is not connected any more 
           clients.remove(this);
           //remove player 
           players.remove(this);
           //update new list after removing client
           outToAll();
           //update new players list after removing this player
           outToPlayers();
           //close input/output streams and client socket
           in.close();
           out.close();
           clientSocket.close();
       } catch (IOException ex) {
          ex.printStackTrace();
       }
}
  }
private void outToAll() {
StringBuilder connectedList = new StringBuilder("client");
for (NewClient client :clients){
    connectedList.append(client.clientName).append(" , ");

}

//Remove comma and spaces at the end
connectedList.setLength(connectedList.length() - 2);

System.out.println("All clients displayed" + connectedList.toString());

//send names to all client in connected list
for (NewClient client :clients){
 client.out.println(connectedList.toString());
}

    }

private void joinplayers(){
    //check the ability to join room for this player
    if(players.size() < 4 ){
        out.println("isNotFull");
        System.out.println(clientName + " player will join" );
        players.add(this);
        System.out.println(clientName + " player joined the room" );
        outToPlayers();
    }
    else{//cannot join the room
        out.println("isFull");
        System.out.println( "room is full" );

    }
}

//store players names in  waiting room and printing them for all players
public void outToPlayers(){
    StringBuilder playersList = new StringBuilder("player");
        for (NewClient client :players){
          playersList.append(client.clientName).append(" , ");// storing players name in string to print them

         }

         //Remove comma and spaces at the end
         playersList.setLength(playersList.length() - 2);

         System.out.println("All players names: " + playersList.toString());

          //send player names to all players in waiting room
          for (NewClient client :players){
           client.out.println(playersList.toString());
           }
}


}
