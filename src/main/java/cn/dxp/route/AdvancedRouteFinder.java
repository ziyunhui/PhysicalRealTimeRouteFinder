package cn.dxp.route;

import cn.dxp.*;
import cn.dxp.math.*;

import java.util.*;

public class AdvancedRouteFinder extends RouteFinder{
    private boolean succeed = false, searching = false;
    //private Vector3 realDestination = null;
    private HashSet<Node> open = new HashSet<Node>();
    private Grid grid = new Grid();
    public int heightLimit = 1;


    public AdvancedRouteFinder(PhysicalRealTimeRouteFinder prtrf, HashMap<Vector3, Integer> m,
            Entity entity, int threadCount){
        super(prtrf, m, entity, threadCount);
    }

    @Override
    public boolean search(){
        this.startTime = System.currentTimeMillis();
        this.stopRouteFindUntil = System.currentTimeMillis() + 10000;
        this.succeed = false;
        this.searching = true;
        this.resetNodes();
        this.grid.clear();
        open = new HashSet<Node>();

        if(this.getStart() == null || this.getDestination() == null){
            return this.succeed = this.searching = false;
        }
        Vector3 r = this.getWalkablePosition((Vector3) this.getDestination()).floor();
        /*if(r.equals(new Vector3(-1, -100, -1)) || r.equals(new Vector3(-1, -101, -1))){
            return this.succeed = this.searching = false;
        }*/

        Node start = new Node(this.getStart().floor());
        Node endNode = new Node(r);
        try{
            start.f = start.g = 0;
            open.add(start);
            this.grid.putNode(start);
            this.grid.putNode(endNode);
        }catch (Exception e){
            return this.succeed = this.searching = false;
        }

        int limit = 100000;
        while(!open.isEmpty() && limit-- > 0){
            if(this.forceStop){
                this.resetNodes();
                this.forceStop = false;
                return this.succeed = this.searching = false;
            }
            Node node = null;

            double f = Double.MAX_VALUE;
            try{
                for(Node cur: this.open){
                    if(cur.f < f && cur.f != -1){
                        node = cur;
                        f = cur.f;
                    }
                }
            }catch (Exception e){
                this.searching = false;
                this.succeed = true;
                nodes.add(new Node(this.getDestination()));
                return true;
            }

            if(node == null){
                this.searching = false;
                this.succeed = true;
                nodes.add(new Node(this.getDestination()));
                return true;
            }

            if(endNode.equals(node)){
                ArrayList<Node> nodes = new ArrayList<Node>();
                nodes.add(node);
                //Node last = node;
                while((node = node.getParent()) != null){
                    nodes.add(new Node(node.getVector3().add(0.5, 0, 0.5)));
                    if(this.forceStop){
                        this.resetNodes();
                        this.forceStop = false;
                        return this.succeed = this.searching = false;
                    }
                }
                nodes.remove(nodes.size() - 1);
                Collections.reverse(nodes);

                
                this.addNodes(nodes);

                this.succeed = true;
                this.searching = false;
                return true;
            }
            node.closed = true;
            open.remove(node);

            for(Node neighbor: this.getNeighbors(node)){
                if(neighbor.closed)
                    continue;

                double tentative_gScore = node.g + 1;

                if(!open.contains(neighbor))
                    open.add(neighbor);
                else if(neighbor.g != -1 && tentative_gScore >= neighbor.g)
                    continue;

                neighbor.setParent(node);
                neighbor.g = tentative_gScore;
                neighbor.f = neighbor.g + neighbor.getVector3().distance(endNode.getVector3());

                if(this.forceStop){
                    this.resetNodes();
                    this.forceStop = false;
                    return this.succeed = this.searching = false;
                }
            }
        }

        if(!this.succeed){
            this.searching = false;
            this.succeed = true;
            nodes.add(new Node(this.getDestination()));
        }
        return this.succeed;
    }

