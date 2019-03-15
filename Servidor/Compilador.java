
package Servidor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
 
public class Compilador{
    /** where shall the compiled class be saved to (should exist already) */
    private static String classOutputFolder = "./src";
    
    public static int codigo;
    
    public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject>
    {
        public void report(Diagnostic<? extends JavaFileObject> diagnostic)
        {
            //System.out.println("Line Number->" + diagnostic.getLineNumber());
            //System.out.println("code->" + diagnostic.getCode());
            System.out.println("Error creando tabla: "
                               + diagnostic.getMessage(Locale.ENGLISH));
            //System.out.println("Source->" + diagnostic.getSource());
            System.out.println(" ");
            //regresara 1 si hubo un error
            codigo=(int)diagnostic.getLineNumber();
        }
    }
 
    /** java File Object represents an in-memory java source file <br>
     * so there is no need to put the source file on hard disk  **/
    public static class InMemoryJavaFileObject extends SimpleJavaFileObject
    {
        private String contents = null;
 
        public InMemoryJavaFileObject(String className, String contents) throws Exception
        {
            super(URI.create("string:///" + className.replace('.', '/')
                             + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.contents = contents;
        }
 
        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException
        {
            return contents;
        }
    }
 
    /** Get a simple Java File Object ,<br>
     * It is just for demo, content of the source code is dynamic in real use case */
    private static JavaFileObject getJavaFileObject(String classEspec, String className)
    {
        
        StringBuilder contents = new StringBuilder(classEspec);
        JavaFileObject jfo = null;
        try
        {
            jfo = new InMemoryJavaFileObject("/*Servidor."+className+"*/"+className, contents.toString());
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return jfo;
    }
 
    /** compile your files by JavaCompiler */
    public static void compile(Iterable<? extends JavaFileObject> files)
    {
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        // for compilation diagnostic message processing on compilation WARNING/ERROR
        MyDiagnosticListener c = new MyDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                                                                              Locale.ENGLISH,
                                                                              null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", classOutputFolder);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        Boolean result = task.call();
        if (result == true)
        {
            System.out.println("Se creo tabla con exito! \n");
        }
    }
    
    //prepara en una cadena la descripcion de la clase
    public static String prepareClass(String nombreClase, String[][] atributos){
        int i;
        
        //class
        String spec = "/*package Servidor;*/ public class " + nombreClase + " { ";
        for(i=0; i<atributos.length; i++){
            spec = spec + "public " + atributos[i][1] + " " + atributos[i][0] + "; ";
        }
        
        //@Override toString
        spec = spec + "@Override public String toString(){ String cad = \"\"; ";
        for(i=0; i<atributos.length; i++){
            spec = spec + "cad = cad + \""+atributos[i][0]+"\" + \":\" + this." + atributos[i][0] + " + \" \"; ";
        }
        spec = spec + "return cad; }";
        
        
        //getters
        for(i=0; i<atributos.length; i++){
            spec = spec + " public " + atributos[i][1] + " get" + atributos[i][0] + "(){ return this." + atributos[i][0] + "; }";
        }
        
        //setters
        for(i=0; i<atributos.length; i++){
            spec = spec + " public void set" + atributos[i][0] + "(" + atributos[i][1] + " " + atributos[i][0] + "){ this." + atributos[i][0] + "=" + atributos[i][0] + "; }";
        }
        
        spec = spec + " } ";
        
        System.out.println(spec);
        return spec;
    }
    
    //trata de crear la clase y devuelve el codigo de error si es que hubo
    public int crearClase(String nombreTabla, String[][] atributos){
        //la bandera de codigo 0 es porque no hay error
        codigo = 0;
        //prepara la cadena para la clase 
        JavaFileObject file = getJavaFileObject(prepareClass(nombreTabla, atributos), nombreTabla);
        Iterable<? extends JavaFileObject> files = Arrays.asList(file);
        compile(files);
        
        return codigo;
    }
    
    //Crea un objeto de la clase 
    public Object crearObjeto(String nombreClase, String[] valores, String[][] atrTipo){
        File file = new File(classOutputFolder);
        Object tabla=null;
        try{
            // Convert File to a URL
            URL url = file.toURL(); // file:/classes/demo
            URL[] urls = new URL[] { url };
            // Create a new class loader with the directory
            ClassLoader loader = new URLClassLoader(urls);
            // Load in the class; Class.childclass should be located in
            // the directory file:/class/demo/
            Class thisClass = loader.loadClass(nombreClase);
            String ClassName = nombreClase;
            Class<?> tClass = Class.forName(ClassName); // convert string classname to class
            tabla = tClass.newInstance(); // invoke empty constructor
            String methodName = "", methodName2=""; 
            Method setNameMethod = null, getNameMethod=null;
            
            //System.out.println("Genero bien instancia "+tabla.getClass().getName());
            
            //recorre el arreglo de atributos y tipos para poder invocar correctamente a los setteres de la clase
            for(int i=0;i<atrTipo.length;i++){
                methodName = "set"+atrTipo[i][0];   //se asigna el nombre del método de la forma set + nombre_atributo
                //methodName2 = "get"+atrTipo[i][0];
                
                
                //le asigna a setNameMethod el tipo de dato que va a enviarse
                switch(atrTipo[i][1]){
                    case "String":
                        setNameMethod = tabla.getClass().getMethod(methodName, String.class);//se crea el método con el nombre y tipo de parámetros que recibirá
                        setNameMethod.invoke(tabla, valores[i]); //se invoca al metodo y guarda el valor ingresado por el cliente
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        String valor = (String) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valor);
                    break;
                    
                    case "int":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error 
                        int valori=Integer.parseInt(valores[i]);    //se hace cast para poder guardar el valor correctamente
                        setNameMethod = tabla.getClass().getMethod(methodName, int.class);//se crea el método con el nombre del metódo y tipo de parámetros que recibirá
                        setNameMethod.invoke(tabla, valori); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        int valorig = (int) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorig);
                    break;  
                    
                    case "boolean":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error
                        if(valores[i].equals("true")|| valores[i].equals("false")) //se comprueba que lo ingresado sea de tipo booleano
                        {
                           boolean valorb=Boolean.parseBoolean(valores[i]);    //se hace cast para poder guardar el valor correctamente
                           setNameMethod = tabla.getClass().getMethod(methodName, boolean.class);//se crea el método con el nombre del método y tipo de parámetros que recibirá
                           setNameMethod.invoke(tabla, valorb); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
                        }
                        else
                           setNameMethod.invoke(tabla, valores[i]); //se crea un error para entrar al catch                       
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        boolean valorbg = (boolean) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorbg);
                    break;
                    
                    case "char":
                        setNameMethod = tabla.getClass().getMethod(methodName, char.class);//se crea el método con el nombre del método y tipo de parámetros que recibirá
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error en el tipo char
                        if(valores[i].equals(""))
                            setNameMethod.invoke(tabla, " ");
                        if(valores[i].length()<=1)       //se verifica si el valor ingresado es de un sólo caractper
                            setNameMethod.invoke(tabla, valores[i].charAt(0)); //se invoca al metodo y guarda el valor ingresado por el cliente
                        else
                            setNameMethod.invoke(tabla, valores[i]);    //se crea un error para entrar al catch
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        char valorc = (char) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorc);
                    break;
                    
                    case "float":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error
                        float valorf = Float.parseFloat(valores[i]);    //se hace cast para poder guardar el valor correctamente
                        setNameMethod = tabla.getClass().getMethod(methodName, float.class);//se crea el método con el nombre del método y tipo de parámetros que recibirá
                        setNameMethod.invoke(tabla, valorf); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        float valorfg = (float) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorfg);
                    break;
                    
                    case "double":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error 
                        double valord = Double.parseDouble(valores[i]);  //se hace cast para poder guardar el valor correctamente
                        setNameMethod = tabla.getClass().getMethod(methodName, double.class);
                        setNameMethod.invoke(tabla, valord); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        double valordg = (double) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valordg);
                    break;    
                }
            }
            
        }catch(Exception e){
            tabla=null;
           e.printStackTrace();
        }
        return tabla;
    }
    
    //se busca un objeto que tenga en al atributo requerido el valor ingresado por el cliente
    public boolean buscarRegistro(String nombreClase,String tipo, String valor,String atributo, Object tabla){
        boolean respuesta=false;
        try{
            String methodName = "get"+atributo; //nombre del método que se utilizara para recuperar el valor dado el atributo
            Method getNameMethod = tabla.getClass().getMethod(methodName); 
            String valor_guardado="";  //se guardara el valor recuperado por el método get

                //se verifica el tipo del valor que va a abtonerse para hacerse el cast adecuado
                switch(tipo){
                    case "String":
                        String valors = (String) getNameMethod.invoke(tabla); // explicit cast
                        //System.out.println("Valor devuelto por metodo:"+valors);    //se imprime el valor que se obtuvo
                        valor_guardado=valors;
                    break;
                    
                    case "int":
                        int valorig = (int) getNameMethod.invoke(tabla); // explicit cast
                        //System.out.println("Valor devuelto por metodo:"+valorig);
                        valor_guardado=Integer.toString(valorig);
                    break;  
                    
                    case "boolean":
                        boolean valorbg = (boolean) getNameMethod.invoke(tabla); // explicit cast
                        //System.out.println("Valor devuelto por metodo:"+valorbg);
                        valor_guardado=Boolean.toString(valorbg);
                    break;
                    
                    case "char":
                        char valorc = (char) getNameMethod.invoke(tabla); // explicit cast
                        //System.out.println("Valor devuelto por metodo:"+valorc);
                        valor_guardado=Character.toString(valorc);
                    break;
                    
                    case "float":
                        float valorfg = (float) getNameMethod.invoke(tabla); // explicit cast
                        //System.out.println("Valor devuelto por metodo:"+valorfg);
                        valor_guardado=Float.toString(valorfg);
                    break;
                    
                    case "double":
                        double valordg = (double) getNameMethod.invoke(tabla); // explicit cast
                        //System.out.println("Valor devuelto por metodo:"+valordg);
                        valor_guardado=Double.toString(valordg);
                    break;    
                }
                valor=valor.replace("'", ""); //se remplazan las comillas por cadena vacía para evitar errores al comparar cadenas o caracteres
                if(valor.equals(valor_guardado))
                    respuesta=true; //retorna true cuando encuentra un registro con el atributo y valor ingresados por el cliente
            
        }catch(Exception e){
            tabla=null;
           e.printStackTrace();
        }
        return respuesta;
    } 
    
    //Crea un nuevo objeto de la clase considerando las modificaciones indicadas
    public Object modificarObjeto(String nombreClase, String[][] modif, String[][] atrTipo, Object tabla){
        try{
            String methodName = "", methodName2=""; 
            Method setNameMethod = null, getNameMethod=null;
            
            String[] valores= new String[atrTipo.length];  //arreglo para guardar los nuevos valores del objeto
            
            //se llena el arreglo de nulos
            for(int i=0;i<atrTipo.length;i++)
                valores[i]=null;
            
            //se recorre el arreglo para ordenar los valores en el orden en el que aparecen en la clase
            for(int i=0;i<atrTipo.length;i++){
                for(int l=0;l<modif.length;l++){
                    if(atrTipo[i][0].equals(modif[l][0])){  //si el aitributo a modificar coincide con el de la clase
                        valores[i]=modif[l][1];  //se guardan el valor y el atributo en el nuevo arreglo
                    }  
                }
            }
            
            //recorre el arreglo de atributos y tipos para poder invocar correctamente a los setteres de la clase
            for(int i=0;i<atrTipo.length;i++){
                methodName = "set"+atrTipo[i][0];   //se asigna el nombre del método de la forma set + nombre_atributo
                //methodName2 = "get"+atrTipo[i][0];
                
                //el procedimiento para hacer el 'setter' solo se realiza cuando es un valor a modificar (es decir se guardo en valores como diferente de nulo)       
                if(valores[i] == null){
                }
                else{
                    valores[i].replace("'", "");
                //le asigna a setNameMethod el tipo de dato que va a enviarse
                switch(atrTipo[i][1]){
                    case "String":
                        setNameMethod = tabla.getClass().getMethod(methodName, String.class);//se crea el método con el nombre y tipo de parámetros que recibirá
                        setNameMethod.invoke(tabla, valores[i]); //se invoca al metodo y guarda el valor ingresado por el cliente
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        String valor = (String) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valor);
                    break;
                    
                    case "int":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error 
                        int valori=Integer.parseInt(valores[i]);    //se hace cast para poder guardar el valor correctamente
                        setNameMethod = tabla.getClass().getMethod(methodName, int.class);//se crea el método con el nombre del metódo y tipo de parámetros que recibirá
                        setNameMethod.invoke(tabla, valori); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        int valorig = (int) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorig);
                    break;  
                    
                    case "boolean":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error
                        if(valores[i].equals("true")|| valores[i].equals("false")) //se comprueba que lo ingresado sea de tipo booleano
                        {
                           boolean valorb=Boolean.parseBoolean(valores[i]);    //se hace cast para poder guardar el valor correctamente
                           setNameMethod = tabla.getClass().getMethod(methodName, boolean.class);//se crea el método con el nombre del método y tipo de parámetros que recibirá
                           setNameMethod.invoke(tabla, valorb); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
                        }
                        else
                           setNameMethod.invoke(tabla, valores[i]); //se crea un error para entrar al catch                       
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        boolean valorbg = (boolean) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorbg);
                    break;
                    
                    case "char":
                        setNameMethod = tabla.getClass().getMethod(methodName, char.class);//se crea el método con el nombre del método y tipo de parámetros que recibirá
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error en el tipo char
                        if(valores[i].equals(""))
                            setNameMethod.invoke(tabla, " ");
                        if(valores[i].length()<=1)       //se verifica si el valor ingresado es de un sólo caractper
                            setNameMethod.invoke(tabla, valores[i].charAt(0)); //se invoca al metodo y guarda el valor ingresado por el cliente
                        else
                            setNameMethod.invoke(tabla, valores[i]);    //se crea un error para entrar al catch
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        char valorc = (char) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorc);
                    break;
                    
                    case "float":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error
                        float valorf = Float.parseFloat(valores[i]);    //se hace cast para poder guardar el valor correctamente
                        setNameMethod = tabla.getClass().getMethod(methodName, float.class);//se crea el método con el nombre del método y tipo de parámetros que recibirá
                        setNameMethod.invoke(tabla, valorf); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        float valorfg = (float) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valorfg);
                    break;
                    
                    case "double":
                        valores[i]=valores[i].replace(" ", ""); //se remplaza cadena vacía por null para no generar error 
                        double valord = Double.parseDouble(valores[i]);  //se hace cast para poder guardar el valor correctamente
                        setNameMethod = tabla.getClass().getMethod(methodName, double.class);
                        setNameMethod.invoke(tabla, valord); //se invoca al metodo y guarda el valor ingresado por el cliente con el debido cast
//                        getNameMethod = tabla.getClass().getMethod(methodName2);
//                        double valordg = (double) getNameMethod.invoke(tabla); // explicit cast
//                        System.out.println("Valor devuelto por metodo:"+valordg);
                    break;    
                }
                }
            }
            
        }catch(Exception e){
            tabla=null;
           e.printStackTrace();
        }
        return tabla;
    }    
}

