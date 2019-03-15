
package Cliente;

import java.net.*;
import java.io.*;

/**
 *
 * @author navi_y_sho
 */
public class Cliente {
    public static void main(String[] args) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));   //buffer para leer del teclado
            InetAddress dir = null;  
            String host = "";   //direccion IP (del servidor) a la cual se va a concectar el cliente
            int pto = 9000;     //puerto (del servidor) al cual se va a conectar el cliente
            System.out.print("\nEscribe la direccion del servidor: ");
            host = br.readLine();
            try{
                dir = InetAddress.getByName(host);
            }
            catch(UnknownHostException u){
                System.err.println("Direccion no valida\n");
                main(args);
            }
            
            Socket c1 = new Socket(dir, pto);       //se crea el socket y se asocia a la dir ip y al puerto especificado
            ObjectOutputStream oos = new ObjectOutputStream(c1.getOutputStream());  //flujo para escribir en el socket
            ObjectInputStream ois = new ObjectInputStream(c1.getInputStream());    //flujo para leer del socket
            System.out.println("\nConexion establecida con el servidor");
            
            while(true){
                System.out.print("\nmysql> ");  //se muestra el prompt
                String cadena = br.readLine().toLowerCase();     //se lee la cadena introducido en el prompt y se pasa a mincusulas
                cadena=cadena.replace("string", "String").replace("\"", "'");   //se remplaza string por String en la cadena para crear correctamente la clase y comillas por comilla simple
                //se verifica que la cadena sea un comando valido
                Regex expr = new Regex();
                int tipo = expr.validar(cadena);
                
                if(tipo>-1){
                    //si la cadena es valida se crea un objeto con el comando (cadena) y el tipo
                    Comando com1 = new Comando(cadena,tipo);
                    
                    oos.writeObject(com1);        //escribe el objeto comando en el socket para enviarlo al servidor
                    oos.flush();     
                    
                    if(tipo == 0){ //el tipo 0 es un comando "exit;"
                        //si el cliente desea salir de la aplicacion
                        System.out.println("\nTermino la aplicacion\n");
                        oos.close();     //se cierra la escritura en el socket
                        ois.close();    //se cierra la lectura del socket
                        c1.close();     //se cierran conexion con el servidor
                        System.exit(0);     //se cierra aplicacion
                    }else{
                        //si es otro tipo de comando, se espera la respuesta del servidor
                        String resp = ois.readUTF();    //se lee la respuesta del servidor a traves del socket
                        System.out.println(resp);       //se escribe la respuesta en el prompt
                    }
                        
                }else{
                    //el comando no es valido
                    System.out.println("\nComando no valido, intente de nuevo por favor");  
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
