package origami_editor.editor.canvas.handler;

import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;
import origami_editor.editor.MouseMode;
import origami_editor.sortingbox.SortingBox;

public class MouseHandlerContinuousSymmetricDraw extends BaseMouseHandlerInputRestricted {
    // Extend the vector ab (= s0) from point a to b until it first intersects another polygonal line
    LineSegment lengthenUntilIntersectionLineSegment = new LineSegment();
    Point lengthenUntilIntersectionPoint = new Point();
    StraightLine.Intersection lengthenUntilIntersection_flg = StraightLine.Intersection.NONE_0;//The situation of the first intersection where ab was extended
    int lengthenUntilIntersectionFoldLineIncluded_flg = 0;//If ab is straightened, including existing polygonal lines, 3
    LineSegment lengthenUntilIntersectionFirstLineSegment = new LineSegment();//Straightening ab and the existing polygonal line that hits first

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52;
    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        System.out.println("i_egaki_dankai=" + d.lineStep.size());

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        Point closest_point = d.getClosestPoint(p);

        if (p.distance(closest_point) < d.selectionDistance) {
            d.lineStepAdd(new LineSegment(closest_point, closest_point, d.lineColor));
        } else {
            d.lineStepAdd(new LineSegment(p, p, d.lineColor));
        }

