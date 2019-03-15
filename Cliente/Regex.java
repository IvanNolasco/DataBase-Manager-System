package Cliente;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author navi_y_sho
 */
public class Regex {
    private ArrayList<String> comandos = new ArrayList<>(); //contendra las expresiones regulares para los comandos

    public Regex() {
        this.comandos.add("exit;");
        this.comandos.add("create +database +[a-z][a-z0-9]* *;");   
        this.comandos.add("create +table +[a-z][a-z0-9]* *\\( *[a-z][a-z0-9]* +[String|int|double|boolean|float|char] *[, *[a-z][a-z0-9]* +[Sting|int|double|boolean|float|char]]* *\\) *;");
        this.comandos.add("use +[a-z][a-z0-9]* *;");
        this.comandos.add("show +databases *;");
        this.comandos.add("show +tables *;");
        this.comandos.add("drop +database +[a-z][a-z0-9]* *;");
        this.comandos.add("drop +table +[a-z][a-z0-9]* *;");
        this.comandos.add("select *\\* *from +[a-z][a-z0-9]* *;");
        this.comandos.add("insert +into +[a-z][a-z0-9]* +values *\\( *(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false)) *(,(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false)))* *\\) *;");
        //this.comandos.add("update ;");
        this.comandos.add("update +[a-z][a-z0-9]* +set +([a-z][a-z0-9]*=(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false)))(,([a-z][a-z0-9]*=(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false))))*;");
        this.comandos.add("update +[a-z][a-z0-9]* +set +([a-z][a-z0-9]*=(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false)))(,([a-z][a-z0-9]*=(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false))))* +where +[a-z][a-z0-9]*=(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false));");
        this.comandos.add("delete +from +[a-z][a-z0-9]* +where +[a-z][a-z0-9]*=(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false));");
        this.comandos.add("delete +\\* +from +[a-z][a-z0-9]*;");
        this.comandos.add("select +\\* +from +[a-z][a-z0-9]* +where +[a-z][a-z0-9]*=(('([a-z0-9].*)')|([0-9]*(.[0-9]+)?)|(true|false));");
        this.comandos.add("(desc|describe) +[a-z][a-z0-9]* *;");
        this.comandos.add("select +count *\\( *\\* *\\) +from +[a-z][a-z0-9]* *;");
    } 
    
    //funcion que valida que una cadena sea un comando valido, con ayuda de expresiones regulares
    //si es una cadena valida, retorna un entero que indica el tipo de comando que es
    //si no es un comando valido retorna -1
    int validar(String cadena){
        cadena=cadena.replace("\"", "'");
        int resp=-1;
        
        //se recorren las expresiones regulares de los comandos para ver si hace match con alguna
        for(int i=0; i<comandos.size(); i++){
            
            Pattern p1 = Pattern.compile(comandos.get(i)); //creamos un objeto correspondiente a un determinado patrón (expresión regular)
            Matcher m1 = p1.matcher(cadena); //se crea un objeto matcher para poder hacer la comprobación
            
            if(m1.matches()){   //matches regresa true si el matcher es una cadena válida para la expresión regular
                resp=i;     //se regresa el indice del arraylist para indicar el tipo
                break;
            }          
        }
        return resp; 
    }
}
