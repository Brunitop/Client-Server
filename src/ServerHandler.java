import java.util.Scanner;
public class ServerHandler implements Runnable{
    String closer;
    public ServerHandler(){
        closer = null;
    }
    public void run() {
        Scanner ns = new Scanner(System.in);

        System.out.println("Presione cualquier tecla y ENTER para terminar los procesos del servidor");
        closer = ns.next();
        MSPServer.serverClosed();
    }
}
