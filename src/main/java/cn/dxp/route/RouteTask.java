package cn.dxp.route;

import java.util.*;

public class RouteTask extends Thread{

    public RouteFinder route;

    public RouteTask(RouteFinder route){
        this.route = route;
    }

    @Override
    public void run(){
        try{
            while(true){
                this.route.research();
                if(!this.route.isSuccess()){
                    System.out.println("寻路超时");
                    continue;
                }
                long usedTime = System.currentTimeMillis() - this.route.startTime;
                HashMap<String, Object> fdata = new HashMap<String, Object>();
                HashMap<String, Object> idata = new HashMap<String, Object>();
                idata.put("FrameCalculateTime", usedTime);
                fdata.put("Route", this.route.nodes);
                fdata.put("Info", idata);
                this.route.prtrf.addNewFrame(fdata);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
