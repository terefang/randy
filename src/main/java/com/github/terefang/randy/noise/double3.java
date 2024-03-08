package com.github.terefang.randy.noise;

/**
 * Simple container class that holds 3 doubles.
 * Takes slightly less storage than an array of double[3] and may avoid array index bounds check speed penalty.
 */
public final class double3 {
    public final double x, y, z;

    public double3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double3 from(double x, double y, double z) {
        return new double3(x, y, z);
    }

    public final double3 fastFloor() {
        return double3.from(NoiseUtil.fastFloor(this.x), NoiseUtil.fastFloor(this.y), NoiseUtil.fastFloor(this.z));
    }

    public final double3 normal(double a, double b) {
        return new double3(this.x * a + b, this.y * a + b, this.z * a + b);
    }

    public final double3 add(double _m) {
        return new double3(this.x + _m, this.y + _m, this.z + _m);
    }

    public final double3 sub(double _m) {
        return new double3(this.x - _m, this.y - _m, this.z - _m);
    }

    public final double3 mul(double _m) {
        return new double3(this.x * _m, this.y * _m, this.z * _m);
    }

    public final double3 div(double _m) {
        return new double3(this.x / _m, this.y / _m, this.z / _m);
    }

    public final double3 add(double3 _m) {
        return new double3(this.x + _m.x, this.y + _m.y, this.z + _m.z);
    }

    public final double3 sub(double3 _m) {
        return new double3(this.x - _m.x, this.y - _m.y, this.z - _m.z);
    }

    public final double3 mul(double3 _m) {
        return new double3(this.x * _m.x, this.y * _m.y, this.z * _m.z);
    }

    public final double3 div(double3 _m) {
        return new double3(this.x / _m.x, this.y / _m.y, this.z / _m.z);
    }

    public final double3 xxx() {
        return comp(0, 0, 0);
    }

    public final double3 yyy() {
        return comp(0, 0, 0);
    }

    public final double3 zzz() {
        return comp(0, 0, 0);
    }

    public final double3 zyx() {
        return comp(2, 1, 0);
    }

    public final double3 yzx() {
        return comp(1, 2, 0);
    }

    public final double3 zxy() {
        return comp(2, 0, 1);
    }

    public final double3 yxz() {
        return comp(1, 0, 2);
    }

    public final double3 comp(int a, int b, int c) {
        double _ret1, _ret2, _ret3;
        switch (a) {
            case 2:
                _ret1 = this.z;
                break;
            case 1:
                _ret1 = this.y;
                break;
            case 0:
            default:
                _ret1 = this.x;
                break;
        }
        switch (b) {
            case 2:
                _ret2 = this.z;
                break;
            case 1:
                _ret2 = this.y;
                break;
            case 0:
            default:
                _ret2 = this.x;
                break;
        }
        switch (c) {
            case 2:
                _ret3 = this.z;
                break;
            case 1:
                _ret3 = this.y;
                break;
            case 0:
            default:
                _ret3 = this.x;
                break;
        }
        return new double3(_ret1, _ret2, _ret3);
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

    public final double2 comp(int a, int b) {
        double _ret1, _ret2, _ret3;
        switch (a) {
            case 2:
                _ret1 = this.z;
                break;
            case 1:
                _ret1 = this.y;
                break;
            case 0:
            default:
                _ret1 = this.x;
                break;
        }
        switch (b) {
            case 2:
                _ret2 = this.z;
                break;
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
}
