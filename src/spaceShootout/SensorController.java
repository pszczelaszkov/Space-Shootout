package spaceShootout;

public class SensorController
{
    private float x,y,z;
    boolean horizontal;
    SensorController(boolean horizontal)
    {
        x = y = z = 0;
        this.horizontal = horizontal;
    }
    
    public float getX(){return x;}
    public float getY(){return y;}
    public float getZ(){return z;}
    
    public void setX(float arg)
    {
        if(horizontal)
            y = arg;
        else
            x = arg;
    }
    public void setY(float arg)
    {
        if(horizontal)
            x = arg;
        else
            y = arg;
    }
    public void setZ(float arg){z = arg;}
}
