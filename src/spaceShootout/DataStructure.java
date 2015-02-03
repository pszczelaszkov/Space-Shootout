package spaceShootout;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;

public class DataStructure implements Savable
{
    int exhausted,damage,dodgeMaxDistance,level,upgradePoints,target,maxCombo;
    String token;
    
    public void write(JmeExporter ex) throws IOException 
    {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(exhausted,"exhausted",300);
        capsule.write(damage,"damage",100);
        capsule.write(dodgeMaxDistance,"dodgeMaxDistance",1000);
        capsule.write(level,"level",1);
        capsule.write(upgradePoints,"upgradePoints",20);
        capsule.write(target,"target",0);      
        capsule.write(maxCombo,"maxCombo",0);
        capsule.write(token,"token","");
    }

    public void read(JmeImporter im) throws IOException 
    {
        InputCapsule capsule = im.getCapsule(this);
        exhausted = capsule.readInt("exhausted",300);
        damage = capsule.readInt("damage",100);
        dodgeMaxDistance = capsule.readInt("dodgeMaxDistance",1000);
        level = capsule.readInt("level",1);
        upgradePoints = capsule.readInt("upgradePoints",20);
        target = capsule.readInt("target",0);
        maxCombo = capsule.readInt("maxCombo",0);
        token = capsule.readString("token","");
        
    }
}
