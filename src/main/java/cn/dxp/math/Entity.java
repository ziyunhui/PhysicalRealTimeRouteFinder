package cn.dxp.math;

public class Entity extends Vector3{

    private Vector3 bb;
    public double pitch;
    public double yaw;

    public Entity(Vector3 vec){
        this(vec.x, vec.y, vec.z);
    }

    public Entity(double x, double y, double z){
        this(x, y, z, 0, 0);
    }

    public Entity(double x, double y, double z, double pi, double ya){
        super(x, y, z);
        this.pitch = pi;
        this.pitch = pi;
    }

    public Vector3 getBoundingBox(){
        return bb;
    }

    public void setBoundingBox(Vector3 b){
        this.bb=b;
    }
}
