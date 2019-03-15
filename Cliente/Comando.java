/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.Serializable;

/**
 *
 * @author navi_
 */
public class Comando implements Serializable{
    private String comando;
    private int tipo;

    public Comando(String comando, int tipo) {
        this.comando = comando;
        this.tipo = tipo;
    }

    public String getComando() {
        return comando;
    }

    public int getTipo() {
        return tipo;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
