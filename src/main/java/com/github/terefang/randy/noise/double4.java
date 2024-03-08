package com.github.terefang.randy.noise;

public final class double4 {
    public final double x, y, z, w;

    public final double4 fastFloor() {
        return double4.from(NoiseUtil.fastFloor(this.x), NoiseUtil.fastFloor(this.y), NoiseUtil.fastFloor(this.z), NoiseUtil.fastFloor(this.w));
    }

    public final double4 normal(double a, double b) {
        return new double4(this.x * a + b, this.y * a + b, this.z * a + b, this.w * a + b);
    }

    public final double4 add(double _m) {
        return new double4(this.x + _m, this.y + _m, this.z + _m, this.w + _m);
    }

    public final double4 sub(double _m) {
        return new double4(this.x - _m, this.y - _m, this.z - _m, this.w - _m);
    }

    public final double4 mul(double _m) {
        return new double4(this.x * _m, this.y * _m, this.z * _m, this.w * _m);
    }

    public final double4 div(double _m) {
        return new double4(this.x / _m, this.y / _m, this.z / _m, this.w / _m);
    }

    public final double4 add(double4 _m) {
        return new double4(this.x + _m.x, this.y + _m.y, this.z + _m.z, this.w + _m.w);
    }

    public final double4 sub(double4 _m) {
        return new double4(this.x - _m.x, this.y - _m.y, this.z - _m.z, this.w - _m.w);
    }

    public final double4 mul(double4 _m) {
        return new double4(this.x * _m.x, this.y * _m.y, this.z * _m.z, this.w * _m.w);
    }

    public final double4 div(double4 _m) {
        return new double4(this.x / _m.x, this.y / _m.y, this.z / _m.z, this.w / _m.w);
    }

    public double4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static double4 from(double x, double y, double z, double w) {
        return new double4(x, y, z, w);
    }

    public final double4 comp(int a, int b, int c, int d) {
        double _ret1, _ret2, _ret3, _ret4;
        switch (a) {
            case 3:
                _ret1 = this.w;
                break;
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
            case 3:
                _ret2 = this.w;
                break;
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
            case 3:
                _ret3 = this.w;
                break;
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
        switch (d) {
            case 3:
                _ret4 = this.w;
                break;
            case 2:
                _ret4 = this.z;
                break;
            case 1:
                _ret4 = this.y;
                break;
            case 0:
            default:
                _ret4 = this.x;
                break;
        }
        return new double4(_ret1, _ret2, _ret3, _ret4);
    }

    public final double3 comp(int a, int b, int c) {
        double _ret1, _ret2, _ret3;
        switch (a) {
            case 3:
                _ret1 = this.w;
                break;
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
            case 3:
                _ret2 = this.w;
                break;
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
            case 3:
                _ret3 = this.w;
                break;
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
    public final double2 ww() {
        return comp(3, 3);
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

    public final double2 zw() {
        return comp(2, 3);
    }

    public final double2 comp(int a, int b) {
        double _ret1, _ret2, _ret3;
        switch (a) {
            case 3:
                _ret1 = this.w;
                break;
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
            case 3:
                _ret2 = this.w;
                break;
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
