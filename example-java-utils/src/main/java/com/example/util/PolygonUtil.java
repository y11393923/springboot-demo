package com.example.util;

import java.util.List;

/**
 * @Author: zhouyuyang
 * @Date: 2020/10/9 17:57
 */
public class PolygonUtil {

    /**
     * (testx <= (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
     * 推导步骤如下
     * <p>
     * 线段开始点(Sx,Sy);结束点(Ex,Ey)
     * 测试点(Tx,Ty)
     * TempX 测试点画直线与线段的X轴交点
     * <p>
     * <p>
     * <p>
     * K = (Sy - Ey) / (Sx - Ex);
     * B = ((Sx * Ey) - (Ex * Sy)) / (Sx - Ey);
     * <p>
     * 参考公式(y = k * x + b) 或者 两点式
     * <p>
     * TempX = (Ty - B) / K
     * TempX = (Ty - (((Sx * Ey) - (Ex * Sy)) / (Sx - Ey))) / ((Sy - Ey) / (Sx - Ex))
     * TempX = ((Ty * (Sx - Ex)) - ((Sx * Ey) - (Ex * Sy))) / (Sy - Ey)
     * TempX = ((Ty * (Sx - Ex)) - ((Sx * Ey) - (Ex * Sy) - (Ex * Ey) + (Ex * Ey))) / (Sy - Ey)
     * TempX = ((Ty * (Sx - Ex)) - ((Ey * (Sx - Ex)) - (Ex * (Ey - Sy)))) / (Sy - Ey)
     * TempX = ((Ty * (Sx - Ex)) - (Ey * (Sx - Ex)) + (Ex * (Ey - Sy))) / (Sy - Ey)
     * TempX = (((Ty - Ey) * (Sx - Ex)) + (Ex * (Ey - Sy))) / (Sy - Ey)
     * TempX = (((Ty - Ey) * (Sx - Ex)) / (Sy - Ey) + Ex
     * TempX = (((Sx - Ex) * (Ty - Ey)) / (Sy - Ey) + Ex
     *
     * @param vertexes 多边形
     * @param target   被测点
     * @return 测试点是否在多边形之内
     */
    public static boolean isPointInPolygon(List<Vertex> vertexes, Vertex target) {

        int intersectCount = 0;
        double precision = 5;  //浮点类型计算时候与0比较时候的容差
        Vertex v1, v2;

        int N = vertexes.size();
        v1 = vertexes.get(0);
        for (int i = 1; i <= N; i++) {
            if (target.equals(v1)) {  // 目标点刚好在顶点上
                return true;
            }

            v2 = vertexes.get(i % N);

            if (target.getX() < Math.min(v1.getX(), v2.getX()) || target.getX() > Math.max(v1.getX(), v2.getX())) {
                v1 = v2;
                continue;
            }

            if (target.getX() > Math.min(v1.getX(), v2.getX()) && target.getX() < Math.max(v1.getX(), v2.getX())) {
                // 被测试点的纵坐标 是否在本次循环所测试的两个相邻点纵坐标范围之内
                if (target.getY() <= Math.max(v1.getY(), v2.getY())) {
                    //目标在垂直线上
                    if (Double.valueOf(v1.getX()).equals(v2.getX()) && target.getY() >= Math.min(v1.getY(), v2.getY())) {
                        return true;
                    }
                    if (Double.valueOf(v1.getY()).equals(v2.getY())) {
                        if (Double.valueOf(target.getY()).equals(v1.getY())) {
                            return true;
                        } else {
                            intersectCount++;
                        }
                    } else {
                        Double v1x = v1.getX();
                        Double v1y = v1.getY();
                        Double v2x = v2.getX();
                        Double v2y = v2.getY();
                        double ty = (target.getX() - v1x) * (v2y - v1y) / (v2x - v1x) + v1y;
                        if (Math.abs(target.getY() - ty) < precision) return true; // 在斜线附近,近似看成在斜线上，返回true
                        if (target.getY() < ty) intersectCount++;
                    }
                }
            } else {
                if (Double.valueOf(target.getX()).equals(v2.getX()) && target.getY() <= v2.getY()) {
                    Vertex v3 = vertexes.get((i + 1) % N);
                    if (target.getX() >= Math.min(v1.getX(), v3.getX()) && target.getX() <= Math.max(v1.getX(), v3.getX())) {
                        intersectCount++;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            v1 = v2;
        }
        //偶数在多边形外，奇数在多边形内
        return (intersectCount & 1) != 0;
    }

    public static class Vertex {
        private double x;
        private double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}


