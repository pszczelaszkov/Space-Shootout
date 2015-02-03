package spaceShootout;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import java.util.HashMap;
import java.util.Map;

public class AudioManager 
{
    private Node rootNode;
    private AssetManager assetManager;
    private Map nodes;
    private boolean enabled;
    
    AudioManager(Node rootNode,AssetManager assetManager)
    {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        enabled = true;
        nodes = new HashMap<String,AudioNode>();
        addAudioNode("click","Interface/sounds/click.ogg");
        addAudioNode("alert","Interface/sounds/alert.ogg");
        addAudioNode("gateOpening","Sounds/gate.ogg");
        addAudioNode("gunShoot","Sounds/gunshoot.ogg");
        addAudioNode("explosion","Sounds/explosion.ogg");
        addAudioNode("explosion","Sounds/explosion.ogg");
    }
    
    public final void addAudioNode(String name,String path)
    {
            AudioNode audioNode = new AudioNode(assetManager, path, false);
            audioNode.setPositional(false);
            audioNode.setVolume(2);
            rootNode.attachChild(audioNode);
            nodes.put(name, audioNode);
    }
    
    public void playAudioInstance(String name)
    {
        if(enabled)
        {
            AudioNode audioNode = (AudioNode)nodes.get(name);
            audioNode.playInstance();
        }
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
