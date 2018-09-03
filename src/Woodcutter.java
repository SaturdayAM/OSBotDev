import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

/*
    ScriptManifest annotation: required by main class to feed data to OSBot script selector.
 */
@ScriptManifest(author = "CaptFalcon", name="Woodcutting Script", info="A beginner script", version = 0.1,
                logo= "https://png.icons8.com/color/180/runescape.png")

//Docs at:
//https://osbot.org/api/org/osbot/rs07/script/Script.html
public final class Woodcutter extends Script {

    @Override
    public final void onStart(){ //called on script start
        log("Logged on start");
    }


    @Override
    public final int onLoop() throws InterruptedException { //core loop for running script

        return 0; //the return value is the time between each onLoop call, in ms
    }

    @Override
    public final int onExit(){
        log("Logged on exit");
    }

    @Override
    public final void onMessage(final Message message){
        log("A message arrived in the chatbox: " + message.getMessage());
    }

}
