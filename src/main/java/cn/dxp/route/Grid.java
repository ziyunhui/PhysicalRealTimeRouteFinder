package cn.dxp.route;

import cn.dxp.math.*;

import java.util.concurrent.ConcurrentHashMap;

public class Grid{
    private ConcurrentHashMap<Double, ConcurrentHashMap<Double, ConcurrentHashMap<Double, Node>>> grid =
            new ConcurrentHashMap<Double, ConcurrentHashMap<Double, ConcurrentHashMap<Double, Node>>>();

    public void clear(){
        grid.clear();
    }

    public void putNode(Node node){
        Vector3 vec = node.getVector3().floor();
        if(!grid.containsKey(vec.x)){
            grid.put(vec.x, new ConcurrentHashMap<Double, ConcurrentHashMap<Double, Node>>());
        }
        ConcurrentHashMap<Double, ConcurrentHashMap<Double, Node>> gx = grid.get(vec.x);
        if(!gx.containsKey(vec.y)){
            gx.put(vec.y, new ConcurrentHashMap<Double, Node>());
        }
        gx.get(vec.y).put(vec.z, node);
    }

    public Node getNode(Vector3 vec){
        vec = vec.floor();
        if(!grid.containsKey(vec.x) || !grid.get(vec.x).containsKey(vec.y)
                || !grid.get(vec.x).get(vec.y).containsKey(vec.z)){
            Node node = new Node(vec.x, vec.y, vec.z);
            this.putNode(node);
            return node;
        }
        return grid.get(vec.x).get(vec.y).get(vec.z);
    }

    public boolean hasNode(Vector3 vec){
        vec = vec.floor();
        if(!grid.containsKey(vec.x) || !grid.get(vec.x).containsKey(vec.y)
                || !grid.get(vec.x).get(vec.y).containsKey(vec.z)){
            return false;
        }
        return true;
    }
}
