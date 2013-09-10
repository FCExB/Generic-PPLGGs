package agents;


import gg.GameMapConverter;

import java.io.Serializable;

import pplgg.MapManager;
import pplgg.Position;

public class GAgent extends AbstractAgent implements Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7062295966164501174L;

    // ENUMERATION 
    private enum MoveStyles{
        VECTOR,													//Follows a Vector
        RANDOM_DIRECTION,										//Changes Direction randomly moves 1 step, could maybe be excluded fuctionality is found in random step, when StepSize is 1
        RANDOM_STEP, 											//Steps in Random Direction 
        FIXED_STEP;												// Doesn't Change Direction.

    }

    private enum TriggerState{
        TRIGGER_FOUND_ON_POS,									// If Trigger is found on position;
        ALWAYS_ON,
        AREA_FULL,												//Area full of triggerTerrain
        AREA_NONE,												//No TriggerTerrain is Found
        TIMER,													//Triggers in Intervals defined by intervalSteps, will be reset every time it is triggered
        PROBABILITY;                                             //Triggers with a certain probability
    }



    private enum MoveStylesBoundaries{
        START,
        RANDOM_WHITHIN_AREA,
        BOUNCE;
    }

    // actions
    private enum Actions{
        PLACE_AT_POSITION, 
        ROOM_AT_POSITION, 
        CIRCLE_AT_POSITION, 
        PLATFORM_AT_POSITION, 
        CROSS_AT_POSITION, 
        RECT_AT_POSITION, 
        WALL_AT_POSITION, 
        GAME_OF_LIFE_IN_AREA
    }

    // SETTING THE AGENT TYPE
    private MoveStyles moveStyle;								//how will the agent move
    private MoveStylesBoundaries moveStyleBoundaries;			//how will the agent move when it hits a boundary	
    private TriggerState trigger;								//what will trigger the agent to perform an action
    private Actions triggedAction;								//what Action will it perform


    int triggerTerrain;										//Kind of Terrain that the agents react to
    int actionTerrain;										//Terrain that the Agent will place in the level
    int switchTerrain;                                      //When ever the agent uses the switch action this is what it will switch to;

    // VARIABLES--MOVEMENT
    Position thisSpawnPos;

    // random initializer values thes should probably be a part of the generator
    private int minWidth = 2;
    private int maxWidth = 4;
    private int minHeight = 2;
    private int maxHeight = 4;
    private int minStepSize = 1;
    private int maxStepSize = 5;
    private int minTimeStep = 1;
    private int maxTimeStep = 100;
    private float minProbability = 0;
    private float maxProbability = 1;

    private int areaWidth;
    private int areaHeight;
    private int stepX;
    private int stepY;

    private int intervalTimeSteps;
    private int currentTimeStep;

    private float probability;

    private float xSpeed;
    private float ySpeed;

    // used to determine direction of next move
    private int xDirection;
    private int yDirection;
    private int[] possibleDirections = {
                    1,
                    -1
    };

    private final static int PARAMETER_WIDTH = 0;
    private final static int PARAMETER_HEIGHT = 1;
    /// booleans regarding

    private boolean[] change_parameters;


    //private boolean change_stepY;
    //private boolean change_stepX;
    //private boolean change_xDirection;
    //private boolean change_yDirection;
    //private boolean change_xSpeed;
    //private boolean change_ySpeed;

    public GAgent clone() {
        GAgent clone;
        try {
            clone = (GAgent) super.clone();
        }
        catch (CloneNotSupportedException e) {
            System.out.println("CLONE DOES NOT WORK");
            e.printStackTrace();
            clone = null;
        }

        return clone;
    }
    
    public GAgent(){
    	
    }
    
    public GAgent(GameMapConverter<?> converter) {
        super(converter);

        //setting all parameters used in postAction to true
        change_parameters = new boolean[2];
        for (int i = 0; i<change_parameters.length;i++){
            change_parameters[i] = true;
        }


        postAction();


        // section to initialize each agent setting all the variables
        xSpeed = (float)Math.random();
        ySpeed = (float)Math.random();

        xDirection = possibleDirections[(int)Math.random()*possibleDirections.length];
        yDirection = possibleDirections[(int)Math.random()*possibleDirections.length];

        stepX = randomBetween(minStepSize, maxStepSize);
        stepY = randomBetween(minStepSize, maxStepSize);

        intervalTimeSteps = randomBetween(minTimeStep, maxTimeStep);

        probability = minProbability+(float)(Math.random()*(maxProbability-minProbability));


        triggerTerrain = (int)(converter.numOfTileTypes()*Math.random());

        actionTerrain = (int)(converter.numOfTileTypes()*Math.random());
        
        switchTerrain = (int)(converter.numOfTileTypes()*Math.random());


        // setting the moveStyle
        moveStyle = MoveStyles.values()[(int)(Math.random()*MoveStyles.values().length)];

        // setting the move style when on Boundary
        moveStyleBoundaries = MoveStylesBoundaries.values()[(int)(Math.random()*MoveStylesBoundaries.values().length)];

        // setting the trigger
        trigger = TriggerState.values()[(int)(Math.random()*TriggerState.values().length)];



        // setting the action it is supposed to perform
        triggedAction = Actions.values()[(int)(Math.random()*Actions.values().length)];

        // setting what will be changeable
        for (int i = 0; i<change_parameters.length;i++){
            change_parameters[i] = Math.random() < 0.5 ? true : false;
        }


    }


    private void postAction(){

        //recalculate width
        if (change_parameters[PARAMETER_WIDTH]) areaWidth = randomBetween(minWidth, maxWidth);

        // recalculate height
        if (change_parameters[PARAMETER_HEIGHT]) areaHeight = randomBetween(minHeight, maxHeight);
    }

    public void initialize(MapManager newManager, int tokens, Position spawnPos_, int waitingPeriod){

        // same as super
        this.waitingPeriod = waitingPeriod;
        setTokens( tokens );
        x = (int)spawnPos_.x;
        y = (int)spawnPos_.y;
        registerWithManager( newManager );
        width = this.askForMapWidth();
        height = this.askForMapHeight();
        currentTimeStep = 0;
        thisSpawnPos = new Position (spawnPos_.x, spawnPos_.y);

    }


    // make postActionStep with booleans
    // 
    @Override
    public void performStep() {
        // move

        move();


        // if triggered, doAction
        if (isTriggered()){
            performAction();
            //post Action Step regarding variables
            //postAction();
        }

        // else do nothing


    }

    private void move() {

        float tmpX;
        float tmpY;

        switch(moveStyle){

            case VECTOR:
                tmpX = x + xSpeed*xDirection;
                tmpY = y + ySpeed*yDirection;
                break;

            case RANDOM_DIRECTION:
                int dx, dy;

                switch ((int)(4*Math.random())) {
                    case 0:
                        dx = 1;
                        dy = 0;
                        break;
                    case 1:
                        dx = 0;
                        dy = -1;
                        break;
                    case 2:
                        dx = -1;
                        dy = 0;
                        break;
                    case 3:
                        dx = 0;
                        dy = 1;
                        break;
                    default:
                        dx = 0; dy = 0;
                        break;
                }

                tmpX = x+dx;
                tmpY = y+dy;

                if (isOnMap((int)tmpX,(int)tmpY)) 
                    moveTo((int)tmpX, (int)tmpY);
                else onBoundaries((int)tmpX,(int)tmpY);

                break;

            case RANDOM_STEP:
                int dx1, dy1;
                stepX = randomBetween(minStepSize, maxStepSize);
                stepY = randomBetween(minStepSize, maxStepSize);


                switch ((int)(4*Math.random())) {
                    case 0:
                        dx1 = 1;
                        dy1 = 0;
                        break;
                    case 1:
                        dx1 = 0;
                        dy1 = -1;
                        break;
                    case 2:
                        dx1 = -1;
                        dy1 = 0;
                        break;
                    case 3:
                        dx1 = 0;
                        dy1 = 1;
                        break;
                    default:
                        dx1 = 0; dy1 = 0;
                        break;
                }

                tmpX = x+stepX*dx1;
                tmpY = y+stepY*dy1;



                break;

            case FIXED_STEP:
                tmpX = x+stepX*xDirection;
                tmpY = y+stepY*yDirection;




                break;

            default: 
                tmpX = -1;
                tmpY = -1;
                System.out.println("JEPPE AND MANUEL WATCH OUT THERE IS ASDOMETINGHIEDSA NLFAWROBNG!");
                break;
        }

        if (isOnMap((int)tmpX,(int)tmpY)) 
            moveTo((int)tmpX, (int)tmpY);
        else 
            onBoundaries((int)tmpX,(int)tmpY);


    }

    private void onBoundaries(int tmpX, int tmpY){
        // check whichBoundary it is
        switch (moveStyleBoundaries){
            case START:
                moveTo((int)thisSpawnPos.x,(int)thisSpawnPos.y);
                break;

            case RANDOM_WHITHIN_AREA:
                int newX,newY;

                do {
                    newX = (int)(thisSpawnPos.x + randomBetween((-0.5*areaWidth), (0.5*areaWidth)));
                    newY = (int)(thisSpawnPos.y + randomBetween((-0.5*areaHeight),(0.5*areaHeight)));

                } while (!isOnMap(newX,newY));

                moveTo(newX,newY);
                break;

            case BOUNCE:
                if (tmpX<0||tmpX>width-1)
                    xDirection *= -1;
                if (tmpY<0||tmpY>height-1)
                    yDirection *= -1;
                break;
        }
    }

    boolean isTriggered(){
        boolean b = false;
        switch(trigger){

            case TRIGGER_FOUND_ON_POS:
                if (requestMapInformation((int)x, (int)y)==triggerTerrain) b = true;
                break;

            case ALWAYS_ON:
                b = true;
                break;

                // is trigged when all the area is full of trigger Terrain
            case AREA_FULL:
                b = true;
                for (int x=0;x<areaWidth;x++){
                    for (int y=0;y<areaHeight;y++){
                        int tmpX = (int)(this.x+x);
                        int tmpY = (int)(this.y+y);
                        if (isOnMap(tmpX, tmpY)) {
                            if (requestMapInformation(tmpX, tmpY)!=triggerTerrain){
                                b = false;
                                break;
                            }
                        }
                    }		
                }



                break;

            case AREA_NONE:
                b = true;
                for (int x=0;x<areaWidth;x++){
                    for (int y=0;y<areaHeight;y++){
                        int tmpX = (int)(this.x+x);
                        int tmpY = (int)(this.y+y);
                        if (isOnMap(tmpX, tmpY)) {
                            if (requestMapInformation(tmpX, tmpY)==triggerTerrain){
                                b = false;
                                break;
                            }
                        }
                    }		
                }

                break;

            case PROBABILITY:
                if (Math.random()<probability) b=true;
                break;

            case TIMER:
                if (currentTimeStep>=intervalTimeSteps){
                    b=true;
                    currentTimeStep = 0;
                }
                else
                    currentTimeStep++;
                break;

        }
        return b;
    }


    private void performAction(){
        switch(triggedAction){

            case PLACE_AT_POSITION:
                checkAndSendMapRequest((int)x, (int)y, actionTerrain);
                break;

            case ROOM_AT_POSITION:
                // creating floor and ceiling
                int y1 = (int)y;
                int y2 = (int)(y + areaHeight-1);
                int x1 = (int)x;
                int x2 = (int)(x+areaWidth-1);

                for (int x=0;x<areaWidth;x++){
                    int tmpX = x1+x;
                    checkAndSendMapRequest(tmpX, y1, actionTerrain);
                    checkAndSendMapRequest(tmpX, y2, actionTerrain);

                }
                // creating walls
                for (int y=0;y<areaHeight;y++){
                    int tmpY = y1+y;
                    checkAndSendMapRequest(x1, tmpY, actionTerrain);
                    checkAndSendMapRequest(x2, tmpY, actionTerrain);

                }		
                break;

            case CIRCLE_AT_POSITION:
                // grows around point, action parameter = radius
                float range = (float)Math.PI*2;
                float steps = range/100;
                float xRadius = (float)0.5*areaWidth;
                float yRadius = (float)0.5*areaHeight;
                int oldX = -1;
                int oldY = -1;
                for (float d = 0; d < range; d+=steps){

                    int newX = (int) (this.x + xRadius*Math.cos(d));
                    int newY = (int) (this.y - yRadius*Math.sin(d));
                    if (newX!=oldX||newY!=oldY){
                        checkAndSendMapRequest(newX, newY, actionTerrain);
                        oldX = newX;
                        oldY = newY;
                    }

                }

                break;

            case PLATFORM_AT_POSITION:
                for (int x=0; x< areaWidth; x++){
                    int tmpX = (int)(this.x+x);
                    checkAndSendMapRequest(tmpX, (int)y, actionTerrain);
                }

                break;

            case CROSS_AT_POSITION:
                int xRange = (int)(areaWidth*0.5);
                int yRange = (int)(areaHeight*0.5);
                for (int x=-xRange;x<=xRange;x++){
                    int tmpX = (int)(this.x+x);
                    checkAndSendMapRequest(tmpX, (int)y, actionTerrain);

                }

                for (int y=-yRange;y<=yRange;y++){
                    int tmpY = (int)(this.y+y);
                    checkAndSendMapRequest((int)x, tmpY, actionTerrain);

                }
                break;

            case RECT_AT_POSITION:
                for (int x=0;x<areaWidth;x++){
                    for (int y=0;y<areaHeight;y++){
                        int tmpX = (int)(this.x+x);
                        int tmpY = (int)(this.y+y);
                        checkAndSendMapRequest(tmpX, tmpY, actionTerrain);

                    }
                }

                break;

            case WALL_AT_POSITION:
                for (int y=0; y< areaHeight; y++){
                    int tmpY = (int)(this.y+y);
                    checkAndSendMapRequest((int)this.x, tmpY, actionTerrain);

                }
                break;

            case GAME_OF_LIFE_IN_AREA:
                int xRange1 = (int)(areaWidth*0.5);
                int yRange1 = (int)(areaHeight*0.5);
                for (int x = -xRange1; x <=xRange1; x++){
                    int tmpX = (width+(int)this.x+x)%width;//Special Wrap Around Case
                    for (int y = -yRange1; y <= yRange1; y++){
                        int tmpY = (height+(int)this.y+y)%height;//Again Wrap Around Case

                        //put game of life code here
                        int neighborsAlive = 0;

                        // check neighbors (re-wrap on map)
                        //for (int checkX = -1 ; checkX )

                        //Check neighbor cells moving one y up and down
                        for (int i=-1; i<=1;i++){
                            if (isAlive((width + tmpX+i)%width,(height+tmpY-1)%height)) neighborsAlive++;
                            if (isAlive((width + tmpX+i)%width,(height+tmpY+1)%height)) neighborsAlive++;

                        }

                        if (isAlive((width + tmpX+1)%width, tmpY)) neighborsAlive++;
                        if (isAlive((width + tmpX-1)%width, tmpY)) neighborsAlive++;

                        // GameOFLIFE
                        if (isAlive(tmpX,tmpY)){
                            if (neighborsAlive<2 || neighborsAlive > 3){
                                checkAndSendMapRequest(tmpX, tmpY, this.actionTerrain);

                            }
                        }
                        else if (neighborsAlive==3){
                            checkAndSendMapRequest(tmpX, tmpY, this.switchTerrain);

                        }




                    }

                }


        }



    }

    private void checkAndSendMapRequest(int x, int y, int t){
        if (isOnMap(x,y)){
            sendMapRequest(x, y, t);
            decreaseTokens(1);
        }
    }
    private boolean isAlive(int x,int y){
        boolean b=false;
        if (requestMapInformation(x, y)==actionTerrain){
            b = true;
        }

        return b;
    }
    
    public void mutate(){
        int numCases = 1;
        float mutationFactor = 0.1f;
        int current = (int)(Math.random()*numCases);

        switch(current){


        case 0:

            xSpeed += (-mutationFactor + ((float)Math.random()*mutationFactor*2));
            break;

        case 1:
            ySpeed += (-mutationFactor + ((float)Math.random()*mutationFactor*2));
            break;

        case 2:
            xDirection *= -1;
            break;

        case 3:
            yDirection *= -1;
            break;


        case 4:
            int range = maxStepSize-minStepSize;
            int stepChange = (int)((Math.random()-0.5)*range*mutationFactor*2);

            int tmpStep = stepX + stepChange;
            stepX = Math.min( Math.max( minStepSize, tmpStep ), maxStepSize );
            //Math.min( Math.max( 0, pos.y), Generator.height-1 );
            break;

        case 5:
            int range2 = maxStepSize-minStepSize;
            int stepChange2 = (int)((Math.random()-0.5)*range2*mutationFactor*2);
            int tmpStep2 = stepY + stepChange2;
            stepY = Math.min( Math.max( minStepSize, tmpStep2 ), maxStepSize );
            //Math.min( Math.max( 0, pos.y), Generator.height-1 );

            break;

        case 6:
            int range3 = maxTimeStep-minTimeStep;
            int timeChange = (int)((Math.random()-0.5)*range3*mutationFactor*2);
            int tmpTime = intervalTimeSteps + timeChange;
            intervalTimeSteps = Math.min( Math.max( minTimeStep, tmpTime ), maxTimeStep );
            break;

        case 7:
            float range4 = maxProbability-minProbability;
            float probabilityChange = (float)((Math.random()-0.5)*range4*mutationFactor*2);
            float tmpProbability = probability + probabilityChange;
            probability = Math.min( Math.max( minProbability, tmpProbability ), maxProbability );
            break;

        case 8:
            triggerTerrain = (int)(converter.numOfTileTypes()*Math.random());
            break;
            
        case 9:
            actionTerrain = (int)(converter.numOfTileTypes()*Math.random());
            break;
        case 10:
            switchTerrain = (int)(converter.numOfTileTypes()*Math.random());
            break;
        case 11:
            moveStyle = MoveStyles.values()[(int)(Math.random()*MoveStyles.values().length)];
            break;
        case 12:
            moveStyleBoundaries = MoveStylesBoundaries.values()[(int)(Math.random()*MoveStylesBoundaries.values().length)];
            break;
        case 13:
            trigger = TriggerState.values()[(int)(Math.random()*TriggerState.values().length)];
            break;
            
        case 14:
            triggedAction = Actions.values()[(int)(Math.random()*Actions.values().length)];
            break;
            
        case 15:
            int p = (int)(Math.random()*change_parameters.length);
            change_parameters[p]=!change_parameters[p];
            break;

        default:
            break;

        }

    }
    
}


