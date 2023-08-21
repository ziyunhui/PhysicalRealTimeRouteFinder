package cn.dxp;

import cn.dxp.math.*;

public class PRTRF{
    static final String version = "v1.0";
    static final int threadCount = 8;

    public static void main(String[] args){
        System.out.println("PhysicalRealTimeRouteFinder " + version);
        PhysicalRealTimeRouteFinder p = new PhysicalRealTimeRouteFinder();
        p.resetHttpSender();
        try{
            System.out.println("绘制地图中...");
            p.generateMap(500, 50, 500);
            while(true){
                if(p.hasMap()){
                    System.out.println("地图数据绘制完成");
                    break;
                }
                Thread.sleep(200);
            }
            System.out.println("生成地图耗时:" + p.mapGenerator.usedTime + "ms");
            System.out.println("发送地图数据");
            p.httpSender.addData(p.encodeMapJsonData());
            System.out.println("计算路线帧...线程数:" + threadCount);
            Entity e = new Entity(p.mapGenerator.startPoint);
            e.setBoundingBox(new Vector3(1, 1, 1));
            p.setEntity(e);
            p.findRoute(p.getMap(), p.mapGenerator.startPoint, p.mapGenerator.endPoint, 8);
            while(true){
                if(p.hasNewFrame()){
                    System.out.println("发送帧 " + p.currentFrame);
                    p.httpSender.addData(p.encodeFrameJsonData());
                }
                Thread.sleep(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
