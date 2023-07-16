package oriedita.editor.export;

import fold.io.CustomFoldReader;
import fold.io.CustomFoldWriter;
import fold.model.Edge;
import fold.model.Face;
import fold.model.FoldEdgeAssignment;
import fold.model.FoldFile;
import fold.model.FoldFrame;
import fold.model.Vertex;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.save.OrieditaFoldFile;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.tools.ResourceUtil;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.LineSegmentSet;
import origami.crease_pattern.PointSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.worker.WireFrame_Worker;
import origami.folding.FoldedFigure;
import origami.folding.HierarchyList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class Fold {
    @Inject
    public Fold() {
    }

    public Save toSave(OrieditaFoldFile foldFile) {
        Save save = SaveProvider.createInstance();

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        FoldFrame rootFrame = foldFile.getRootFrame();

        for (int i = 0; i < rootFrame.getEdges().size(); i++) {
            Edge edge = rootFrame.getEdges().get(i);

            LineSegment ls = new LineSegment();
            double ax = edge.getStart().getX();
            double ay = edge.getStart().getY();
            ls.setA(new Point(ax, ay));
            double bx = edge.getEnd().getX();
            double by = edge.getEnd().getY();
            ls.setB(new Point(bx, by));
            ls.setColor(getColor(edge.getAssignment()));

            minX = Math.min(Math.min(minX, ax), bx);
            minY = Math.min(Math.min(minY, ay), by);
            maxX = Math.max(Math.max(maxX, ax), bx);
            maxY = Math.max(Math.max(maxY, ay), by);

            save.addLineSegment(ls);
        }

        save.setCircles(new ArrayList<>(foldFile.getCircles()));

        FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
        ori_s_temp.setSave(save);//セレクトされた折線だけ取り出してori_s_tempを作る
        ori_s_temp.move(
                new Point(minX, minY),
                new Point(minX, maxY),
                new Point(-200, -200),
                new Point(-200, 200)
        );

        Save save1 = SaveProvider.createInstance();
        ori_s_temp.getSave(save1);

        save1.setTexts(new ArrayList<>(foldFile.getTexts()));

        return save1;
    }

    public static LineColor getColor(FoldEdgeAssignment edgeAssignment) {
        switch (edgeAssignment) {
            case BORDER:
                return LineColor.BLACK_0;
            case MOUNTAIN_FOLD:
                return LineColor.RED_1;
            case VALLEY_FOLD:
                return LineColor.BLUE_2;
            case FLAT_FOLD:
                return LineColor.CYAN_3;
            case UNASSIGNED:
            default:
                return LineColor.BLACK_0;
        }
    }

    private FoldEdgeAssignment getAssignment(LineColor lineColor) {
        switch (lineColor) {
            case ANGLE:
            case NONE:
            default:
                return FoldEdgeAssignment.UNASSIGNED;
            case BLACK_0:
                return FoldEdgeAssignment.BORDER;
            case RED_1:
                return FoldEdgeAssignment.MOUNTAIN_FOLD;
            case BLUE_2:
                return FoldEdgeAssignment.VALLEY_FOLD;
            case CYAN_3:
            case ORANGE_4:
            case MAGENTA_5:
            case GREEN_6:
            case YELLOW_7:
            case PURPLE_8:
            case OTHER_9:
                return FoldEdgeAssignment.FLAT_FOLD;
        }
    }

    public Save importFile(File file) throws FileReadingException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            CustomFoldReader<OrieditaFoldFile> orieditaFoldFileCustomFoldReader = new CustomFoldReader<>(OrieditaFoldFile.class, fileInputStream);
            return toSave(orieditaFoldFileCustomFoldReader.read());
        } catch (IOException e) {
            throw new FileReadingException(e);
        }
    }


    private void exportFile(Save save, LineSegmentSet lineSegmentSet, File file) throws InterruptedException, FileReadingException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            CustomFoldWriter<FoldFile> foldFileCustomFoldWriter = new CustomFoldWriter<>(fileOutputStream);
            foldFileCustomFoldWriter.write(toFoldSave(save, lineSegmentSet));
        } catch (IOException e) {
            throw new FileReadingException(e);
        }
    }

    public void exportFile(Save save, File file) throws FileReadingException, InterruptedException {
        LineSegmentSet s = new LineSegmentSet();
        s.setSave(save);
        if (s.getNumLineSegments() == 0) {
            s.addLine(new Point(0,0), new Point(0,0), LineColor.BLACK_0);
        }
        exportFile(save, s, file);
    }

    public OrieditaFoldFile toFoldSave(Save save) throws InterruptedException {
        LineSegmentSet s = new LineSegmentSet();
        s.setSave(save);
        return toFoldSave(save, s);
    }

    public FoldFrame toFoldFrame(FoldedFigure foldedFigure) {
        FoldFrame frame = new FoldFrame();
        frame.setAttributes(List.of("2D"));
        frame.setClasses(List.of("foldedForm"));

        loadPointSetToFrame(frame, foldedFigure.wireFrame_foldedCp.get(), true, foldedFigure.wireFrame_baseCp);
        HierarchyList hl = foldedFigure.foldedFigure_worker.hierarchyList;
        for (int i = 1; i <= hl.getFacesTotal(); i++) {
            for (int j = i + 1; j <= hl.getFacesTotal(); j++) {
                if (i == j) continue;
                int order = hl.get(i, j);
                if (order == HierarchyList.UNKNOWN_N50 || order == HierarchyList.EMPTY_N100) continue;

                FoldFrame.FaceOrder fo = new FoldFrame.FaceOrder();
                fo.setFace1(frame.getFaces().get(i-1));
                fo.setFace2(frame.getFaces().get(j-1));
                fo.setFace1AboveFace2(
                           (order == HierarchyList.BELOW_0 && foldedFigure.wireFrame_baseCp.getIFacePosition(j) % 2 == 0)
                        || (order == HierarchyList.ABOVE_1 && foldedFigure.wireFrame_baseCp.getIFacePosition(j) % 2 == 1));

                frame.getFaceOrders().add(fo);
            }
        }
        FoldFile f = new FoldFile();
        f.setRootFrame(frame);
        File file = new File("test.fold");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            CustomFoldWriter<FoldFile> foldFileCustomFoldWriter = new CustomFoldWriter<>(fileOutputStream);
            foldFileCustomFoldWriter.write(f);
        } catch (IOException e) {
            Logger.error(e);
        }
        return frame;
    }

    public OrieditaFoldFile toFoldSave(Save save, LineSegmentSet lineSegmentSet) throws InterruptedException {
        WireFrame_Worker wireFrame_worker = new WireFrame_Worker(3.0);
        wireFrame_worker.setLineSegmentSetWithoutFaceOccurence(lineSegmentSet);
        PointSet pointSet = wireFrame_worker.get();

        OrieditaFoldFile foldFile = new OrieditaFoldFile();
        foldFile.setCreator("oriedita");
        FoldFrame rootFrame = foldFile.getRootFrame();

        loadPointSetToFrame(rootFrame, pointSet, false, null);

        foldFile.setCircles(save.getCircles());
        foldFile.setTexts(save.getTexts());
        foldFile.setVersion(ResourceUtil.getVersionFromManifest());

        return foldFile;
    }

    private void loadPointSetToFrame(FoldFrame frame, PointSet pointSet, boolean loadFaces, WireFrame_Worker baseCp) {
        for (int i = 1; i <= pointSet.getNumPoints(); i++) {
            Vertex vertex = new Vertex();
            vertex.setX(pointSet.getPoint(i).getX());
            vertex.setY(pointSet.getPoint(i).getY());
            frame.getVertices().add(vertex);
        }

        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            Edge edge = new Edge();
            edge.setAssignment(getAssignment(pointSet.getColor(i)));
            edge.setFoldAngle(getFoldAngle(pointSet.getColor(i)));
            Vertex startVertex = frame.getVertices().get(pointSet.getBegin(i) - 1);
            Vertex endVertex = frame.getVertices().get(pointSet.getEnd(i) - 1);

            edge.setStart(startVertex);
            edge.setEnd(endVertex);

            frame.getEdges().add(edge);
        }
        if (!loadFaces) {
            return;
        }
        try {
            baseCp.getFacePositions();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i <= pointSet.getNumFaces(); i++) {
            Face face = new Face();
            origami.folding.element.Face face1 = pointSet.getFace(i);
            for (int j = face1.getNumPoints(); j >= 1; j--) {
                Vertex p1 = frame.getVertices().get(face1.getPointId(j)-1);
                face.getVertices().add(p1);
            }
            frame.getFaces().add(face);
        }
    }

    private double getFoldAngle(LineColor color) {
        switch (color) {
            case BLUE_2:
                return 180;
            case RED_1:
                return -180;
            default:
                return 0;
        }
    }

    private List<Double> toFoldPoint(Point p) {
        return Arrays.asList(p.getX(), p.getY());
    }
}
