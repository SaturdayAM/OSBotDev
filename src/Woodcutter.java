import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;


import java.util.function.BooleanSupplier;

/*
    ScriptManifest annotation: required by main class to feed data to OSBot script selector.
 */
@ScriptManifest(author = "CaptFalcon", name="Woodcutting Script", info="A beginner script", version = 0.1,
                logo= "https://png.icons8.com/color/180/runescape.png")

//Docs at:
//https://osbot.org/api/org/osbot/rs07/script/Script.html
//Script class extends MethodProvider, allows access to OSBot API

//Map:
/*
            N y++

   x-- W         E x++

            S y--
 */
public final class Woodcutter extends Script {

    @Override
    public final void onStart(){ //called on script start
        log("Logged on start");
    }


    @Override
    public final int onLoop() throws InterruptedException { //core loop for running script


        //Player position
        Position playerPos = myPosition();

        //Center of large chicken coop near sheep pen in Lumbridge
        Position coopCenter = new Position(3178, 3296, 0);

        //Rectangle Area spanning most of coop. Params are corners x1y1, x2y2
        Area chickenCoop = new Area(3172, 3290, 3183, 3302);

        if(chickenCoop.contains(playerPos)){ //if in coop
            log("in coop")
        } else{
            getWalking().walk(coopCenter);
        }


        return 0; //the return value is the time between each onLoop call, in ms
    }

    @Override
    public final void onExit(){
        log("Logged on exit");
    }

}