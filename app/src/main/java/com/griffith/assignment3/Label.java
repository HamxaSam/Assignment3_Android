package com.griffith.assignment3;

/**
 * Created by 42900 on 28/04/2017 for Assignment3.
 */

public class Label {
    private String label;
    private int coord;

    public Label(String label, int coord) {
        this.label = label;
        this.coord = coord;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCoord() {
        return coord;
    }

    public void setCoord(int coord) {
        this.coord = coord;
    }

    @Override
    public String toString() {
        return "Label{" +
                "label='" + label + '\'' +
                ", coord=" + coord +
                '}';
    }
}
