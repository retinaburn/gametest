package com.moynes;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;
import java.util.*;

@Data
@Slf4j
public class GameState {
    float speed = 0.1f; //m/s2
    V2 velocity = new V2();
    V2 playerPos = new V2(0, 0);
    V2 acceleration = new V2();
    double adjustmentOffsetWhenCollides = 0.01;
    List<Block> blockPos = new ArrayList<>();
    V2 v1 = new V2();
    V2 v2 = new V2();
    V2 v3 = new V2();
    //    int blockHalfDim = 10;
    int playerRadius = 50;
    int playerHeight = 10;

    int MAP_SIZE = 21;
    int NUM_ROOMS = 40;


    List<Rectangle> diagramRects = new ArrayList<>();

    boolean animateAnger = false;
    boolean animateParticles = false;
    int xDirection = 1;

    boolean reCenter = false;

    private List<Block> drawRoom(boolean leftDoor, boolean upDoor, boolean rightDoor, boolean downDoor, int startingX, int startingY) {
        int endingX = startingX + 700;
        int endingY = startingY + 500;
        List<Block> result = new ArrayList<>();
        for (int x = startingX; x <= endingX; x += Block.halfDim * 2) {
            if ((upDoor) && (x >= startingX + 300 && x <= startingX + 400)) {
            } else {
                result.add(new Block(new V2(x, endingY)));
            }
            if ((downDoor) && (x >= startingX + 300 && x <= startingX + 400)) {
            } else {
                result.add(new Block(new V2(x, startingY)));
            }
        }
        for (int y = startingY; y <= endingY; y += Block.halfDim * 2) {
            if ((leftDoor) && (y >= startingY + 200 && y <= startingY + 300)) {
            } else {
                result.add(new Block(new V2(startingX, y)));
            }
            if ((rightDoor) && (y >= startingY + 200 && y <= startingY + 300)) {
            } else {
                result.add(new Block(endingX, y));
            }
        }
        return result;
    }


    private List<Block> buildMap(Map<String, Boolean> map) {
        List<Block> blockList = new ArrayList<>();
        for (int y = -1 * (MAP_SIZE / 2); y <= (MAP_SIZE / 2); y++) {
            int startingY = -250 + (y * 500);
            for (int x = -1 * (MAP_SIZE / 2); x <= (MAP_SIZE / 2); x++) {
                int startingX = -350 + (x * 700);
                if (map.get(key(x, y)) == Boolean.TRUE) {
                    blockList.addAll(
                            drawRoom(
                                    mapGet(map, key(x - 1, y)),
                                    mapGet(map, key(x, y + 1)),
                                    mapGet(map, key(x + 1, y)),
                                    mapGet(map, key(x, y - 1)),
                                    startingX, startingY));
                }
            }
        }
        return blockList;
    }

    private Boolean mapGet(Map<String, Boolean> map, String key) {
        Boolean result = map.get(key);
        return Objects.requireNonNullElse(result, Boolean.FALSE);
    }


    private Map<String, Boolean> initMap() {
        int x = 0, y = 0;
        Map<String, Boolean> map = new HashMap<>();
        map.put(key(x, y), true);
        for (int i = 0; i < NUM_ROOMS; i++) {
            int newDirection = (int) (Math.random() * 4);
            switch (newDirection) {
                case 0 -> x--;
                case 1 -> y++;
                case 2 -> x++;
                case 3 -> y--;
            }
            map.put(key(x, y), true);
        }
        log.debug("Map");
        String mapString = "\n";
        for (y = MAP_SIZE / 2; y > -1 * (MAP_SIZE / 2); y--) {

            for (x = -1 * (MAP_SIZE / 2); x <= (MAP_SIZE / 2); x++) {
                if (map.get(key(x, y)) == Boolean.TRUE) {
                    mapString = mapString + 1;
                } else {
                    mapString = mapString + 0;
                }
            }
            mapString += "\n";
        }
        log.debug(mapString);
        return map;

    }

    private String key(int x, int y) {
        return x + "," + y;
    }

    public GameState() {
        Map<String, Boolean> map = initMap();
        blockPos.addAll(buildMap(map));
        //blockPos.addAll(initRoom());

    }

