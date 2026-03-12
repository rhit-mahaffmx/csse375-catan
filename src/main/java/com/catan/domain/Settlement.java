package com.catan.domain;

public class Settlement {

    Turn player;

    public Settlement(Turn color){
        player = color;
    }

    public Turn getColor() {
        return player;
    }
}
