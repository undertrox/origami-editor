package origami_editor.editor.canvas;

import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Point;
import origami_editor.editor.LineStyle;
import origami_editor.tools.Camera;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Static utility class for drawing
 */
public class DrawingUtil {
    //For drawing thick lines
    public static void widthLine(Graphics g, Point a, Point b, double width, LineColor iColor) {
        widthLine(g, new LineSegment(a, b), width, iColor);
    }

    public static void widthLine(Graphics g, LineSegment s, double r, LineColor iColor) {
        switch (iColor) {
            case BLACK_0:
                g.setColor(Color.black);
                break;
            case RED_1:
                g.setColor(Color.red);
                break;
            case BLUE_2:
                g.setColor(Color.blue);
                break;
            case CYAN_3:
                g.setColor(Color.green);
                break;
            case ORANGE_4:
                g.setColor(Color.orange);
                break;
        }
        LineSegment sp = OritaCalc.moveParallel(s, r);
        LineSegment sm = OritaCalc.moveParallel(s, -r);

        int[] x = new int[5];
        int[] y = new int[5];

        x[0] = (int) sp.determineAX();
        y[0] = (int) sp.determineAY();
        x[1] = (int) sp.determineBX();
        y[1] = (int) sp.determineBY();
        x[2] = (int) sm.determineBX();
        y[2] = (int) sm.determineBY();
        x[3] = (int) sm.determineAX();
        y[3] = (int) sm.determineAY();

        g.fillPolygon(x, y, 4);
    }

    //Draw a cross around the designated Point
    public static void cross(Graphics g, Point t, double length, double width, LineColor icolor) {
        Point tx0 = new Point();
        Point tx1 = new Point();
        Point ty0 = new Point();
        Point ty1 = new Point();
        tx0.setX(t.getX() - length);
        tx0.setY(t.getY());
        tx1.setX(t.getX() + length);
        tx1.setY(t.getY());
        ty0.setX(t.getX());
        ty0.setY(t.getY() - length);
        ty1.setX(t.getX());
        ty1.setY(t.getY() + length);
        widthLine(g, tx0, tx1, width, icolor);
        widthLine(g, ty0, ty1, width, icolor);
    }

