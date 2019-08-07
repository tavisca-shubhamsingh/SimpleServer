import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler extends Thread {

    Socket socket;

    public ClientHandler(Socket socket)
    {
        this.socket=socket;
    }

    @Override
    public void run()
    {
        try(
                BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()))
        )
        {
            System.out.println("Thread created. Processing the request now");
            String header=in.readLine();
            System.out.println(header);
            String[] parts=header.split(" ");
            if(parts[0].equalsIgnoreCase("GET"))
            {
                try {
                    if (parts[1].length() == 1) {
                        servePage(socket,"index.html");
                    } else {
                        String fileName = parts[1].substring(1);
                        if(fileName.contains(".jpg")|| fileName.contains(".ico"))
                        {
                            serveImage(socket,fileName);
                        }
                        else {
                            InputStream file = new FileInputStream(fileName);
                            servePage(socket, fileName);
                        }
                    }
                   }
                catch (Exception e)
                {
                    servePage(socket,"fileNotFound.html");
                }
            }
            else
            {
                servePage(socket,"badRequest.html");
            }
            System.out.println("request processed");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    void servePage(Socket socket,String fileName) throws IOException {
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());

                BufferedReader in = new BufferedReader(new FileReader(fileName));
        ) {
            String str = "";
            String content = "";
            while (true) {
                if ((str = in.readLine()) != null) {
                    content += str;
                } else {
                    break;
                }
            }

            String httpResponse = returnCommonHeader();
            httpResponse += "Content-Type: text/html\r\n\r\n";
            httpResponse += content;
            bufferedOutputStream.write(httpResponse.getBytes("UTF-8"));
            bufferedOutputStream.flush();
            socket.close();
        } catch (Exception e) {
            System.out.println("error");
        }

    }

    private void serveImage(Socket socket, String fileName) {

        try(
                BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream bufferedInputStream=new BufferedInputStream(new FileInputStream(fileName))
        ) {

            String httpResponse = returnCommonHeader();
            httpResponse+="Content-Type: image/jpeg\r\n";
            httpResponse+="Content-Length:"+bufferedInputStream.available();
            httpResponse+="\r\n\r\n";

            bufferedOutputStream.write(httpResponse.getBytes("UTF-8"));

            bufferedOutputStream.flush();
            byte[] buffer=new byte[bufferedInputStream.available()];
            int bytesRead;
            while ((bytesRead= bufferedInputStream.read(buffer))!=-1)
            {
                bufferedOutputStream.write(buffer,0,bytesRead);
            }
            bufferedOutputStream.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String returnCommonHeader() {
        Date d = new Date();
        String httpResponse = "HTTP/1.1 200 OK\r\n";
        httpResponse += "Server: My Java HTTP Server : 1.0\r\n";
        httpResponse += "Date: " + d.toString() + "\r\n";
        return httpResponse;
    }
}
