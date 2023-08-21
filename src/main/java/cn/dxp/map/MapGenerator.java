package cn.dxp.map;

import java.util.*;

import cn.dxp.math.*;
import cn.dxp.*;

public class MapGenerator extends Thread{

    public PhysicalRealTimeRouteFinder p;
    public HashMap<Vector3, Integer> mapData = new HashMap<Vector3, Integer>();
    public Vector3 startPoint;
    public Vector3 endPoint;
    private boolean success = false;
    private Vector3 size;
    private long startTime;
    public long usedTime;

    public MapGenerator(PhysicalRealTimeRouteFinder prtrf, double x, double y, double z){
        this.p = prtrf;
        this.size = new Vector3(x, y, z);
    }

    // 0 air
    // 1 stone
    // 2 water
    // 3

    @Override
    public void run(){
        startTime = System.currentTimeMillis();
        this.mapData.putAll(generateLand(this.size));
        this.mapData = addAir(this.mapData, this.size);
        this.spawnPos();
        this.success = true;
        usedTime = System.currentTimeMillis() - startTime;
    }

    public void spawnPos(){
        for(int i = (int) size.y; i >= 0; i--){
            if(this.mapData.containsKey(new Vector3(0, i, 0))){
                if(this.mapData.get(new Vector3(0, i, 0)) == 1){
                    if(i == (int) size.z){
                        this.mapData.put(new Vector3(0, i - 1, 0), 1);
                        this.mapData.remove(new Vector3(0, i, 0));
                        i--;
                    }else{
                        this.startPoint = new Vector3(0, i + 1, 0);
                        break;
                    }
                }
            }
        }
        for(int i = (int) size.y; i >= 0; i--){
            if(this.mapData.containsKey(new Vector3(size.x - 1, i, size.z - 1))){
                if(this.mapData.get(new Vector3(size.x - 1, i, size.z - 1)) == 1){
                    if(i == (int) size.z){
                        this.mapData.put(new Vector3(size.x - 1, i - 1, size.z - 1), 1);
                        this.mapData.remove(new Vector3(size.x - 1, i, size.z - 1));
                        i--;
                    }else{
                        this.endPoint = new Vector3(size.x - 1, i + 1, size.z - 1);
                        break;
                    }
                }
            }
        }
    }

    public static HashMap<Vector3, Integer> generateLand(double x, double y, double z){
        return generateLand(new Vector3(x, y, z));
    }

    public static HashMap<Vector3, Integer> generateLand(Vector3 landSize){
        HashMap<Vector3, Integer> mapDataCache = new HashMap<Vector3, Integer>();
        double h = 6;
        if(landSize.z < 2){
            h = 1;
        }
        for(double x = -2; x < landSize.x + 2; x++){
            for(double z = -2; z < landSize.z + 2; z++){
                mapDataCache.put(new Vector3(x, -1, z), 1);
            }

        }
        for(double x = 0; x < landSize.x; x++){
            for(double y = 0; y < h; y++){
                for(double z = 0; z < landSize.z; z++){
                    mapDataCache.put(new Vector3(x, y, z), 1);
                }
            }
        }
        return mapDataCache;
    }

    public static HashMap<Vector3, Integer> generateObstacle(double x, double y, double z){
        return generateObstacle(new Vector3(x, y, z));
    }

    public static HashMap<Vector3, Integer> generateObstacle(Vector3 landSize){
        HashMap<Vector3, Integer> mapDataCache = new HashMap<Vector3, Integer>();

        for(double x = 0; x < landSize.x; x++){
            for(double z = 0; z < landSize.z; z++){
                if(Math.random() <= 0.2){
                    int h = 0;
                    if(Math.random() <= 0.6){
                        h = 1;
                    }else{
                        h = 2;
                    }
                    for(double y = 6; y < 6 + h; y++){
                        mapDataCache.put(new Vector3(x, y, z), 1);
                    }
                }
            }
        }
        return mapDataCache;
    }

    public static HashMap<Vector3, Integer> addAir(HashMap<Vector3, Integer> map, Vector3 landSize){
        for(double x = -1; x < landSize.x; x++){
            for(double y = -1; y <= landSize.y + 1; y++){
                for(double z = -1; z < landSize.z; z++){
                    if(!map.containsKey(new Vector3(x, y, z))){
                        map.put(new Vector3(x, y, z), 0);
                    }
                }
            }
        }
        return map;
    }

    public boolean isSuccess(){
        return(this.success && this.mapData.size() > 0);
    }
}
