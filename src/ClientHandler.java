import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable{
    public static final String RESET = "\u001B[0m";
    BufferedReader in;
    PrintWriter out;
    String username;

    public ClientHandler(Socket theSocket, String un){
        try{
            in = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
            out = new PrintWriter(theSocket.getOutputStream());
            username = un;
        } catch (IOException e){
            System.out.println("Hubo un error: " + e + RESET);
        }
    }

    @Override
    public void run() {
        String message;
        boolean close = false;

        while(!close){
            try{
                message = in.readLine();
                //imprimir el mensaje en consola si es para mí
                if(message.contains("@" + username)){
                    //checar si el mensaje tiene el formato correcto y si está dirigido a este usuario
                    ArrayList<Character> msglist = new ArrayList<>();
                    ArrayList<Character> destlist = new ArrayList<>();
                    String destcheck;
                    int count = 0;

                    //guardar la porción de mensaje y terminar cuando se llegue a '@'
                    while(message.charAt(count) != '@'){
                        msglist.add(message.charAt(count));
                        count++;
                    }
                    String print = msglist.toString();

                    //guardar la porción del destinatario
                    while(count < message.length()){
                        destlist.add(message.charAt(count));
                        count++;
                    }
                    destcheck = destlist.toString();

                    if(destcheck.equals("@" + username)){
                        System.out.println(print);
                    }
                }
            } catch (IOException e) {
                System.out.println("Hubo un error: " + e + RESET);
            }
        }
    }
}