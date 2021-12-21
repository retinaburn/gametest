package com.moynes;

import java.awt.*;

public class Particle {
    V2 pos;
    V2 vel;
    int maxLife;
    int life;
    int size = 10;

    public Particle(V2 pos, V2 vel, int life) {
        this.pos = pos;
        this.vel = vel;
        this.life = this.maxLife = life;
    }

    public boolean update(long dt){
        pos = pos.add((new V2(vel).multiply(dt)));
        life -= 1;
        return life > 0;
    }

    public void render(Graphics g){
        if (life>0) {
            Color currentColor = g.getColor();

            g.setColor(new Color(255, 255, 0, 255));// * (life/maxLife)));
            g.fillRect(pos.getIntX() - (size / 2), pos.getIntY() - (size / 2), size, size);

            g.setColor(currentColor);
        } else {

        }
    }

}