    public Set<Node> getNeighbors(Node node){
        HashSet<Node> neighbors = new HashSet<Node>();
        Vector3 vec = node.getVector3();
        boolean bb = false;
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        Vector3 vec1 = this.getWalkablePosition(vec.add(1, 0, 0));
        Vector3 vec2 = this.getWalkablePosition(vec.add(0, 0, 1));
        Vector3 vec3 = this.getWalkablePosition(vec.add(-1, 0, 0));
        Vector3 vec4 = this.getWalkablePosition(vec.add(0, 0, -1));
        Vector3 vec5;
        Vector3 vec6;
        Vector3 vec7;
        Vector3 vec8;
        Vector3 a = new Vector3(-1, -1, -1);
        Vector3 b = new Vector3(-1, -2, -1);
        int bt = map.get(vec.add(0, 2, 0));
        if(bt == 0){
            bb = true;
        }

        if(!vec1.equals(b)){
            if(vec1.y <= vec.y){
                b1 = true;
            }
            if(!vec1.equals(a) && bb){
                neighbors.add(this.grid.getNode(vec1));
            }
        }
        if(!vec2.equals(b)){
            if(vec2.y <= vec.y){
                b2 = true;
            }
            if(!vec2.equals(a) && bb){
                neighbors.add(this.grid.getNode(vec2));
            }
        }
        if(!vec3.equals(b)){
            if(vec3.y <= vec.y){
                b3 = true;
            }
            if(!vec3.equals(a) && bb){
                neighbors.add(this.grid.getNode(vec3));
            }
        }
        if(!vec4.equals(b)){
            if(vec4.y <= vec.y){
                b4 = true;
            }
            if(!vec4.equals(a) && bb){
                neighbors.add(this.grid.getNode(vec4));
            }
        }

        if(b1 && b2){
            vec5 = this.getWalkablePosition(vec.add(1, 0, 1));
            if(!vec5.equals(a) && !vec5.equals(b)){
                neighbors.add(this.grid.getNode(vec5));
            }
        }
        if(b2 && b3){
            vec6 = this.getWalkablePosition(vec.add(-1, 0, 1));
            if(!vec6.equals(a) && !vec6.equals(b)){
                neighbors.add(this.grid.getNode(vec6));
            }
        }
        if(b3 && b4){
            vec7 = this.getWalkablePosition(vec.add(-1, 0, -1));
            if(!vec7.equals(a) && !vec7.equals(b)){
                neighbors.add(this.grid.getNode(vec7));
            }
        }
        if(b4 && b1){
            vec8 = this.getWalkablePosition(vec.add(1, 0, -1));
            if(!vec8.equals(a) && !vec8.equals(b)){
                neighbors.add(this.grid.getNode(vec8));
            }
        }
        return neighbors;
    }

    public Vector3 getWalkablePosition(Vector3 vec){
        if(this.grid.hasNode(vec)){
            return vec;
        }
        int block = map.get(vec.add(0, 1, 0));
        int block1 = map.get(vec.add(0, 2, 0));
        int block2;
        if(map.get(vec) == 2){
            for(double i = vec.y + 1; i < 255; i++){
                Vector3 v = new Vector3(vec.x, i, vec.z);
                if(map.get(v) == 0){
                    return v.add(0, -1, 0);
                }
                if(map.get(v) == 1){
                    break;
                }
            }
            return vec;
        }
        for(double i = vec.y; i > 0; i--){
            Vector3 v = new Vector3(vec.x, i, vec.z);
            block2 = map.get(v);
            if(block == 0){
                if(this.canWalkOn(block, block1, block2)){
                    return v.add(0, 1, 0);
                }
            }else{
                return new Vector3(-1, -101, -1);
            }
            block1 = block;
            block = block2;
        }
        return new Vector3(-1, -100, -1);
    }

    private boolean canWalkOn(int block, int block1, int block2){
        if(block1 == 0 || block1 == 0){
            if(block2 == 1){
                return true;
            }/*
              * else{ if(this.getEntity() instanceof Swimable && (id == Block.WATER || id ==
              * Block.STILL_WATER)){ if(block.getId() != 0){ return true; } } }
              */
        }
        return false;
    }

    /*private double heuristic(Vector3 one, Vector3 two){
        double dx = Math.abs(one.x - two.x);
        double dy = Math.abs(one.y - two.y);
        double dz = Math.abs(one.z - two.z);

        double max = Math.max(dx, dz);
        double min = Math.min(dx, dz);

        return 0.414 * min + max + dy;
    }*/

    @Override
    public synchronized void resetNodes(){
        super.resetNodes();
    }

    @Override
    public boolean research(){
        this.resetNodes();
        return this.search();
    }

    @Override
    public boolean isSearching(){
        return this.searching;
    }

    @Override
    public boolean isSuccess(){
        return this.succeed;
    }
}
