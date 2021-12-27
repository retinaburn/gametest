package com.moynes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class V2 {
    public double x = 0.0;
    public double y = 0.0;

    public int getIntX(){
        return (int)x;
    }
    public int getIntY(){
        return (int)y;
    }

    public V2(){}
    public V2(double x, double y){
        this.x = x;
        this.y = y;
    }
    public V2(V2 v){
        this.x = v.x;
        this.y = v.y;
    }
    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }
    public double getLength(){
        return Math.sqrt(x * x + y * y);
    }

    public V2 normalize(){
        double magnitude = getLength();
        if (magnitude == 0){
            x = 0;
            y = 0;
        } else {
            x /= magnitude;
            y /= magnitude;
        }
        return this;
    }

    public V2 multiply(double d){
        x *= d;
        y *= d;
        return this;
    }

    public V2 subtract(V2 v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }
    public V2 add(V2 v){
        x += v.x;
        y += v.y;
        return this;
    }
    public boolean equals(V2 v){
        return x==v.x && y==v.y;
    }

    public String toString(){
        return "("+x+","+y+")";
    }
}