    public boolean update(KeyState keyState, long dt) {
        V2 newAcceleration = new V2();
        diagramRects.clear();

//        if (keyState.UP_KEY_DOWN ||
//                keyState.DOWN_KEY_DOWN ||
//                keyState.LEFT_KEY_DOWN ||
//                keyState.RIGHT_KEY_DOWN)
//            hasChange = true;
        if (keyState.UP_KEY_DOWN) {
            newAcceleration.y += 1;
        }
        if (keyState.DOWN_KEY_DOWN) {
            newAcceleration.y -= 1;
        }
        if (keyState.LEFT_KEY_DOWN) {
            xDirection = -1;
            newAcceleration.x -= 1;
        }
        if (keyState.RIGHT_KEY_DOWN) {
            xDirection = 1;
            newAcceleration.x += 1;
        }
        if (keyState.SPACE_KEY_DOWN) {
            animateAnger = true;
            animateParticles = true;
//            hasChange = true;
        }
        newAcceleration = newAcceleration.normalize().multiply(speed);
        newAcceleration.add(new V2(velocity).multiply(-0.08));

        V2 newPlayerPos = new V2(playerPos);
        //p = 1/2*a*sq(t) + v't + p
//        log.debug("new accel: {}, sq(dt): {}, vel: {}, newPlayerPos: {}",
//                newAcceleration, (dt*dt), velocity, newPlayerPos);
        newPlayerPos = (new V2(newAcceleration))
                .multiply(dt * dt)
                .multiply(0.5)
                .add(new V2(velocity).multiply(dt))
                .add(new V2(newPlayerPos));
        velocity = (new V2(newAcceleration).multiply(dt).add(velocity));
        acceleration = newAcceleration;

        if (velocity.getLength() < 0.01)
            velocity = new V2();
        if (acceleration.getLength() < 0.01)
            acceleration = new V2();

        //does newPlayerPos intersect with the blocks
        boolean isCollides = false;
//        for(Vector2D block: blockPos){
//            Rectangle r = new Rectangle((int)block.x - blockRadius, (int)block.y-blockRadius, blockRadius*2, blockRadius*2);
//            if (r.contains(newPlayerPos.x, newPlayerPos.y)){
//                log.debug("Collision!");
//                isCollides = true;
//                break;
//            }
//        }

        Rectangle p = new Rectangle((int) newPlayerPos.x - playerRadius,
                (int) newPlayerPos.y - playerHeight / 2,
                playerRadius * 2,
                playerHeight);

        Rectangle fire = new Rectangle((int) newPlayerPos.x - 2 * playerRadius,
                (int) newPlayerPos.y - playerRadius,
                4 * playerRadius,
                100);
        diagramRects.add(fire);

        if (countCollisions(p) >= 2){
            return true;
        }

        for (Block block : blockPos) {
            //if the block was on fire last time, lets make it not on fire
            if (block.isOnFire) {
                block.isOnFire = false;
//                hasChange = true;
            }

            //does newPlayerPos as rectangle intersect with the blocks
            Rectangle r = new Rectangle((int) block.center.x - Block.halfDim,
                    (int) block.center.y - Block.halfDim,
                    Block.halfDim * 2,
                    Block.halfDim * 2);


            if (r.intersects(p)) {
                log.debug("Collision!");
                v1 = new V2(block.center.getIntX(), block.center.getIntY());
                v2 = new V2(newPlayerPos.x, newPlayerPos.y);

                v3 = new V2(v2).subtract(v1);
                log.debug("{},{},{}", v1, v2, v3);
//
                log.debug("Initial collision = TRUE");
                isCollides = true;
                if (r.contains(rectangleGetBottomRight(p))) {
                    if (r.contains(rectangleGetTopRight(p))) {
                        log.debug("Right Side");
                    } else {
                        log.debug("Bottom Right");
                        if (!r.contains(v3.getIntX(), v3.getIntY())) {
                            log.debug("Found alternate path");
                            newPlayerPos.x += v3.getIntX() * adjustmentOffsetWhenCollides;
                            newPlayerPos.y += v3.getIntY() * adjustmentOffsetWhenCollides; //playerPos.y;
                            isCollides = false;
                        }
                    }
                } else if (r.contains(rectangleGetBottomLeft(p))){
                    if (r.contains(rectangleGetTopLeft(p))){
                        log.debug("Left Side");
                    } else {
                        log.debug("Bottom Left");
                        if (!r.contains(v3.getIntX(), v3.getIntY())){
                            log.debug("Found alternate path");
                            newPlayerPos.x += v3.getIntX() * adjustmentOffsetWhenCollides;
                            newPlayerPos.y += v3.getIntY() * adjustmentOffsetWhenCollides; //playerPos.y;
                            isCollides = false;
                        }
                    }
                } else if (r.contains(rectangleGetTopLeft(p))){
                    log.debug("Top Left");
                    log.debug("Found alternate path");
                    newPlayerPos.x += v3.getIntX() * adjustmentOffsetWhenCollides;
                    newPlayerPos.y += v3.getIntY() * adjustmentOffsetWhenCollides; //playerPos.y;
                    isCollides = false;
                } else if (r.contains(rectangleGetTopRight(p))){
                    log.debug("Top Right");
                    log.debug("Found alternate path");
                    newPlayerPos.x += v3.getIntX() * adjustmentOffsetWhenCollides;
                    newPlayerPos.y += v3.getIntY() * adjustmentOffsetWhenCollides; //playerPos.y;
                    isCollides = false;
                }

//                }
                break;
            }

            if (animateAnger) {
                if (r.intersects(fire)) {
                    block.isOnFire = true;
                }
            }

        }

        if (!isCollides) {
            playerPos = new V2(newPlayerPos);
        } else {
            velocity = new V2(0, 0);
        }

//        if (animateAnger)
//            hasChange = true;
        return true;
    }

    public int countCollisions(Rectangle p){
        int result = 0;
        V2 collisionBlock = new V2();
        for (Block block: blockPos){
            Rectangle r = new Rectangle((int) block.center.x - Block.halfDim,
                    (int) block.center.y - Block.halfDim,
                    Block.halfDim * 2,
                    Block.halfDim * 2);
            if (p.intersects(r) && !collisionBlock.equals(block.center)){
                result++;
                if (result == 1){
                    collisionBlock = new V2(block.center);
                }
                //log.debug("Num Collisions = {}, block = {}", result, block.center);
            }
        }
        return result;
    }

    public Point rectangleGetBottomRight(Rectangle r) {
        return new Point(r.x + r.width, r.y);
    }

    public Point rectangleGetTopRight(Rectangle r) {
        return new Point(r.x + r.width, r.y + r.height);
    }

    public Point rectangleGetBottomLeft(Rectangle r){
        return new Point(r.x, r.y);
    }
    public Point rectangleGetTopLeft(Rectangle r){
        return new Point(r.x, r.y+r.height);
    }
}