    public static void drawVertex(Graphics2D g, Point a, int pointSize) {
        g.setColor(Color.black);
        g.fill(new Rectangle2D.Double(a.getX() - pointSize, a.getY() - pointSize, 2 * pointSize + .5, 2 * pointSize + .5));
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt1(Graphics g, LineSegment s_tv) {
        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt2(Graphics g, LineSegment s_tv) {
        g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線

    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt3(Graphics g, LineSegment s_tv) {
        g.setColor(new Color(255, 200, 0, 50));
        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt4(Graphics g, LineSegment s_tv, int color_transparency) {
        g.setColor(new Color(255, 0, 147, color_transparency));

        g.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    public static void setColor(Graphics g, LineColor i) {
        switch (i) {
            case BLACK_0:
                g.setColor(Color.black);
                break;
            case RED_1:
                g.setColor(Color.red);
                break;
            case BLUE_2:
                g.setColor(Color.blue);
                break;
            case CYAN_3:
                g.setColor(new Color(100, 200, 200));
                break;
            case ORANGE_4:
                g.setColor(Color.orange);
                break;
            case MAGENTA_5:
                g.setColor(Color.magenta);
                break;
            case GREEN_6:
                g.setColor(Color.green);
                break;
            case YELLOW_7:
                g.setColor(Color.yellow);
                break;
            case PURPLE_8:
                g.setColor(new Color(210, 0, 255));
                break;
        }
    }

    public static void drawSelectLine(Graphics g, LineSegment s, Camera camera) {
        g.setColor(Color.green);

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));

        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + 0.000001, s_tv.determineAY() + 0.000001);
        b.set(s_tv.determineBX() + 0.000001, s_tv.determineBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
    }

    public static void drawAuxLiveLine(Graphics g, LineSegment as, Camera camera, float lineWidth, int pointSize, float f_h_WireframeLineWidth) {
        setColor(g, as.getColor());

        Graphics2D g2 = (Graphics2D) g;

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(as));
        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + 0.000001, s_tv.determineAY() + 0.000001);
        b.set(s_tv.determineBX() + 0.000001, s_tv.determineBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

        if (lineWidth < 2.0f) {//Draw a square at the vertex
            g.setColor(Color.black);
            g.fillRect((int) a.getX() - pointSize, (int) a.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
            g.fillRect((int) b.getX() - pointSize, (int) b.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く
        }

        if (lineWidth >= 2.0f) {//  Thick line
            g2.setStroke(new BasicStroke(1.0f + f_h_WireframeLineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                g.setColor(Color.white);
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.black);
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.white);
                g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.black);
                g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }

            g2.setStroke(new BasicStroke(f_h_WireframeLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

        }
    }

    public static void drawCircle(Graphics g, Circle circle, Camera camera, float lineWidth, int pointSize) {
        Point a= new Point();
        a.set(camera.object2TV(circle.determineCenter()));//この場合のaは描画座標系での円の中心の位置

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        if (circle.getCustomized() == 0) {
            setColor(g, circle.getColor());
        } else if (circle.getCustomized() == 1) {
            g.setColor(circle.getCustomizedColor());
        }

        //円周の描画
        double d_width = circle.getRadius() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。
        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

        a.set(camera.object2TV(circle.determineCenter()));//この場合のaは描画座標系での円の中心の位置

        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        g.setColor(new Color(0, 255, 255, 255));

        //円の中心の描画
        if (lineWidth < 2.0f) {//中心の黒い正方形を描く
            g.setColor(Color.black);
            g.fillRect((int) a.getX() - pointSize, (int) a.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く//g.fillRect(10, 10, 100, 50);長方形を描く
        }

        if (lineWidth >= 2.0f) {//  太線指定時の中心を示す黒い小円を描く
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            if (pointSize != 0) {
                d_width = (double) lineWidth / 2.0 + (double) pointSize;


                g.setColor(Color.white);
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.black);
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }
    }

    public static void drawAuxLine(Graphics g, LineSegment s, Camera camera, float lineWidth, int pointSize) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        if (s.getCustomized() == 0) {
            setColor(g, s.getColor());
        } else if (s.getCustomized() == 1) {
            g.setColor(s.getCustomizedColor());
        }

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));
        Point a= new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + 0.000001, s_tv.determineAY() + 0.000001);
        b.set(s_tv.determineBX() + 0.000001, s_tv.determineBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

        if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
            drawVertex(g2, a, pointSize);
            drawVertex(g2, b, pointSize);
        }

        if (lineWidth >= 2.0f) {//  太線
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                g.setColor(Color.white);
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));


                g.setColor(Color.black);
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.white);
                g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.black);
                g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }
    }

    public static void drawLineStep(Graphics g, LineSegment s, Camera camera, float lineWidth, boolean gridInputAssist) {
        Graphics2D g2 = (Graphics2D) g;
        setColor(g, s.getColor());
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));
        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + 0.000001, s_tv.determineAY() + 0.000001);
        b.set(s_tv.determineBX() + 0.000001, s_tv.determineBY() + 0.000001);//The reason for adding 0.000001 is to prevent the original fold line from being affected by the new fold line when drawing on the display.


        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 2;
        }

        switch (s.getActive()) {
            case ACTIVE_A_1:
                g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            case ACTIVE_B_2:
                g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            case ACTIVE_BOTH_3:
                g.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                g.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
        }
    }

    public static void drawLineCandidate(Graphics g, LineSegment s, Camera camera, int pointSize) {
        setColor(g, s.getColor());

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));
        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + 0.000001, s_tv.determineAY() + 0.000001);
        b.set(s_tv.determineBX() + 0.000001, s_tv.determineBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
        int i_width = pointSize + 5;

        switch (s.getActive()) {
            case ACTIVE_A_1:
                g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線
                break;
            case ACTIVE_B_2:
                g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                break;
            case ACTIVE_BOTH_3:
                g.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                g.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線

                g.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                g.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                break;
        }
    }

    public static void drawCircleStep(Graphics g, Circle c, Camera camera) {
        Graphics2D g2 = (Graphics2D) g;
        setColor(g, c.getColor());
        Point a = new Point();

        a.set(camera.object2TV(c.determineCenter()));//この場合のs_tvは描画座標系での円の中心の位置
        a.set(a.getX() + 0.000001, a.getY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        double d_width = c.getRadius() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。

        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
    }

    public static void drawCpLine(Graphics g, LineSegment s, Camera camera, LineStyle lineStyle, float lineWidth, int pointSize) {
        float[] dash_M1 = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
        float[] dash_M2 = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
        float[] dash_V = {8.0f, 8.0f};//破線

        Graphics2D g2 = (Graphics2D) g;
        switch (lineStyle) {
            case COLOR:
                setColor(g, s.getColor());
                g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                break;
            case COLOR_AND_SHAPE:
                setColor(g, s.getColor());
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                }//一点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
            case BLACK_ONE_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                }//一点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
            case BLACK_TWO_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_M2, 0.0f));
                }//二点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
        }

        LineSegment s_tv = new LineSegment();
        s_tv.set(camera.object2TV(s));
        Point a = new Point();
        Point b = new Point();
        a.set(s_tv.determineAX() + 0.000001, s_tv.determineAY() + 0.000001);
        b.set(s_tv.determineBX() + 0.000001, s_tv.determineBY() + 0.000001);//なぜ0.000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        g2.draw(new Line2D.Double(a.getX(), a.getY(), b.getX(), b.getY()));

        if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
            drawVertex(g2, a, pointSize);
            drawVertex(g2, b, pointSize);
        }

        if (lineWidth >= 2.0f) {//  太線
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                g.setColor(Color.white);
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.black);
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.white);
                g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                g.setColor(Color.black);
                g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }
    }
}
