package com.github.terefang.randy.noise;

public final class int4 {
    public final int x, y, z, w;

    public int4(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static int4 from(int x, int y, int z, int w) {
        return new int4(x, y, z, w);
    }

    public final int4 add(int _m) {
        return new int4(this.x + _m, this.y + _m, this.z + _m, this.w + _m);
    }

    public final int4 sub(int _m) {
        return new int4(this.x - _m, this.y - _m, this.z - _m, this.w - _m);
    }

    public final int4 mul(int _m) {
        return new int4(this.x * _m, this.y * _m, this.z * _m, this.w * _m);
    }

    public final int4 div(int _m) {
        return new int4(this.x / _m, this.y / _m, this.z / _m, this.w / _m);
    }

    public final int4 add(int4 _m) {
        return new int4(this.x + _m.x, this.y + _m.y, this.z + _m.z, this.w + _m.w);
    }

    public final int4 sub(int4 _m) {
        return new int4(this.x - _m.x, this.y - _m.y, this.z - _m.z, this.w - _m.w);
    }

    public final int4 mul(int4 _m) {
        return new int4(this.x * _m.x, this.y * _m.y, this.z * _m.z, this.w * _m.w);
    }

    public final int4 div(int4 _m) {
        return new int4(this.x / _m.x, this.y / _m.y, this.z / _m.z, this.w / _m.w);
    }

    public final int4 comp(int a, int b, int c, int d) {
        int _ret1, _ret2, _ret3, _ret4;
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
        return new int4(_ret1, _ret2, _ret3, _ret4);
    }
}
