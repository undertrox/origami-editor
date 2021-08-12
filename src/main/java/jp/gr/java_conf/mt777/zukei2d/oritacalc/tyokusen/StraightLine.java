package jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen;

import jp.gr.java_conf.mt777.zukei2d.ten.*;
import jp.gr.java_conf.mt777.zukei2d.senbun.*;

public class StraightLine {
    //注意！p1=p2の場合は結果がおかしくなるがこの関数にチェック機構がないので、気づきにくいかも。
    //aは0以上。もしa＝0なら、bが0以上になるようにすること。こうしないと、直線との距離の符号がおかしくなる。
    double a, b, c;//a*x+b*y+c=0,  a,b,c,x,y,は整数として扱う(20181115このコメントおかしいのでは？)

    public StraightLine() {  //コンストラクタ
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 1.0;
        double y2 = 1.0;

        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    public StraightLine(double a0, double b0, double c0) {  //コンストラクタ
        a = a0;
        b = b0;
        c = c0;
        coefficient();
    }


    public StraightLine(Point p1, Point p2) {  //コンストラクタ
        //二点を指定して直線のa,b,cを求める
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }


    public StraightLine(LineSegment s0) {  //コンストラクタ
        //Senbunを指定して直線のa,b,cを求める
        double x1 = s0.getAX(), y1 = s0.getAY();
        double x2 = s0.getBX(), y2 = s0.getBY();
        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }


    public StraightLine(double x1, double y1, double x2, double y2) {  //コンストラクタ
        //二点を指定して直線のa,b,cを求める

        a = y2 - y1;
        b = x1 - x2;
        c = y1 * x2 - x1 * y2;
        coefficient();
    }

    //
    void coefficient() {
        if ((a < 0.0)) {
            a = -a;
            b = -b;
            c = -c;
        }
        //	if((a==0.0)&&(b<0.0)){
        if ((-0.1 < a) && (a < 0.1)) {
            if (b < 0.0) {
                a = -a;
                b = -b;
                c = -c;
            }
        }
    }


    public void display(String str0) {
        System.out.println(str0 + "   " + a + " x + " + b + " y + " + c + " = 0.0 ");
    }


    //translation
    public void translate(double d) {
        c = c + d * Math.sqrt(a * a + b * b);
    }

    //
    public void set(StraightLine t) {
        a = t.getA();
        b = t.getB();
        c = t.getC();
        coefficient();
    }

    //

    public void setA(double a0) {
        a = a0;
    }

    public void setB(double b0) {
        a = b0;
    }

    public void setC(double c0) {
        a = c0;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double calculateDistance(Point p) {//直線と点pとの距離
        double x = p.getX();
        double y = p.getY();
        //return Math.abs((double) ((float)(a*x+b*y+c)/Math.sqrt((float)(a*a+b*b))));
        return Math.abs((a * x + b * y + c) / Math.sqrt(a * a + b * b));
    }


    public double calculateDistanceSquared(Point p) {//The square of the distance between the straight line and the point p
        double x = p.getX();
        double y = p.getY();
        //return Math.abs((double) ((float)(a*x+b*y+c)/Math.sqrt((float)(a*a+b*b))));
        return (a * x + b * y + c) * (a * x + b * y + c) / (a * a + b * b);
    }


    public void orthogonalize(Point p) { //Converted to a straight line (bx-ay + d = 0) that passes through the point (x, y) and is orthogonal to ax + by + c = 0
        double e;
        double x = p.getX();
        double y = p.getY();
        c = -b * x + a * y;
        e = a;
        a = b;
        b = -e;

        coefficient();
    }


    public int sameSide(Point p1, Point p2) {// Returns 1 if the two points are on the same side of the straight line, -1 if they are on the other side, 0 if there is a point on the straight line
        double dd = dainyuukeisan(p1) * dainyuukeisan(p2);
		return Double.compare(dd, 0.0);
	}


    public double dainyuukeisan(Point p) {
        return a * p.getX() + b * p.getY() + c;
    }  //a*x+b*y+cにx,yを代入した値を返す

    public int lineSegment_intersect_hantei_kuwasii(LineSegment s0) {//0 = This straight line does not intersect a given line segment, 1 = intersects at X type, 21 = intersects at point a of line segment at T type, 22 = intersects at point b of line segment at T type, 3 = Line segment is included in the straight line.


        double d_a2 = calculateDistanceSquared(s0.getA());
        double d_b2 = calculateDistanceSquared(s0.getB());

        if (d_a2 < 0.00000001 && d_b2 < 0.00000001) {
            return 3;
        }


        if (d_a2 < 0.00000001 && d_b2 >= 0.00000001) {
            return 21;
        }
        if (d_a2 >= 0.00000001 && d_b2 < 0.00000001) {
            return 22;
        }


        //以下は線分のa点もb点も直線上にはないと判断される場合

        double d_a = dainyuukeisan(s0.getA());
        double d_b = dainyuukeisan(s0.getB());

        if (d_a * d_b > 0.0) {
            return 0;
        }
        if (d_a * d_b < 0.0) {
            return 1;
        }


        return 3;

    }


    //Added 20170312 function to find intersections with other straight lines
    public Point findIntersection(StraightLine t2) {
        double a1 = a, b1 = b, c1 = c;//直線t1, a1*x+b1*y+c1=0の各係数を求める。
        double a2 = t2.getA(), b2 = t2.getB(), c2 = t2.getC();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

        return new Point((b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1), (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1));
    }


    //直線上の点pの影の位置（点pと最も近い直線上の位置）を求める。　20170312追加
    public Point findShadow(Point p) {
        StraightLine t1 = new StraightLine(a, b, c);
        t1.orthogonalize(p);//点p1を通って tに直行する直線を求める。
        return findIntersection(t1);
    }


}

















