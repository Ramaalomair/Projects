/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication3;

/**
 *
 * @author aloma
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.IOException;
import static java.lang.System.out;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.Timer;



public class NewServer
{
    public WaitingRoom frame = new WaitingRoom();
    private static ArrayList<NewClient> clients = new ArrayList<>();
    public static ArrayList<NewClient> players = new ArrayList<>();
    private static int timeLeft = 30;
    public static void main(String[] args) throws IOException
            
    {
        try{   
        
            //Create server socket
         ServerSocket serverSocket = new ServerSocket(9090);
         System.out.println("Running server...");
         //while network always on accept client
        while (true){
         System.out.println("Waiting for client connection");
         
         //accept the client connection
         Socket client = serverSocket.accept();
         System.out.println("Connected to client");
        PrintWriter out= new PrintWriter(client.getOutputStream(),true); 
         //create a new client handler thread for each client
         NewClient clientThread=new NewClient(client,clients,players); // new thread 
         clients.add(clientThread);
         //start the client handler thread
         new Thread (clientThread).start();
         out.println("TIME:" + timeLeft);
        }
        }catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
            e.printStackTrace();
    }
}
    
    private static void startCountdown() {
         
        Timer timer;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;

                // إرسال الوقت المتبقي لجميع المستخدمين
                for (NewClient client : clients) {
                    System.out.println("Time left: " +timeLeft);
                    //out.println("TIME:" + timeLeft);
                }

                if (timeLeft <= 0) {
                    ((Timer) e.getSource()).stop();
                    // هنا يمكن بدء اللعبة أو إخطار اللاعبين بنهاية الوقت
                }
            }
        });
        timer.start();
    }
}








