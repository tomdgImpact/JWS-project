package fr.epita.assistant.jws.domain.entity;

public enum Starting {
    TOPLEFT(1, 1),
    TOPRIGHT(15, 1),
    BOTTOMLEFT(1, 13),
    BOTTOMRIGHT(15, 13);

    private int x;
    private int y;

    Starting(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
