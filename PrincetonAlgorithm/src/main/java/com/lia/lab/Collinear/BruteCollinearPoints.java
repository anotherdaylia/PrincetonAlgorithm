package com.lia.lab.Collinear;

import edu.princeton.cs.algs4.*;
import java.util.*;

/**
 * Created by liqu on 10/1/15.
 */
public class BruteCollinearPoints {
    private Point[] points;
    private Point[] pointsCopy;
    private int numberOfSegments;

    // finds all line calculateSegments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {throw new java.lang.NullPointerException();}
        this.points = points;
        this.numberOfSegments = 0;

        // throws an exception if duplicate points
        this.pointsCopy = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            pointsCopy[i] = points[i];
        }

        Arrays.sort(pointsCopy);
        for (int i = 1; i < pointsCopy.length; i++) {
            if (pointsCopy[i - 1].compareTo(pointsCopy[i]) == 0) {
                throw new java.lang.IllegalArgumentException();
            }
        }
    }

    // the number of line calculateSegments
    public int numberOfSegments() {
        return numberOfSegments;
    }

    // the line calculateSegments
    public LineSegment[] segments() {
        HashMap<Double, ArrayList<Point[]>> map = new HashMap<>();

        for (int p = 0; p < points.length; p++) {
            for (int q = p + 1; q < points.length; q++) {
                for (int r = q + 1; r < points.length; r++) {
                    for (int s = r + 1; s < points.length; s++) {

                        Double slope1 = points[p].slopeTo(points[q]);
                        Double slope2 = points[p].slopeTo(points[r]);
                        Double slope3 = points[p].slopeTo(points[s]);

                        if ((Double.compare(slope1, slope2) == 0) && (Double.compare(slope1, slope3) == 0)){

                            ArrayList<Point> collinear = new ArrayList<>();
                            collinear.add(points[p]);
                            collinear.add(points[q]);
                            collinear.add(points[r]);
                            collinear.add(points[s]);
                            Collections.sort(collinear);

                            // check if the new collinear overlap with existing collinears
                            if (map.containsKey(slope1)) {
                                ArrayList<Point[]> linesgmtList = map.get(slope1);
                                int count = 0; // track the number of linesegment have gone through in linesgmtList
                                int index = Integer.MAX_VALUE; // track the linesegment needs update

                                for (Point[] pointPair : linesgmtList) {
                                    Point ls_p = pointPair[0];

                                    // check if points on the collinear is on the existing collier
                                    if (ls_p.slopeTo(collinear.get(0)) != slope1
                                            && ls_p.slopeTo(collinear.get(0)) != Double.NEGATIVE_INFINITY) {
                                        count++;
                                        if (count < linesgmtList.size()) {
                                            continue;
                                        } else {
                                            index = linesgmtList.indexOf(pointPair);
                                            break;
                                        }
                                    } else {
                                        index = linesgmtList.indexOf(pointPair);
                                        break;
                                    }
                                }

                                // the collinear has overlap w. existing collinears, update linesegment
                                if (count < linesgmtList.size() && index != Integer.MAX_VALUE) {
                                    Point[] pointPair = linesgmtList.get(index);
                                    Point ls_p = pointPair[0];
                                    Point ls_q = pointPair[1];

                                    if (collinear.get(0).compareTo(ls_p) < 0 || collinear.get(3).compareTo(ls_q) > 0) {
                                        ls_p = collinear.get(0);
                                        ls_q = collinear.get(3);

                                        Point[] ls = new Point[2];
                                        ls[0] = ls_p;
                                        ls[1] = ls_q;

                                        linesgmtList.set(index, ls);
                                        map.put(slope1, linesgmtList);
                                    }
                                }

                                // the collinear does not overlap w/ existing collinear, add to list directly
                                /* Bug fixed:
                                this block of code have to be placed in the last, b/c after adding more linesegments
                                the size of the arraylist will be changed */
                                if (count >= linesgmtList.size() && index != Integer.MAX_VALUE) {
                                    Point[] pointPair = new Point[2];
                                    pointPair[0] = collinear.get(0);
                                    pointPair[1] = collinear.get(collinear.size()-1);
                                    linesgmtList.add(pointPair);
                                    map.put(slope1, linesgmtList);
                                    numberOfSegments++;
                                }

                            } else { // if map does not contain the slope of collinear, add the collinear
                                Point[] pointPair = new Point[2];
                                pointPair[0] = collinear.get(0);
                                pointPair[1] = collinear.get(collinear.size()-1);
                                ArrayList<Point[]> linesgmtList = new ArrayList<>();
                                linesgmtList.add(pointPair);
                                map.put(slope1, linesgmtList);
                                numberOfSegments++;
                            }
                        }
                    }
                }
            }
        }

        return convertToArr(map);
    }

    private LineSegment[] convertToArr (HashMap<Double, ArrayList<Point[]>> map) {
        ArrayList<LineSegment> lsList = new ArrayList<>();

        Iterator it_map = map.keySet().iterator();
        while(it_map.hasNext()) {
            ArrayList<Point[]> linesgmtList = map.get(it_map.next());
            Iterator it_list = linesgmtList.iterator();
            while (it_list.hasNext()) {
                Point[] pointPair = (Point[]) it_list.next();
                LineSegment ls = new LineSegment(pointPair[0], pointPair[1]);
                lsList.add(ls);
            }
        }

        LineSegment[] lineSgmtArr = new LineSegment[lsList.size()];
        lineSgmtArr = lsList.toArray(lineSgmtArr);

        return lineSgmtArr;
    }

}