        System.out.println("i_egaki_dankai=" + d.lineStep.size());
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.lineStep.size() == 2) {
            continuous_folding_new(d.lineStep.get(0).getA(), d.lineStep.get(1).getA());

            d.record();

            d.lineStep.clear();
        }
    }

    public void continuous_folding_new(Point a, Point b) {//An improved version of continuous folding.
        d.app.repaint();

        //ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする。
        //補助活線は無視する
        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする


        //「再帰関数における、種の発芽」交点がない場合「種」が成長せずリターン。

        lengthenUntilIntersectionCalculateDisregardIncludedLineSegment_new(a, b);//一番近い交差点を見つけて各種情報を記録
        // 0 = This straight line does not intersect the given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
        if (lengthenUntilIntersection_flg == StraightLine.Intersection.NONE_0) {
            return;
        }

        LineSegment s = new LineSegment();
        s.set(lengthenUntilIntersectionLineSegment);
        d.lineStepAdd(s);
        s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);

        System.out.println("20201129 saiki repaint ");

        //「再帰関数における、種の生成」求めた最も近い交点から次のベクトル（＝次の再帰関数に渡す「種」）を発生する。最も近い交点が折線とＸ字型に交差している点か頂点かで、種のでき方が異なる。

        //最も近い交点が折線とＸ字型の場合無条件に種を生成し、散布。
        // 0 = This straight line does not intersect the given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
        if (lengthenUntilIntersection_flg == StraightLine.Intersection.INTERSECT_X_1) {
            LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
            kousaten_made_nobasi_saisyono_lineSegment.set(lengthenUntilIntersectionFirstLineSegment);

            Point new_a = new Point();
            new_a.set(lengthenUntilIntersectionPoint);//Ten new_aは最も近い交点
            Point new_b = new Point();
            new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            continuous_folding_new(new_a, new_b);//種の散布
            return;
        }

        //最も近い交点が頂点（折線端末）の場合、頂点に集まる折線の数で条件分けして、種を生成し散布、
        // 0 = This straight line does not intersect the given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
        // 0 = This straight line does not intersect the given line segment, 1 = X type intersects, 2 = T type intersects, 3 = Line segment is included in the straight line.
        if ((lengthenUntilIntersection_flg == StraightLine.Intersection.INTERSECT_T_A_21)
                || (lengthenUntilIntersection_flg == StraightLine.Intersection.INTERSECT_T_B_22)) {//System.out.println("20201129 21 or 22");

            StraightLine tyoku1 = new StraightLine(a, b);

            SortingBox<LineSegment> t_m_s_nbox = new SortingBox<>();

            t_m_s_nbox.set(d.foldLineSet.get_SortingBox_of_vertex_b_surrounding_foldLine(lengthenUntilIntersectionLineSegment.getA(), lengthenUntilIntersectionLineSegment.getB()));

            if (t_m_s_nbox.getTotal() == 2) {
                if (tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1)) == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                if (tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2)) == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                if (tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2)) == StraightLine.Intersection.INCLUDED_3) {
                    LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                    kousaten_made_nobasi_saisyono_lineSegment.set(lengthenUntilIntersectionFirstLineSegment);

                    Point new_a = new Point();
                    new_a.set(lengthenUntilIntersectionPoint);//Ten new_aは最も近い交点
                    Point new_b = new Point();
                    new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                    continuous_folding_new(new_a, new_b);//種の散布
                }
            } else


            if (t_m_s_nbox.getTotal() == 3) {
                if (tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1)) == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(2));
                    if (tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3)) == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(lengthenUntilIntersectionFirstLineSegment);

                        Point new_a = new Point();
                        new_a.set(lengthenUntilIntersectionPoint);//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                if (tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2)) == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(3));
                    if (tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1)) == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(lengthenUntilIntersectionFirstLineSegment);

                        Point new_a = new Point();
                        new_a.set(lengthenUntilIntersectionPoint);//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                if (tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3)) == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                    if (tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2)) == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment();
                        kousaten_made_nobasi_saisyono_lineSegment.set(lengthenUntilIntersectionFirstLineSegment);

                        Point new_a = new Point();
                        new_a.set(lengthenUntilIntersectionPoint);//Ten new_aは最も近い交点
                        Point new_b = new Point();
                        new_b.set(OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a));//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b);//種の散布
                    }
                }
            }
        }
    }

    //Extend the vector ab (= s0) from point a to b, until it first intersects another fold line (ignoring the line segment contained in the straight line) // If it does not intersect another fold line, set Point a return
    public void lengthenUntilIntersectionCalculateDisregardIncludedLineSegment_new(Point a, Point b) {//Extend the vector ab (= s0) from point a to b, until it first intersects another fold line (ignoring the line segment contained in the straight line) // If it does not intersect another fold line, Ten a return
        LineSegment s0 = new LineSegment();
        s0.set(a, b);
        LineSegment addLine = new LineSegment();
        addLine.set(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
        double kousa_point_distance = kousa_point.distance(addLine.getA());
        StraightLine straightLine = new StraightLine(addLine.getA(), addLine.getB());
        StraightLine.Intersection i_kousa_flg;

        lengthenUntilIntersection_flg = StraightLine.Intersection.NONE_0;
        lengthenUntilIntersectionFoldLineIncluded_flg = 0;
        for (int i = 1; i <= d.foldLineSet.getTotal(); i++) {
            LineSegment s = d.foldLineSet.get(i);
            if (s.getColor().isFoldingLine()) {
                // 0 = This straight line does not intersect the given line segment,
                // 1 = X type intersects,
                // 21 = T-shaped intersection at point a of the line segment,
                // 22 = T-shaped intersection at point b of the line segment,
                // 3 = Line segments are included in the straight line.
                i_kousa_flg = straightLine.lineSegment_intersect_reverse_detail(s);//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                //if(i_kousa_flg==3){lengthenUntilIntersectionFoldLineIncluded_flg=3;}
                if ((i_kousa_flg == StraightLine.Intersection.INTERSECT_X_1 || i_kousa_flg == StraightLine.Intersection.INTERSECT_T_A_21) || i_kousa_flg == StraightLine.Intersection.INTERSECT_T_B_22) {

                    kousa_point.set(OritaCalc.findIntersection(straightLine, s));//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す

                    double newDistance = kousa_point.distance(addLine.getA());
                    if (newDistance > 0.00001 && newDistance < kousa_point_distance) {
                        double d_kakudo = OritaCalc.angle(addLine.getA(), addLine.getB(), addLine.getA(), kousa_point);

                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_point_distance = newDistance;
                            addLine.set(addLine.getA(), kousa_point);

                            lengthenUntilIntersection_flg = i_kousa_flg;
                            lengthenUntilIntersectionFirstLineSegment.set(s);
                        }
                    }
                }
            }
        }

        lengthenUntilIntersectionLineSegment.set(addLine);
        lengthenUntilIntersectionPoint.set(addLine.getB());
    }
}
