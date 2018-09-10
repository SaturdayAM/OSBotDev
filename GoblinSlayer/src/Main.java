import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;

@ScriptManifest(name = "GoblinSlayer", info = "Kills lumbridge goblins", version =1.0 , logo ="" , author = "CaptFalcon")
public class Main extends Script {
    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        log("does this work");
    }

    @Override
    public int onLoop() throws InterruptedException {

        NPC goblin = getNpcs().closest("Goblin");

        if(goblin != null){ //if it exists
            log("Goblin found");
            if(!goblin.isVisible()){
                getCamera().toEntity(goblin);
                new ConditionalSleep(random(500,700), 100){
                    @Override
                    public boolean condition(){
                        return false;
                    }
                }.sleep();
            }

            if(goblin.interact("Attack")){
                log("We have attacked: " + goblin.getName());

                //ConditionalSleep(int timeout, int sleepTime)
                //sleepTime between condition checks. Default 25ms.
                //Most actions will need much more time, reduce CPU load by increasing
                //sleepTime
                int timeOut = random(2000, 3000);
                new ConditionalSleep(timeOut, 1000){
                    @Override
                    public boolean condition(){
                        //will keep looping until this returns true
                        //i.e. attacked goblin.
                        //Will stop sleeping between 2-3 seconds
                        return getCombat().isFighting();
                    }
                }.sleep(); //call immediately, sleep
            }



        }

        return random(100, 300);

    }


    @Override
    public void onExit() throws InterruptedException {
        super.onExit();
    }

    @Override
    public void onPaint(Graphics2D iIIiiiiIiiII) {
        super.onPaint(iIIiiiiIiiII);
    }
}
