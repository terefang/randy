package com.github.terefang.randy.noise;

/**
 * Simple container class that holds 2 doubles.
 * Takes slightly less storage than an array of double[2] and may avoid array index bounds check speed penalty.
 */
public final class double2 {
    public final double x, y;

    public double2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double2 from(double x, double y) {
        return new double2(x, y);
    }

    public final double2 xx() {
        return comp(0, 0);
    }

    public final double2 yy() {
        return comp(1, 1);
    }

    public final double2 zz() {
        return comp(2, 2);
    }

    public final double2 xy() {
        return comp(0, 1);
    }

    public final double2 yx() {
        return comp(1, 0);
    }

    public final double2 xz() {
        return comp(0, 2);
    }

    public final double2 zx() {
        return comp(2, 0);
    }

    public final double2 yz() {
        return comp(1, 2);
    }

    public final double2 zy() {
        return comp(2, 1);
    }

    public final double2 fastFloor() {
        return double2.from(NoiseUtil.fastFloor(this.x), NoiseUtil.fastFloor(this.y));
    }


    public final double2 comp(int a, int b) {
        double _ret1, _ret2, _ret3;
        switch (a) {
            case 1:
                _ret1 = this.y;
                break;
            case 0:
            default:
                _ret1 = this.x;
                break;
        }
        switch (b) {
            case 1:
                _ret2 = this.y;
                break;
            case 0:
            default:
                _ret2 = this.x;
                break;
        }
        return new double2(_ret1, _ret2);
    }

    public final double2 normal(double a, double b) {
        return new double2(this.x * a + b, this.y * a + b);
    }

    public final double2 add(double _m) {
        return new double2(this.x + _m, this.y + _m);
    }

    public final double2 sub(double _m) {
        return new double2(this.x - _m, this.y - _m);
    }

    public final double2 mul(double _m) {
        return new double2(this.x * _m, this.y * _m);
    }

    public final double2 div(double _m) {
        return new double2(this.x / _m, this.y / _m);
    }

    public final double2 add(double2 _m) {
        return new double2(this.x + _m.x, this.y + _m.y);
    }

    public final double2 sub(double2 _m) {
        return new double2(this.x - _m.x, this.y - _m.y);
    }

    public final double2 mul(double2 _m) {
        return new double2(this.x * _m.x, this.y * _m.y);
    }

    public final double2 div(double2 _m) {
        return new double2(this.x / _m.x, this.y / _m.y);
    }
}
