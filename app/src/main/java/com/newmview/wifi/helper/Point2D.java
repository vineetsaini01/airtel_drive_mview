/*
package com.mcpsinc.mview.helper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Stack;

class Point implements Comparable<Point>
{
    public static final Comparator<Point> X_ORDER = new XOrder();
    public static final Comparator<Point> Y_ORDER = new YOrder();
    public static final Comparator<Point> R_ORDER = new ROrder();
    public final Comparator<Point> POLAR_ORDER = new PolarOrder();
    public final Comparator<Point> ATAN2_ORDER = new Atan2Order();
    public final Comparator<Point> DISTANCE_TO_ORDER = new DistanceToOrder();

    private final double x; // x coordinate
    private final double y; // y coordinate

    public Point(double x, double y)
    {
        if (Double.isInfinite(x) || Double.isInfinite(y))
            throw new IllegalArgumentException("Coordinates must be finite");
        if (Double.isNaN(x) || Double.isNaN(y))
            throw new IllegalArgumentException("Coordinates cannot be NaN");
        if (x == 0.0)
            x = 0.0; // convert -0.0 to +0.0
        if (y == 0.0)
            y = 0.0; // convert -0.0 to +0.0
        this.x = x;
        this.y = y;
    }

    public double x()
    {
        return x;
    }

    public double y()
    {
        return y;
    }

    public double r()
    {
        return Math.sqrt(x * x + y * y);
    }

    public double theta()
    {
        return Math.atan2(y, x);
    }

    private double angleTo(Point that)
    {
        double dx = that.x - this.x;
        double dy = that.y - this.y;
        return Math.atan2(dy, dx);
    }

    public static int ccw(Point a, Point b, Point c)
    {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (area2 < 0)
            return -1;
        else if (area2 > 0)
            return +1;
        else
            return 0;
    }

    public static double area2(Point a, Point b, Point c)
    {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public double distanceTo(Point that)
    {
        double dx = this.x - that.x;
        double dy = this.y - that.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceSquaredTo(Point that)
    {
        double dx = this.x - that.x;
        double dy = this.y - that.y;
        return dx * dx + dy * dy;
    }

    public int compareTo(Point that)
    {
        if (this.y < that.y)
            return -1;
        if (this.y > that.y)
            return +1;
        if (this.x < that.x)
            return -1;
        if (this.x > that.x)
            return +1;
        return 0;
    }

    private static class XOrder implements Comparator<Point>
    {
        public int compare(Point p, Point q)
        {
            if (p.x < q.x)
                return -1;
            if (p.x > q.x)
                return +1;
            return 0;
        }
    }

    private static class YOrder implements Comparator<Point>
    {
        public int compare(Point p, Point q)
        {
            if (p.y < q.y)
                return -1;
            if (p.y > q.y)
                return +1;
            return 0;
        }
    }

    private static class ROrder implements Comparator<Point>
    {
        public int compare(Point p, Point q)
        {
            double delta = (p.x * p.x + p.y * p.y) - (q.x * q.x + q.y * q.y);
            if (delta < 0)
                return -1;
            if (delta > 0)
                return +1;
            return 0;
        }
    }

    private class Atan2Order implements Comparator<Point>
    {
        public int compare(Point q1, Point q2)
        {
            double angle1 = angleTo(q1);
            double angle2 = angleTo(q2);
            if (angle1 < angle2)
                return -1;
            else if (angle1 > angle2)
                return +1;
            else
                return 0;
        }
    }

    private class PolarOrder implements Comparator<Point>
    {
        public int compare(Point q1, Point q2)
        {
            double dx1 = q1.x - x;
            double dy1 = q1.y - y;
            double dx2 = q2.x - x;
            double dy2 = q2.y - y;

            if (dy1 >= 0 && dy2 < 0)
                return -1; // q1 above; q2 below
            else if (dy2 >= 0 && dy1 < 0)
                return +1; // q1 below; q2 above
            else if (dy1 == 0 && dy2 == 0)
            { // 3-collinear and horizontal
                if (dx1 >= 0 && dx2 < 0)
                    return -1;
                else if (dx2 >= 0 && dx1 < 0)
                    return +1;
                else
                    return 0;
            } else
                return -ccw(Point.this, q1, q2); // both above or below
        }
    }

    private class DistanceToOrder implements Comparator<Point>
    {
        public int compare(Point p, Point q)
        {
            double dist1 = distanceSquaredTo(p);
            double dist2 = distanceSquaredTo(q);
            if (dist1 < dist2)
                return -1;
            else if (dist1 > dist2)
                return +1;
            else
                return 0;
        }
    }

    public boolean equals(Object other)
    {
        if (other == this)
            return true;
        if (other == null)
            return false;
        if (other.getClass() != this.getClass())
            return false;
        Point that = (Point) other;
        return this.x == that.x && this.y == that.y;
    }

    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }

    public int hashCode()
    {
        int hashX = ((Double) x).hashCode();
        int hashY = ((Double) y).hashCode();
        return 31 * hashX + hashY;
    }

}

*/
