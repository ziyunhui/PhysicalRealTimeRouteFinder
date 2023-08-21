package cn.dxp;

import cn.dxp.map.*;
import cn.dxp.math.*;
import cn.dxp.network.*;
import cn.dxp.route.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class PhysicalRealTimeRouteFinder{

  public HttpSender httpSender;
  public MapGenerator mapGenerator;
  public RouteFinder routeFinder;
  public Entity entity = new Entity(0, 0, 0);
  public ArrayList<HashMap<String, Object>> frameQueue = new ArrayList<HashMap<String, Object>>();
  public int currentFrame = 0;
  public long startTime = System.currentTimeMillis();

  public PhysicalRealTimeRouteFinder(){

  }

  public void generateMap(double x, double y, double z){
    this.mapGenerator = new MapGenerator(this, x, y, z);
    this.mapGenerator.start();
  }

  public boolean hasMap(){
    if(this.mapGenerator != null){
      if(this.mapGenerator.isSuccess()){
        return true;
      }
    }
    return false;
  }

  public HashMap<Vector3, Integer> getMap(){
    if(this.hasMap()){
      return this.mapGenerator.mapData;
    }
    return new HashMap<Vector3, Integer>();
  }

  public void findRoute(HashMap<Vector3, Integer> map, Vector3 startPoint, Vector3 endPoint,
      int threadCount){
    this.routeFinder = new AdvancedRouteFinder(this, map, this.entity, threadCount);
    this.routeFinder.setStart(startPoint);
    this.routeFinder.setDestination(endPoint);
    this.routeFinder.taskSearch();
  }

  public void setEntity(Entity e){
    this.entity = e;
  }

  public boolean hasNewFrame(){
    if(this.frameQueue.size() > 0){
      return true;
    }
    return false;
  }

  public HashMap<String, Object> getNewFrame(){
    return this.frameQueue.get(0);
  }

  public boolean deleteNewFrame(){
    this.frameQueue.remove(0);
    return true;
  }

  public void addNewFrame(HashMap<String, Object> f){
    this.frameQueue.add(f);
  }

  public void resetHttpSender(){
    if(this.httpSender == null){
      this.httpSender = new HttpSender(this);
    }else{
      HttpSender old = this.httpSender;
      this.httpSender = new HttpSender(this);
      this.httpSender.dataQueue = old.dataQueue;
    }
    this.httpSender.start();
  }

  public String encodeMapJsonData(){
    JSONObject jsonData = new JSONObject();
    HashMap<Vector3, Integer> mdata = this.mapGenerator.mapData;
    JSONArray map = new JSONArray();
    int mapindex = 0;
    Iterator<Vector3> it = (Iterator<Vector3>) mdata.keySet().iterator();
    while(it.hasNext()){
      Vector3 pos = (Vector3) it.next();
      JSONArray point = new JSONArray();
      point.put(0, pos.x);
      point.put(1, pos.y);
      point.put(2, pos.z);
      point.put(3, mdata.get(pos));
      map.put(mapindex, point);
      mapindex++;
    }
    jsonData.put("DataType", "Map");
    jsonData.put("Map", map);
    return jsonData.toString();
  }

  @SuppressWarnings("unchecked")
  public String encodeFrameJsonData(){
    HashMap<String, Object> fdata = this.getNewFrame();
    if(fdata == null){
      return "";
    }
    ArrayList<Vector3> rdata = (ArrayList<Vector3>) fdata.get("Route");
    JSONObject jsonData = new JSONObject();
    JSONObject en = new JSONObject();
    JSONArray route = new JSONArray();
    JSONObject info = new JSONObject();
    for(int i = 0; i < rdata.size(); i++){
      Vector3 pos = rdata.get(i);
      JSONArray point = new JSONArray();
      point.put(0, pos.x);
      point.put(1, pos.y);
      point.put(2, pos.z);
      route.put(i, point);
    }
    HashMap<String, Object> idata = (HashMap<String, Object>) fdata.get("Info");
    Iterator<String> it = (Iterator<String>) idata.keySet().iterator();
    while(it.hasNext()){
      String k = (String) it.next();
      info.put(k, idata.get(k));
    }
    info.put("RunTime", System.currentTimeMillis() - this.startTime);
    info.put("FrameNumber", this.currentFrame);
    en.put("Position",
        new JSONArray().put(0, this.entity.x).put(1, this.entity.y).put(2, this.entity.z));
    en.put("BoundingBox", new JSONArray().put(0, this.entity.getBoundingBox().x)
        .put(1, this.entity.getBoundingBox().y).put(2, this.entity.getBoundingBox().z));
    en.put("Rotation", new JSONArray().put(0, this.entity.pitch).put(1, this.entity.yaw));
    jsonData.put("DataType", "Frame");
    jsonData.put("Route", route);
    jsonData.put("Info", info);
    jsonData.put("Entity", en);
    System.out.println(this.currentFrame+"帧已打包，耗时"+String.valueOf(idata.get("FrameCalculateTime")));
    this.currentFrame++;
    this.deleteNewFrame();
    return jsonData.toString();
  }
}
