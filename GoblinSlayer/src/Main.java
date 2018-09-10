import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;

@ScriptManifest(name = "GoblinSlayer", info = "Kills lumbridge goblins", version =1.1 , logo ="" , author = "CaptFalcon")
public class Main extends Script {

    private String targetName = "Chicken";

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        log("Initializing script");
    }

    @Override
    public int onLoop() throws InterruptedException {

        if(getCombat().isFighting()){ //If we're fighting, loop
            return random(500, 700);
        }

        //Select target npc to fight

        //Using a filter

//        NPC targetNpc = getNpcs().closest(new Filter<NPC>() {
//            @Override
//            public boolean match(NPC npc) {
//                //getMap() from map api. Checks if we can reach npc/
//                return npc != null && npc.getName().equals(targetName) //is a chicken
//                                   && npc.exists() //still exists
//                                   && getMap().canReach(npc) //can be reached
//                                   && npc.getHealthPercent() > 0 //not dying
//                                   && npc.getInteracting() == null ; //not fighting somebody else
//            }
//        });


        //With lambda notation
        NPC targetNpc = getNpcs().closest(npc -> npc != null &&
                                                 npc.getName().equals(targetName) &&
                                                 npc.exists() &&
                                                 getMap().canReach(npc) &&
                                                 npc.getHealthPercent() > 0 &&
                                                 npc.getInteracting() == null);

        if(targetNpc != null){

            if(!targetNpc.isVisible() ){ //Move camera to npc if needed, then pause
                getCamera().toEntity(targetNpc);
                new ConditionalSleep(random(300,500), 100){
                    @Override
                    public boolean condition(){
                        return false;
                    }
                }.sleep();
            }

            if(targetNpc.interact("Attack")){ //If we've attacked it
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

}
