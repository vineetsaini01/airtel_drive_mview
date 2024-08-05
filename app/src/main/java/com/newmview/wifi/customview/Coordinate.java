package com.newmview.wifi.customview;

public class Coordinate implements Comparable<Coordinate> {
    /** x int This is the x coordinate of the coordinate. */
    public int x;
    /** y int This is the y coordinate of the coordinate. */
    public int y;
    /** skip int This is the skip value on the graph. */
    private int skip;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /** angle double This is the angle of the coordinate and the x coordinate axis. */
    private double angle = -Double.MAX_VALUE;
    public String label;
    public double getAngle(){
        return angle;
    }

    /**
     * This method sets the angle with the x axis after computing it.
     * @param angle double This is the angle calculated.
     */
    public void setAngle(double angle){
        this.angle = angle;
    }

    /**
     * This method sorts the coordinates in counter-clockwise order, by comparing their angles to the x axis.
     * @param o Coordinate This is the comparison coordinate.
     * @return int This is the end result sent to the comparable class.
     */
    public int compareTo(Coordinate o){
        if (this.angle-o.angle < 0){
            return -1;
        }
        else if (this.angle == o.angle){
            if (this.y < o.y){
                return -1;
            }
            return 1;
        }
        return 1;
    }

    /**
     * This method returns a String representation of a coordinate.
     * @return String This is the String representation of the coordinate.
     */
    public String toString(){
        return "(" + x + ", " + y + ") " + (angle * 180/Math.PI) + " degrees";
    }
}
