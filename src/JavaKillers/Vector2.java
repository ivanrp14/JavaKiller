/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JavaKillers;

import java.io.Serializable;

/**
 *
 * @author ivanr
 */
public class Vector2 implements Serializable{

    public double x, y;

    public Vector2() {
         this.x = 0;
        this.y = 0;
    }
    
    public Vector2(double _x, double _y) {
        this.x = _x;
        this.y = _y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    
    
   
}
