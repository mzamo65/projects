import javax.imageio.IIOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

class Server{
    public static void main(String args[])throws Exception{

        Scanner sc=new Scanner(System.in);

        System.out.println("Welcome to the server");
        System.out.println("Select an action: ");
        String request = sc.nextLine();
        if(request.equals("view")){view();}
        else{download();}

    }

    public static void view(){
        Path path = Paths.get("/home/j/jlxkhu003/Documents/Networks");
        try(Stream<Path> subPaths = Files.walk(path)){
            subPaths.forEach((System.out::println));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void download() throws IOException {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter File Name: ");

        String filename=sc.nextLine();
        sc.close();
        while(true)
        {
            //create server socket on port 5000
            ServerSocket ss=new ServerSocket(5000);
            System.out.println ("Waiting for request");
            Socket s=ss.accept();
            System.out.println ("Connected With "+s.getInetAddress().toString());
            DataInputStream din=new DataInputStream(s.getInputStream());
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());
            try{
                String str="";

                str=din.readUTF();
                System.out.println("SendGet....Ok");

                if(!str.equals("stop")){

                    System.out.println("Sending File: "+filename);
                    dout.writeUTF(filename);
                    dout.flush();

                    File f=new File(filename);
                    FileInputStream fin=new FileInputStream(f);
                    long sz=(int) f.length();

                    byte b[]=new byte [1024];

                    int read;

                    dout.writeUTF(Long.toString(sz));
                    dout.flush();

                    System.out.println ("Size: "+sz);
                    System.out.println ("Buf size: "+ss.getReceiveBufferSize());

                    while((read = fin.read(b)) != -1){
                        dout.write(b, 0, read);
                        dout.flush();
                    }
                    fin.close();

                    System.out.println("..ok");
                    dout.flush();
                }
                dout.writeUTF("stop");
                System.out.println("Send Complete");
                dout.flush();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println("An error occured");
            }
            din.close();
            s.close();
            ss.close();
        }
    }
}
