import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;

@ScriptManifest(name = "GoblinSlayer", info = "Kills lumbridge goblins", version =1.1 , logo ="" , author = "CaptFalcon")
public class Main extends Script {

    private String targetName = "Chicken";
    private String targetLoot = "Feather";

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        log("Initializing script, with looting v 3.08");
    }

    @Override
    public int onLoop() throws InterruptedException {


        //Select target npc to fight. Can use a filter or a lambda expression.

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

        if(myPlayer().getInteracting() != null || myPlayer().isUnderAttack()){
            //If interacting, return to stop checking until out of combat
            return random(500, 1000);
        }

        //With lambda expression.
        NPC targetNpc = getNpcs().closest(npc -> npc != null &&
                                                 npc.getName().equals(targetName) &&
                                                 npc.exists() &&
                                                 getMap().canReach(npc) &&
                                                 npc.getHealthPercent() > 0 &&
                                                 npc.getInteracting() == null);

        if(targetNpc == null){ //If npc is null, wait for one to spawn
            return random(500, 1000);
        }


        if(!targetNpc.isVisible() ){ //Move camera to npc if needed, then pause
            getCamera().toEntity(targetNpc);
            new ConditionalSleep(random(300,500), 100){
                @Override
                public boolean condition(){
                    return false;
                }
            }.sleep();
        }

        targetNpc.interact("Attack");


//        //ConditionalSleep(int timeout, int sleepTime)
//        //sleepTime between condition checks. Default 25ms.
//        //Most actions will need much more time, reduce CPU load by increasing
//        //sleepTime
//        int timeOut = random(2000, 3000);
//        new ConditionalSleep(timeOut, 100){
//            @Override
//            public boolean condition(){
//                //will keep looping until this returns true
//                //i.e. attacked goblin.
//                //Will stop sleeping between 2-3 seconds
//                return targetNpc.getHealthPercent() == 0;
//            }
//        }.sleep(); //call immediately, sleep


        GroundItem toLoot = groundItems.closest("Feather");

        if(!myPlayer().isUnderAttack()){
            toLoot.interact("Take");
            return random(1500, 2000);

        }
        return random(500, 1000);
    }


    @Override
    public void onExit() throws InterruptedException {
        super.onExit();
    }

}
