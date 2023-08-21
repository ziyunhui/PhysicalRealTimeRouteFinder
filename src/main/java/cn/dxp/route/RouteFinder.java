package cn.dxp.route;

import cn.dxp.*;
import cn.dxp.math.*;

import java.util.*;

public abstract class RouteFinder{
	private int current = 0;
	protected Vector3 destination = null, start = null;
	protected ArrayList<Node> nodes = new ArrayList<Node>();
	protected boolean forceStop = false;
	private boolean arrived = false;
	public long stopRouteFindUntil = System.currentTimeMillis();
	public boolean firstSearch = false;

	public HashMap<Vector3, Integer> map;
	private Entity entity;
	public PhysicalRealTimeRouteFinder prtrf;
	public RouteTask rtask;
	public long startTime;

	public RouteFinder(PhysicalRealTimeRouteFinder prtrf, HashMap<Vector3, Integer> m,
			Entity entity, int threadCount){
		this.map = m;
		this.entity = entity;
		this.prtrf = prtrf;
	}

	public void setEntity(Entity e){
		this.entity = e;
	}

	public Entity getEntity(){
		return this.entity;
	}

	public void setStart(Vector3 start){
		this.start = start.clone();
	}

	public Vector3 getStart(){
		return (this.firstSearch ?start : this.entity);
	}

	public void setDestination(Vector3 destination){
		this.destination = destination.clone();
	}

	public Vector3 getDestination(){
		return destination;
	}

	protected void resetNodes(){
		this.nodes.clear();
		this.arrived = false;
		this.current = 0;
	}

	public void forceStop(){
		this.forceStop = true;
		if(!this.isSearching()){
			this.forceStop = false;
			this.resetNodes();
		}
	}

	public void addNode(Node node){
		this.nodes.add(node);
	}

	public void addNodes(ArrayList<Node> node){
		this.nodes.addAll(node);
	}

	public boolean hasNext(){
		return !this.arrived && nodes.size() > this.current + 1;
	}

	public boolean next(){
		if(this.hasNext()){
			this.current++;
			return true;
		}
		return false;
	}

	public boolean hasReachedNode(Vector3 vec){
		Vector3 cur = this.get().getVector3();
		return vec.x == cur.x && vec.y == cur.y && vec.z == cur.z;
	}

	public Node get(){
		if(nodes.size() == 0){
			if(this.getDestination() != null){
				return new Node(this.getDestination());
			}else{
				throw new IllegalStateException("There is no path found.");
			}
		}
		if(this.arrived)
			return null;
		return nodes.get(current);
	}

	public void arrived(){
		this.current = 0;
		this.arrived = true;
	}

	public boolean hasRoute(){
		return this.nodes.size() > 0;
	}

	public void taskSearch(){
		this.rtask = new RouteTask(this);
		this.rtask.start();
	}

	public abstract boolean search();

	public abstract boolean research();

	public abstract boolean isSearching();

	public abstract boolean isSuccess();
}
