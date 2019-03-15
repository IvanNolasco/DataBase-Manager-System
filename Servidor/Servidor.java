
package Servidor;

import Cliente.Comando;
import java.net.*;
import java.io.*;
import java.util.HashMap;

/**
 *
 * @author navi_y_sho
 */
public class Servidor {
    
    public static void main(String[] args) {
        
        //objeto que contendra la funcionalidad del gestor de base de datos
        Gestor sgbd = new Gestor();
        
        try{
        int pto = 9000;
        ServerSocket s = new ServerSocket(pto);     //se asocia el socket al puerto especificado
        System.out.println("Servicio iniciado, esperando clientes");
        
        for(;;){
            Socket c1 = s.accept();     //se acepta la conexion de un cliente
            System.out.println("Cliente conectado desde" + c1.getInetAddress()+":"+c1.getPort());
            ObjectOutputStream oos = new ObjectOutputStream(c1.getOutputStream());      //para escribir en el socket
            ObjectInputStream ois = new ObjectInputStream(c1.getInputStream());         //para leer del socket
            
            while(true){
                Comando com1 = (Comando) ois.readObject();      //se lee el objeto comando
                
                if(com1.getTipo()==0){    //se verifica que no el comando no sea de tipo 0 "exit;"
                    System.out.println("\nCliente termino aplicacion\n");
                    oos.close();     //se cierra buffer de lectura del socket
                    ois.close();     //se cierra la escritura en el socket  
                    c1.close();     //se cierra conexion con el cliente
                    break;
                } else{
                    //si es de otro tipo de comando
                    System.out.println("\nComando recibido: "+com1.getComando());
                    String respuesta = sgbd.ejecutarComando(com1);
                    oos.writeUTF(respuesta);       //se envia respuesta al cliente
                    oos.flush();
                }
            }
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    }
}
