package com.moynes;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

@Slf4j
public class GameCanvas extends JPanel {

    GameState gameState;

    int angerIndex = 0;
    int angerMaxIndex = 10;
    BufferedImage[] anger = new BufferedImage[10];
    Particle[] particles = new Particle[100];

    public GameCanvas(int width, int height, GameState gameState, KeyListener keyListener) {
        this.gameState = gameState;
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.addKeyListener(keyListener);

        try {
            anger[0] = loadImage("anger_1.png");
            anger[1] = loadImage("anger_2.png");
            anger[2] = loadImage("anger_3.png");
            anger[3] = loadImage("anger_4.png");
            anger[4] = loadImage("anger_5.png");
            anger[5] = loadImage("anger_6.png");
            anger[6] = loadImage("anger_7.png");
            anger[7] = loadImage("anger_8.png");
            anger[8] = loadImage("anger_9.png");
            anger[9] = loadImage("anger_10.png");
        } catch (IOException e) {
            log.error("{}", e.getMessage());
        }


        initParticles();
    }

    private void initParticles() {
        int x = (int)(Math.random()*800);
        int y = (int)(Math.random()*600);
        x = 400;
        y = 400;

        for (int i = 0; i < particles.length; i++) {
            double vx = Math.sin(Math.random()*180);
            double vy = -(Math.random()*1);
//            int vx = (int)(Math.random()*(5) - 3);
//            int vy = (int)(Math.random()*(5) - 3);
            int life = (int)(Math.random()*500);

            particles[i] = new Particle(new V2(x, y), new V2(vx, vy), life);
        }

    }

    long timeSinceLastAnimatedFrame = 0;

    public void render(long dT) {
        timeSinceLastAnimatedFrame += dT;
        if (timeSinceLastAnimatedFrame >= 30)
            timeSinceLastAnimatedFrame = 0;
        if (timeSinceLastAnimatedFrame == 0 && gameState.animateAnger) {
            angerIndex += 1;
            angerIndex = angerIndex % angerMaxIndex;
            if (angerIndex == 0)
                gameState.animateAnger = false;
        }
        if (gameState.animateParticles) {
            boolean atLeastOneStillAlive = false;
            for (Particle particle : particles) {
                boolean particleAlive = particle.update(dT);
                if (!atLeastOneStillAlive)
                    atLeastOneStillAlive = particleAlive;
            }
            if (!atLeastOneStillAlive){
                log.debug("Reset particles");
                gameState.animateParticles = false;
                initParticles();
            }
        }
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, this.getMaximumSize().width, this.getMaximumSize().height);

        //Let player move around grid
//        drawGrid(g);
//        drawPlayer(g, gameState.getPosX(), gameState.getPosY(), anger[angerIndex]);

        //Move grid around player
        V2 windowOrigin = recenter(gameState.getPlayerPos());

        drawGrid(g, windowOrigin);

        for (Block block : gameState.getBlockPos()) {
            V2 newBlockPos = (new V2(block.center)).add(windowOrigin);
            Color color = Color.GRAY;
            if (block.isOnFire)
                color = Color.RED;
            drawSquare(g, newBlockPos, block.halfDim, color);
        }

        if (gameState.animateAnger) {
            drawFireImpact(g);
        }

        drawPlayer(g, 0, 0, anger[angerIndex]);
        drawCircle(g, 0, 0, 2);

        drawCollisionRect(g, -gameState.playerRadius,
                -gameState.playerHeight / 2,
                gameState.playerRadius * 2,
                gameState.playerHeight);

        drawRightAlignedText(g, "PlayerP: " + gameState.getPlayerPos().toString(), 20);
        drawRightAlignedText(g, "WindowP: " + windowOrigin, 40);
//        drawRightAlignedText(g, "Max: "+this.getMaximumSize(), 60);
//        drawRightAlignedText(g, "W,H: ("+this.getWidth()+","+this.getHeight()+")", 80);
        drawRightAlignedText(g, "V: " + gameState.velocity.toString(),60);
        drawRightAlignedText(g, "A: " + gameState.acceleration.toString(), 80);

