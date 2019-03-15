/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import Cliente.Comando;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author navi_y_sho
 */
public class Gestor {
    
    private HashMap <String, HashMap<String, LinkedList<Object>> > bases = new HashMap <String, HashMap<String, LinkedList<Object>>>();     //contendra las bases de datos del gestor
    private String bdActual = null;      //representa la base de datos que se este utilizando
    
    //hashmap que contendra las especificaciones de las tablas (nombre_atributos y tipo)
    private HashMap <String, String[][]> tablaEspec = new HashMap <String, String[][]>();
    
    String ejecutarComando(Comando com){
        String respuesta;
        switch(com.getTipo()){
            case 1:
                respuesta = ejecutarCreateDatabase(com.getComando());
            break;
            
            case 2:
                //se verifica que se este usando una BD
                if(bdActual!=null)
                    respuesta = ejecutarCreateTable(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 3:
                respuesta = ejecutarUse(com.getComando());
            break;
            
            case 4:
                respuesta = ejecutarShowDatabases();
            break;
            
            case 5:
                if(bdActual!=null)
                    respuesta = ejecutarShowTables();
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 6:
                respuesta = ejecutarDropDatabase(com.getComando());
            break;
            
            case 7:
                if(bdActual!=null)
                    respuesta = ejecutarDropTable(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
         
            case 8:
                if(bdActual!=null)
                    respuesta = ejecutarSelectAllFrom(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 9:
                if(bdActual!=null)
                    respuesta = ejecutarInsertInto(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
//            case 10:
//                //if(bdActual!=null)
//                    respuesta = ejecutarUpdate(com.getComando());
//                //else
//                    //respuesta = "Hubo un error reconociendo el comando";
//            break;
            
            case 11:
                if(bdActual!=null)
                    respuesta = ejecutarUpdateWhere(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 12:
                if(bdActual!=null)
                    respuesta = ejecutarDeleteWhere(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 13:
                if(bdActual!=null)
                    respuesta = ejecutarDelete(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 14:
                if(bdActual!=null)
                    respuesta = ejecutarSelectWhere(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 15:
                if(bdActual!=null)
                    respuesta = ejecutarDescribe(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            case 16:
                if(bdActual!=null)
                    respuesta = ejecutarSelectCount(com.getComando());
                else
                    respuesta = "No se esta usando ninguna base de datos";
            break;
            
            default:
                respuesta = "Hubo un error reconociendo el comando";    
        }
        return respuesta;
    }
    
    String ejecutarCreateDatabase(String comando){
        String respuesta;
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreBD = palabras[2].replace(";", "");  //se recupera el nombre de la BD sin el ";"
        
        if(bases.containsKey(nombreBD)){        //si ya existe una BD con el nombre dado
            respuesta = "Ya existe la base de datos " + nombreBD;
        }else{
            HashMap <String, LinkedList <Object> > tablas = new HashMap <String, LinkedList <Object> >(); //se crea un hashmap para las tablas de esa BD
            this.bases.put(nombreBD, tablas);       //se crea el registro de esa base de datos deltro del hashmap
            respuesta =  "Se ha creado la base de datos " + nombreBD;
        }
        
        return respuesta;
    }
    
    String ejecutarShowDatabases(){
        String respuesta = "Bases de datos:";
        
        if(this.bases.isEmpty()){
            respuesta = "No existen bases de datos";
        } else{
            //si no esta vacio el hashmap, se recorre con un iterator
            Set set = this.bases.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
               Map.Entry mentry = (Map.Entry)iterator.next();
               String aux = "\n" + mentry.getKey();
               respuesta = respuesta.concat(aux);
            }
        }
        return respuesta;
    }
    
    String ejecutarDropDatabase(String comando){
        String respuesta = "";
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreBD = palabras[2].replace(";", "");  //se recupera el nombre de la BD sin el ";"
        
        if(this.bases.containsKey(nombreBD)){        //si existe una BD con el nombre dado
            this.bases.remove(nombreBD);        //se revueme la BD 
            respuesta = "Se ha eliminado la base de datos " + nombreBD;
            if(this.bdActual.equals(nombreBD))
                bdActual = null;        //si esa base de datos era la que estaba en uso, se sale de ahi xd
        }else{
            //si no existe una BD con el nombre dado
            respuesta =  "No existe la base de datos " + nombreBD;
        } 
        return respuesta;
    }
    
    String ejecutarUse(String comando){
        String respuesta = "";
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreBD = palabras[1].replace(";", "");  //se recupera el nombre de la BD sin el ";"
        
        if(this.bases.containsKey(nombreBD)){        //si existe una BD con el nombre dado
            this.bdActual = nombreBD;       //la BD en uso sera la especificada
            respuesta = "Se esta usando la base de datos " + nombreBD;
        }else{
            //si no existe una BD con el nombre dado
            respuesta =  "No existe la base de datos " + nombreBD;
        } 

        return respuesta;
    }
    
    String ejecutarCreateTable(String comando){
        String respuesta = "";  
        
        //procedimiento para dividir el token en "create table (nombre)" y "(atributo tipo, atributo tipo...)
        StringTokenizer tokens = new StringTokenizer(comando,"(");  
        String [] subcad = new String[tokens.countTokens()];      
        int i = 0;
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            subcad[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        //procedimiento para obtener el nombre de la tabla
        StringTokenizer tokens2 = new StringTokenizer(subcad[0]);  
        String [] subcad2 = new String[tokens2.countTokens()];      
        int j = 0;
        while(tokens2.hasMoreTokens()){      //mientras haya tokens
            subcad2[j] = (String) tokens2.nextElement();    //se recupera el token y se guarda en arreglo
            j++;
        }
        String nombreTabla = subcad2[2];  //se recupera el nombre de la tabla
        
        //procedimiento para obtener los atributos y su tipo
        StringTokenizer tokens3 = new StringTokenizer(subcad[1].replace(");", ""), ","); //se elimina ");"
        String [] subcad3 = new String[tokens3.countTokens()];
        int k = 0;
        while(tokens3.hasMoreTokens()){
            subcad3[k] = (String) tokens3.nextElement();
            k++;
        }
        
        String [][] atrTipo = new String[subcad3.length][2];
        
        for(int l=0;l<subcad3.length;l++)
        {
            StringTokenizer tokens4 = new StringTokenizer(subcad3[l]); //se separa segun los espacios
            String [] subcad4 = new String[tokens4.countTokens()];
            int m = 0;
            while(tokens4.hasMoreTokens()){
                subcad4[m] = (String) tokens4.nextElement();
                m++;
            }
            atrTipo[l][0] = subcad4[0];
            atrTipo[l][1] = subcad4[1];
            //System.out.println("atri: " + atrTipo[l][0] + " tipo: " + atrTipo[l][1]);
        }
        
        //si la tabla se llama igual que las bases de datos
        if(nombreTabla.equals(this.bdActual)){
           respuesta = "No se puede crear la tabla por que se llama igual que la base de datos"; 
        }else{
            if(this.bases.get(this.bdActual).containsKey(nombreTabla)){
                //si ya existe una tabla con ese nombre, no se guarda
                respuesta = "Ya existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
            }
            else{
                //se trata de crear la clase (con el nombre introducido por el cliente + nombreBD) para la tabla
                Compilador compi = new Compilador();
                //se concatena el nombre de la BD para que se puedan crear tablas con el mismo nombre en diferentes BD
                int cod = compi.crearClase(nombreTabla+"_"+this.bdActual, atrTipo);
                if(cod == 0){
                    //no hubo error al crear la clase, se procede a crear la tabla en el hashmap con el nombre de la tabla introducido por el cliente
                    LinkedList <Object> lista = new LinkedList <Object>();
                    this.bases.get(this.bdActual).put(nombreTabla, lista);
                    this.tablaEspec.put(nombreTabla+"_"+this.bdActual, atrTipo);
                    respuesta = "Se creo la tabla " + nombreTabla;
                }else{
                    //hubo error en la tabla
                    respuesta = "No se pudo crear la tabla, quien te enseño sql?";
                }
            }
        }
        
        return respuesta;
    }
    
    String ejecutarShowTables(){
        String respuesta = "Tablas en " + this.bdActual + ": ";
        
        if(this.bases.get(this.bdActual).isEmpty()){
            respuesta = "No existen tablas dentro de la base de datos " + this.bdActual;
        }else{
            //si no esta vacio el hashmap, se recorre con un iterator
            Set set = this.bases.get(this.bdActual).entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
               Map.Entry mentry = (Map.Entry)iterator.next();
               String aux = "\n" + mentry.getKey();
               respuesta = respuesta.concat(aux);
            }
        
        }
        
        return respuesta;
    }
    
    String ejecutarDropTable(String comando){
        String respuesta = "";
        
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreTabla = palabras[2].replace(";", "");  //se recupera el nombre de la tabla sin el ";"
        
        if(this.bases.get(this.bdActual).containsKey(nombreTabla)){ //si dentro de la BD actual ya existe una tabla llamada igual
            this.bases.get(this.bdActual).remove(nombreTabla);        //se revueme la tabla dentro de esa BD
            respuesta = "Se ha eliminado la tabla " + nombreTabla + " de la base de datos " + this.bdActual;
        }else{
            //si no existe una tabla con el mismo nombre dentro de la BD
            respuesta =  "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        } 
        
        return respuesta;
    }

    String ejecutarInsertInto(String comando){
        String respuesta="";
        //procedimiento para dividir el token en "insert into nombre_tabla values" y "(valor, valor...)
        StringTokenizer tokens = new StringTokenizer(comando,"(");  
        String [] subcad = new String[tokens.countTokens()];      
        int i = 0;
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            subcad[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        //procedimiento para obtener el nombre de la tabla
        StringTokenizer tokens2 = new StringTokenizer(subcad[0]);
        String [] subcad2 = new String[tokens2.countTokens()];
        int j=0;
        while(tokens2.hasMoreTokens()){     //mientras haya tokens
            subcad2[j] = (String) tokens2.nextElement();    //se recupera el token y se guarda en arreglo
            j++; 
        }
        //se recupera el nombre de la tabla y se concatena en nombre de la base de datos 
        String nombreTabla = subcad2[2] + "_" + this.bdActual;
        //nombreTabla = nombre de la tabla + nombre de la BD
        //subcad2[2] = nombre de la tabla
        
        //verificar que exista la tabla donde se va a hacer el insert
        if(!this.bases.get(this.bdActual).containsKey(subcad2[2])){
            //si no existe
            respuesta = "No existe la tabla " + subcad2[2] + " dentro de la base de datos " + this.bdActual;
        }else{
            //si existe hace el procedimiento para obtener los valores
            StringTokenizer tokens3 = new StringTokenizer(subcad[1].replace(");", "").replace("'", ""), ","); //se elimina ");" y comillas
            String [] subcad3 = new String[tokens3.countTokens()];
            int k = 0;
            while(tokens3.hasMoreTokens()){
                subcad3[k] = (String) tokens3.nextElement();
                //System.out.println("valor"+k+": "+ subcad3[k]);
                k++;
            }
            Compilador compi = new Compilador();
            //se obtiene el objeto de la clase (tabla) y se inserta en el hashmap
            Object objeto = compi.crearObjeto(nombreTabla, subcad3, this.tablaEspec.get(nombreTabla));
            if(objeto != null){
                respuesta = "Se creo el registro con exito"; 
                //ingresar el registro en el linkedlist del hashmap
                this.bases.get(this.bdActual).get(subcad2[2]).add(objeto);
            }
            else{
                respuesta = "No pudo crearse el registro con exito";            
            } 
        }      
        return respuesta;
    }
    
    String ejecutarSelectAllFrom(String comando){
        String respuesta="";
        
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreTabla = palabras[3].replace(";", "");  //se recupera el nombre de la tabla sin el ";"
        //si no existe
        if(!this.bases.get(this.bdActual).containsKey(nombreTabla)) //se comprueba si la tabla ingresada existe dentor de la bd
            respuesta = "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        else{
            if(this.bases.get(this.bdActual).get(nombreTabla).isEmpty()){
                //comprueba si esta vacia la tabla
                respuesta = "No existen registros en la tabla " + nombreTabla;
            }else{
                //si hay registros recorre los elementos de la lista ligada y los imprime
                for(i=0; i<this.bases.get(this.bdActual).get(nombreTabla).size();i++){
                    respuesta = respuesta + this.bases.get(this.bdActual).get(nombreTabla).get(i).toString() +"\n";
                }
            }
        }
        
        return respuesta;
    }
    
    String ejecutarDelete(String comando){
        String respuesta = "";
        
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreTabla = palabras[3].replace(";", "");  //se recupera el nombre de la tabla sin el ";"
        
        if(!this.bases.get(this.bdActual).containsKey(nombreTabla))//se comprueba si la tabla ingresada existe dentor de la bd
            respuesta = "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        else{
            if(this.bases.get(this.bdActual).get(nombreTabla).isEmpty()){
                //comprueba si esta vacia la tabla
                respuesta = "No existen registros que borrar en la tabla " + nombreTabla;
            }else{
                //si hay registros limpia la lista ligada
                this.bases.get(this.bdActual).get(nombreTabla).clear();
                respuesta = "Se han eliminado todos los registros de la tabla " + nombreTabla;
            }
        }
        
        return respuesta;
    }
    
    String ejecutarDeleteWhere(String comando){
        String respuesta = "";
        //procedimiento para separar las cadena en "delete from nombreTabla where atributo" y "valor"
        StringTokenizer tokens = new StringTokenizer(comando,"=");      //se parte el comando en tokens
        String [] subcad = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            subcad[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        //procedimiento para obtener el nombre de la tabla y del atributo
        StringTokenizer tokens2 = new StringTokenizer(subcad[0]);      //se parte el comando en tokens
        String [] subcad2 = new String[tokens2.countTokens()];      //arreglo que contendra los tokens
        int j = 0;
        
        while(tokens2.hasMoreTokens()){      //mientras haya tokens
            subcad2[j] = (String) tokens2.nextElement();    //se recupera el token y se guarda en arreglo
            j++;
        }
        
        String nombreTabla = subcad2[2];  //se recupera el nombre de la tabla 
        String atributo = subcad2[4];       //se recupera el atributo de la condición
        String valor = subcad[1].replace(";", "");  //se recupera el valor para realizar la condición
        
        if(!this.bases.get(this.bdActual).containsKey(nombreTabla))//se comprueba si la tabla ingresada existe dentro de la bd
            respuesta = "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        else{
            if(this.bases.get(this.bdActual).get(nombreTabla).isEmpty()){
                //comprueba si esta vacia la tabla
                respuesta = "No existen registros que borrar en la tabla " + nombreTabla;
            }else{
                String[][] atrib = this.tablaEspec.get(nombreTabla+"_"+this.bdActual); //se recupera los atributos y tipos de la tabla requerida
                respuesta="No se encontró el atributo "+atributo+" en la tabla "+subcad2[2];    //respuesta default por si llega al fin del arreglo y no encuentra el atributo
                
                for(i=0;i<atrib.length;i++){
                    if(atrib[i][0].equals(atributo)){  //se verifica si el atributo ingresado por el usuario existe en la tabla
                        respuesta="No se encontro ningun valor de coincidencia"; //respuesta por default por si no hay coincidencias
                        int cont = 0; //para contar el numero de registros que coinciden y se van a borrar
                        Compilador compi = new Compilador();
                        //se recorre la lista ligada para recuperar en orden inverso
                        for(int k=this.bases.get(this.bdActual).get(nombreTabla).size()-1; k>=0;k--){
                            //se recupera el objeto en cada indice de la lista ligada
                            Object obj = this.bases.get(this.bdActual).get(nombreTabla).get(k);
                            //se busca si el registro cumple con la condicion dada
                            if(compi.buscarRegistro(nombreTabla+"_"+this.bdActual,atrib[i][1],valor,atributo,obj)){
                                //si se encontro que cumple con la condicion se elimina
                                cont ++;
                                this.bases.get(this.bdActual).get(nombreTabla).remove(k);
                                respuesta = "Se eliminaron " + cont + " registros";
                            }
                        }
                        
                        break;
                    }
                }
            }
        }
        return respuesta;
    }
    
    String ejecutarUpdateWhere(String comando){
        String respuesta = "";
        
        //procedimiento para separar la cadena en 'update' 'nombreTabla' 'set' 'atributos' 'where' 'condicion'
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] subcad = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            subcad[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreTabla = subcad[1];  //se recupera el nombre de la tabla
        
        //procedimiento para encontrar los atributos y valores a modificar
        StringTokenizer tokens2 = new StringTokenizer(subcad[3],",");      //se parte el comando en tokens
        String [] subcad2 = new String[tokens2.countTokens()];      //arreglo que contendra los tokens
        int j = 0;
        
        while(tokens2.hasMoreTokens()){      //mientras haya tokens
            subcad2[j] = (String) tokens2.nextElement();    //se recupera el token y se guarda en arreglo
            j++;
        }
        
        //procedimiento para encontrar el atributo y valor de la condición
        StringTokenizer tokens3 = new StringTokenizer(subcad[5],"=");      //se parte el comando en tokens
        String [] subcad3 = new String[tokens3.countTokens()];      //arreglo que contendra los tokens
        int k = 0;
        
        while(tokens3.hasMoreTokens()){      //mientras haya tokens
            subcad3[k] = (String) tokens3.nextElement();    //se recupera el token y se guarda en arreglo
            k++;
        }
        
        String atributo = subcad3[0];   //se guarda el atributo de la condición para buscar el registro
        String valor = subcad3[1].replace(";", "");      //se guarda el valor de la condición para buscar el registro
        
        String [][] atrVal = new String[subcad2.length][2];
        
        //procedimiento para guardar los atributos y sus valores a los que se realizarán las modificaciones
        for(int l=0;l<subcad2.length;l++)
        {
            StringTokenizer tokens4 = new StringTokenizer(subcad2[l],"="); //se separa segun los iguales
            String [] subcad4 = new String[tokens4.countTokens()];
            int m = 0;
            while(tokens4.hasMoreTokens()){
                subcad4[m] = (String) tokens4.nextElement();
                m++;
            }
            atrVal[l][0] = subcad4[0];
            atrVal[l][1] = subcad4[1];
            System.out.println("atri: " + atrVal[l][0] + " valor: " + atrVal[l][1]);
        }
        
        if(!this.bases.get(this.bdActual).containsKey(nombreTabla))//se comprueba si la tabla ingresada existe dentro de la bd
            respuesta = "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        else{
            if(this.bases.get(this.bdActual).get(nombreTabla).isEmpty()){
                //comprueba si esta vacia la tabla
                respuesta = "No existen registros en la tabla " + nombreTabla;
            }else{
                //se recupera la especificacion de esa tabla (nombretributos, tipo)
                String[][] atrib = this.tablaEspec.get(nombreTabla+"_"+this.bdActual); //se recupera los atributos y tipos de la tabla requerida
                respuesta="No se encontró el atributo "+atributo+" en la tabla "+nombreTabla;    //respuesta default por si llega al fin del arreglo y no encuentra el atributo
                
                for(i=0;i<atrib.length;i++){
                    if(atrib[i][0].equals(atributo)){  //se verifica si el atributo ingresado por el usuario existe en la tabla
                        respuesta = "Se modificaron los registros correctamente";
                        int cont = 0, ban=1;
                        
                        Compilador compi = new Compilador();
                        //se recorre la lista ligada
                        for( k=0; k<this.bases.get(this.bdActual).get(nombreTabla).size(); k++){
                            
                            if(ban==0){
                                respuesta = "Hubo un error tratando de actualizar los registros";
                                break;
                            }
                                
                            
                            //se recupera el objeto en cada indice de la lista ligada
                            Object obj = this.bases.get(this.bdActual).get(nombreTabla).get(k);
                            System.out.println("recuperado: " + obj.toString());
                            System.out.println(nombreTabla+"_"+this.bdActual);
                            System.out.println(atrib);
                            System.out.println(atrVal);
                            System.out.println(obj.toString());
                            //se busca si el objeto cumple con la condicion dada
                            if(compi.buscarRegistro(nombreTabla+"_"+this.bdActual,atrib[i][1],valor,atributo,obj)){
                                System.out.println("Entro aqui");
                                //si se encontro que cumple con la condicion se hace el update de los datos en el objeto
                                obj = compi.modificarObjeto(nombreTabla+"_"+this.bdActual, atrVal, atrib, obj);
                                
                                
                                if(obj != null ){       //si el objeto no es null, significa que se hizo bien el update
                                    //se remplaza el nuevo objeto en la lista ligada
                                    this.bases.get(this.bdActual).get(nombreTabla).set(k, obj);
                                    cont++;
                                }else{
                                    System.out.println("El objeto fue nulo");
                                    ban=0; //hubo error  modificando un objeto
                                }
                                
                            }
                        }
                        
                        if(cont==0)
                            respuesta = "No se encontraron coincidencias con el criterio de busqueda";
                        break;
                    }
                }
                
            }
        }
        
        return respuesta;
    }
    
    String ejecutarSelectWhere(String comando){
        String respuesta = "";
        
        //procedimiento para separar las cadena en "select * from nombreTabla where atributo" y "valor"
        StringTokenizer tokens = new StringTokenizer(comando,"=");      //se parte el comando en tokens
        String [] subcad = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            subcad[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        //procedimiento para obtener el nombre de la tabla y del atributo
        StringTokenizer tokens2 = new StringTokenizer(subcad[0]);      //se parte el comando en tokens
        String [] subcad2 = new String[tokens2.countTokens()];      //arreglo que contendra los tokens
        int j = 0;
        
        while(tokens2.hasMoreTokens()){      //mientras haya tokens
            subcad2[j] = (String) tokens2.nextElement();    //se recupera el token y se guarda en arreglo
            j++;
        }
        
        String nombreTabla = subcad2[3];  //se recupera el nombre de la tabla 
        String atributo = subcad2[5];       //se recupera el atributo de la condición
        String valor = subcad[1].replace(";", "");  //se recupera el valor para realizar la condición
        
        if(!this.bases.get(this.bdActual).containsKey(nombreTabla))//se comprueba si la tabla ingresada existe dentro de la bd
            respuesta = "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        else{
            if(this.bases.get(this.bdActual).get(nombreTabla).isEmpty()){
                //comprueba si esta vacia la tabla
                respuesta = "No existen registros en la tabla " + nombreTabla;
            }else{
                String[][] atrib = this.tablaEspec.get(nombreTabla+"_"+this.bdActual); //se recupera los atributos y tipos de la tabla requerida
                respuesta="No se encontró el atributo "+atributo+" en la tabla "+nombreTabla;    //respuesta default por si llega al fin del arreglo y no encuentra el atributo
                
                for(i=0;i<atrib.length;i++){
                    if(atrib[i][0].equals(atributo)){  //se verifica si el atributo ingresado por el usuario existe en la tabla
                        respuesta = "";
                        int cont = 0;
                        Compilador compi = new Compilador();
                        //se recorre la lista ligada
                        for(int k=0; k<this.bases.get(this.bdActual).get(nombreTabla).size(); k++){
                            //se recupera el objeto en cada indice de la lista ligada
                            Object obj = this.bases.get(this.bdActual).get(nombreTabla).get(k);
                            //se busca si el registro cumple con la condicion dada
                            if(compi.buscarRegistro(nombreTabla+"_"+this.bdActual,atrib[i][1],valor,atributo,obj)){
                                //si se encontro que cumple con la condicion se elimina
                                respuesta = respuesta + this.bases.get(this.bdActual).get(nombreTabla).get(k).toString() +"\n";
                                cont++;
                            }
                        }
                        if(cont==0)
                            respuesta = "No se encontraron coincidencias con el criterio de busqueda";
                        break;
                    }
                }
            }
        }
        
        return respuesta;
    }
    
    String ejecutarSelectCount(String comando){
        String respuesta="";
        
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreTabla = palabras[palabras.length-1].replace(";", "");  //se recupera el nombre de la tabla sin el ";"
        //si no existe
        if(!this.bases.get(this.bdActual).containsKey(nombreTabla)) //se comprueba si la tabla ingresada existe dentor de la bd
            respuesta = "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        else{
            int count = this.bases.get(this.bdActual).get(nombreTabla).size();
            respuesta = "Existen " + count + " registros en la tabla " + nombreTabla + "";
        }
        
        return respuesta;
    }
    
    String ejecutarDescribe(String comando){
        String respuesta = "";
        StringTokenizer tokens = new StringTokenizer(comando);      //se parte el comando en tokens
        String [] palabras = new String[tokens.countTokens()];      //arreglo que contendra los tokens
        int i = 0;
        
        while(tokens.hasMoreTokens()){      //mientras haya tokens
            palabras[i] = (String) tokens.nextElement();    //se recupera el token y se guarda en arreglo
            i++;
        }
        
        String nombreTabla = palabras[1].replace(";", "");  //se recupera el nombre de la tabla sin el ;
        String nombreDescTabla = nombreTabla + "_" + this.bdActual; 
        
        if(!this.bases.get(this.bdActual).containsKey(nombreTabla)) //se comprueba si la tabla ingresada existe dentor de la bd
            respuesta = "No existe la tabla " + nombreTabla + " dentro de la base de datos " + this.bdActual;
        else{
            String[][] descTabla = this.tablaEspec.get(nombreDescTabla);
            respuesta = nombreTabla + ": \n";
            for(i=0; i<descTabla.length; i++){
                respuesta = respuesta + descTabla[i][0] + " " + descTabla[i][1] + "\n";
            }
        }

        return respuesta;
    }
}
