package com.moynes;

public class Block {
    V2 center;
    int halfDim = 50;

    boolean isOnFire = false;

    public Block(int centerX, int centerY){
        center = new V2(centerX, centerY);
    }

    public Block(V2 center){
        this.center = center;
    }

}
