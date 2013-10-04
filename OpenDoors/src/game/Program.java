package game;
import gg.PPLGGFront;


public class Program {
	
	public static void main(String[] args){
		PPLGGFront<Map> front = new PPLGGFront<Map>(new OpenDoorsAI(), new MapConverter(), new OpenDoors(), 180,14);
		
		front.generatePLGs(180,14, 15, 25, 7, 20);
		//front.runPPLGGGUI();
	}

}