        if (gameState.animateParticles) {
            for (Particle particle : particles) {
                particle.render(g);
            }
        }
    }

    private void drawFireImpact(Graphics g) {
        int width = 200;
        int height = 100;
        Color originalColor = g.getColor();
        g.setColor(new Color(128, 0, 0, 128));
        V2 windowCoord = mapToWindow(new V2(-100, -50));
        g.fillOval(windowCoord.getIntX(), windowCoord.getIntY(), 200, 100);




        if (gameState.diagramRects.size() > 0) {
            g.setColor(Color.YELLOW);
            Rectangle r = gameState.diagramRects.get(0);
            V2 pos = new V2(r.x, r.y);
            V2 windowOrigin = recenter(gameState.getPlayerPos());
            V2 mappedPos = mapToWindow(pos).add(windowOrigin);
            g.drawRect(mappedPos.getIntX(), mappedPos.getIntY(), r.width, r.height);
        }


        g.setColor(originalColor);
    }

    private void drawCollisionRect(Graphics g, int x, int y, int width, int height) {
        Color originalColor = g.getColor();
        V2 windowCoord = mapToWindow(new V2(x, y));
        g.drawRect(windowCoord.getIntX(), windowCoord.getIntY(), width, height);


        g.setColor(originalColor);
    }

    private void drawRightAlignedText(Graphics g, String string, int y) {
        Rectangle2D fontBound = g.getFontMetrics().getStringBounds(string, g);
        int posX = this.getMaximumSize().width - fontBound.getBounds().width;
//        g.setColor(Color.BLACK);
//        g.drawLine(0, y, g.getClipBounds().width, y);
//        g.setColor(Color.BLUE);
//        g.drawLine(0, y + (int)fontBound.getCenterY(), g.getClipBounds().width, y + (int)fontBound.getCenterY());
//        g.setColor(Color.RED);
//        int bottomY = y + (int)fontBound.getCenterY() + (int)fontBound.getHeight()/2;
//        g.drawLine(0, bottomY, g.getClipBounds().width, bottomY);
//        int topY = y + (int)fontBound.getCenterY() - (int)fontBound.getHeight()/2;
//        g.drawLine(0, topY, g.getClipBounds().width, topY);

        g.setColor(Color.BLACK);
        g.fillRect(
                posX,
                y + (int) fontBound.getCenterY() - (int) fontBound.getHeight() / 2,
                fontBound.getBounds().width,
                (int) fontBound.getHeight());

        g.setColor(Color.GREEN);
        g.drawString(string,
                posX,
                y);
    }

    private V2 recenter(V2 orig) {
        return new V2(-orig.x, -orig.y);
    }

    private void drawGrid(Graphics g) {
        drawGrid(g, new V2(0, 0));
    }

    private void drawGrid(Graphics g, V2 position) {
        int gridSize = 100;
        boolean fill = false;
        int startX = (int) position.x;
        int startY = (int) -position.y;
        startX = startX % (gridSize * 2);
        startY = startY % (gridSize * 2);

        //x = starting position of player
        // - (2 * gridsize) (so we have a buffer of 2 blocks to the left)
        //We loop until x <
        // width of screen
        // + (2 * gridSize) (so we have a buffer of 2 blocks to the right)
        // + starting position of player
        for (int x = startX - 2 * gridSize; x < (this.getMaximumSize().width + (2 * gridSize) + startX); x += gridSize) {
            fill = !fill;
            for (int y = startY - 2 * gridSize; y < (this.getMaximumSize().height + (2 * gridSize) + startY); y += gridSize) {
                fill = !fill;
                if (fill)
                    g.fillRect(x, y, gridSize, gridSize);
                else
                    g.drawRect(x, y, gridSize, gridSize);
            }
        }

    }

    private void drawPlayer(Graphics g, int posX, int posY, BufferedImage bufferedImage) {
        V2 windowCoord = mapToWindow(new V2(posX, posY));
        windowCoord.x -= (double) bufferedImage.getWidth() / 2;
        windowCoord.y -= bufferedImage.getHeight();
        if (gameState.xDirection >= 0)
            g.drawImage(bufferedImage, windowCoord.getIntX(), windowCoord.getIntY(), this);
        else {
            g.drawImage(bufferedImage,
                    windowCoord.getIntX() + bufferedImage.getWidth(),
                    windowCoord.getIntY(),
                    -bufferedImage.getWidth(),
                    bufferedImage.getHeight(), this);
        }
    }

    private void drawPlayer(Graphics g, double posX, double posY, BufferedImage bufferedImage) {
        log.debug("PosX: {}, PosY: {}", posX, posY);
        V2 windowCoord = mapToWindow(new V2(posX, posY));
        windowCoord.x -= (double) bufferedImage.getWidth() / 2;
        windowCoord.y -= bufferedImage.getHeight();
        if (gameState.xDirection >= 0)
            g.drawImage(bufferedImage, windowCoord.getIntX(), windowCoord.getIntY(), this);
        else {
            g.drawImage(bufferedImage,
                    windowCoord.getIntX() + bufferedImage.getWidth(),
                    windowCoord.getIntY(),
                    -bufferedImage.getWidth(),
                    bufferedImage.getHeight(), this);
        }
    }

    void drawCircle(Graphics g, int x, int y, int radius) {
        V2 windowCoord = mapToWindow(new V2(x, y));
        g.drawOval(windowCoord.getIntX() - radius, windowCoord.getIntY() - radius, radius * 2, radius * 2);
    }

    void drawSquare(Graphics g, V2 pos, int radius, Color color) {
        int recenterX = (int) (this.getMaximumSize().width / 2 + pos.x);
        int recenterY = (int) (this.getMaximumSize().height / 2 - pos.y);
        Color orig = g.getColor();
        g.setColor(color);
        g.fillRect(recenterX - radius, recenterY - radius, radius * 2, radius * 2);
        g.setColor(Color.BLACK);
        g.drawRect(recenterX - radius, recenterY - radius, radius * 2, radius * 2);
        g.setColor(orig);
    }

    V2 mapToWindow(V2 v) {
        return new V2(this.getMaximumSize().width / 2.0 + v.x,
                this.getMaximumSize().height / 2.0 + v.y);
    }

    BufferedImage loadImage(String filename) throws IOException {
        String imagePath = "images/";
        ClassLoader loader = this.getClass().getClassLoader();
        return ImageIO.read(Objects.requireNonNull(loader.getResourceAsStream(imagePath + filename)));
    }
}



