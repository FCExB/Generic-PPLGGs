package pplgg.ga;

import ga.Individual;
import pplgg.AgentParams;
import pplgg.Generator;
import pplgg.Position;

public class GeneratorIndividual<T> extends Individual<GeneratorIndividual<T>> {

    Generator<T> myGenerator;
    private static final int spawnRadiusMutation = 4;
    private static final int xPositionMutation = 100;
    private static final int yPositionMutation = 20;
    private static final int spawnTimeMutation = 50;
    private static final int tokenMutation = 10;
    
    public GeneratorIndividual(Generator<T> gen) {
        this.myGenerator = gen;
    }

    @Override
    public GeneratorIndividual<T> copy() {
        Generator<T> genCopy = myGenerator.copy();
        GeneratorIndividual<T> copy = new GeneratorIndividual<T>(genCopy);
        return copy;
    }

    public Generator<T> getGenerator() {
        return myGenerator;
    }

    @Override
    public void mutate() {
        //pick an agent to mutate
        AgentParams toMutate = myGenerator.extractRandomAgent();
        //switch((int)(0)) { //pick a parameter to mutate (adding/removing an agent has smaller possibility)
        toMutate.agentType.mutate();
        switch((int)(Math.random()*2)) { //pick a parameter to mutate (adding/removing an agent has smaller possibility)
            case 0: //mutate waiting time
                toMutate.waitingPeriod = (int)(1+(Generator.maximumWaitingTime-1)*Math.random());
                break;
            case 1: //add or remove an agent with equal chance (except if removing will make generator empty, thus void)
                if (Math.random()<0.5 && myGenerator.getNoAgents()>1)
                    myGenerator.extractRandomAgent();
                else {
                    myGenerator.addRandomAgent();
                }
                break;

        }
        //mutate spawn position
        Position pos = toMutate.pos; 
        int newX = (int)pos.x + ((int)(2*xPositionMutation*Math.random()-xPositionMutation));
        newX = Math.min( Math.max( 0, newX), Generator.width-1 ); //clamp
        int newY = (int)pos.y + ((int)(2*yPositionMutation*Math.random()-yPositionMutation));
        newY = Math.min( Math.max( 0, newY), Generator.height-1 ); //clamp
        toMutate.pos = new Position(newX, newY); 

        //mutate spawn radius
        toMutate.spawnRadius = Math.min(Generator.maximumSpawnRadius, Math.max( 0, toMutate.spawnRadius+2*spawnRadiusMutation*Math.random()-spawnRadiusMutation ));

        //mutate sequencing
        toMutate.spawnTime = Math.min(Generator.maximumSpawnTime, Math.max( 0, (int)(toMutate.spawnTime+2*spawnTimeMutation*Math.random()-spawnTimeMutation )));

        //mutate amount of tokens
        toMutate.tokens = Math.min(Generator.maximumTokens, Math.max( Generator.minimumTokens, (int)(toMutate.tokens+2*tokenMutation*Math.random()-tokenMutation )));

        //put the mutation back in the generator
        myGenerator.addAgent( toMutate );
    }

}
