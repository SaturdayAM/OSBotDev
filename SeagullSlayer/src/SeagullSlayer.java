import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.EntityAPI;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import org.osbot.rs07.api.Combat;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.filter.NameFilter;

import java.util.function.BooleanSupplier;

/*
    ScriptManifest annotation: required by main class to feed data to OSBot script selector.
 */
@ScriptManifest(author = "CaptFalcon", name="Seagull Slayer", info="A beginner script", version = 0.1,
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
public final class SeagullSlayer extends Script {

    @Override
    public final void onStart(){ //called on script start
        log("Logged on start");
    }

    private enum State { //Declare states
        KILL, SLEEP;
    }

    private State getState(){ //returns the state

        NPC seagull = getNpcs().closest(new Filter<NPC>(){
            @Override
            public boolean match(NPC npc){
                return npc != null && npc.exists() &&
                        npc.getName().equals("Seagull") && npc.isAttackable();
            }

        });

        if(seagull != null){
            return State.KILL;
        }
        return State.SLEEP;

    }

    @Override
    public final int onLoop() throws InterruptedException { //core loop for running script


        //Referrenced from: https://osbot.org/forum/topic/70292-vags-chicken-blaster-first-script-ever/?do=findComment&comment=803046
        if(myPlayer().getInteracting() != null){
            //If player is interacting, return to stop checking until out of combat
            return random(500, 1000);
        }

        NPC seagull = getNpcs().closest(new Filter<NPC>() {
            @Override
            public boolean match(NPC npc) {
                return npc != null && npc.exists() &&
                        npc.getName().equals("Seagull") && npc.isAttackable();
            }
        });

        if(seagull == null){
            //chicken is null, wait for one to spawn
            return random(500, 1000);
        }

        if(!seagull.isOnScreen()){
            camera.toEntity(seagull);
            return random(500, 1000);
        }

        seagull.interact("Attack");

        //Player position
        Position playerPos = myPosition();

        //Center of seagulls on docks of port sarim
        Position coopCenter = new Position(3030, 3236, 0);

        Position randomCenter =  new Position(random(3029,3031), random(3235, 3237), 0);

        //Rectangle Area spanning most of coop. Params are corners x1y1, x2y2
        Area seagullCoop = new Area(3026, 3225, 3037, 3237);

        if(!seagullCoop.contains(playerPos)){ //if not in coop
            getWalking().walk(coopCenter);
        }

        //If leveled up, continue
        if(dialogues.inDialogue()){
            dialogues.clickContinue();
        }

        return random(500, 1000); //the return value is the time between each onLoop call, in ms
    }

    @Override
    public final void onExit(){
        log("Logged on exit");
    }

}