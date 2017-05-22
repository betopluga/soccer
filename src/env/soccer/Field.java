package soccer;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;
import jade.domain.introspection.AddedBehaviour;
import java.util.logging.Logger;
import java.util.concurrent.ThreadLocalRandom;

public class Field extends Artifact{
	static int x_size 		= 13;
	static int y_size 		= 9;	
	static int x_center 	= 7;
	static int y_center 	= 5;	
	static int x1_goal1  	= 3;
	static int y1_goal1  	= 1;	
	static int x2_goal1  	= 7;	
	static int y2_goal1  	= 1;	
	static int x1_goal2  	= 3;	
	static int y1_goal2  	= 13;	
	static int x2_goal2  	= 7;
	static int y2_goal2  	= 13;
	static int games;
	static int count_games	= 0;
	final static long PAUSE = 1000;
	private Logger logger = Logger.getLogger("field."+Field.class.getName());
	
	
    public void init(int pgames) {		
		defineObsProperty("x_ballPosition", 0);
		defineObsProperty("y_ballPosition", 0);
		defineObsProperty("scoreB", 0);
		defineObsProperty("scoreR", 0);
		games = pgames;
		//System.out.println(getObsProperty("scoreR").getValue());
    }
		
    //@OPERATION
	public void incrementScore(int y_defender){
		int x_ballPosition = (Integer) getObsProperty("x_ballPosition").getValue();
		int y_ballPosition = (Integer) getObsProperty("y_ballPosition").getValue();
		
		//if ball into the left goal
		if(x_ballPosition==2 && (y_ballPosition>=3 && y_ballPosition <=7) && (y_defender+1) == y_ballPosition){
			int scoreR = (Integer) getObsProperty("scoreR").getValue();
			updateObsProperty("scoreR", scoreR + 1);	
			logger.info("GOAL!");
		//if ball into the right goal
		} else if(x_ballPosition==12 && (y_ballPosition>=3 && y_ballPosition <=7) && (y_defender-1) == y_ballPosition){
			int scoreB = (Integer) getObsProperty("scoreB").getValue();
			updateObsProperty("scoreB", scoreB + 1);
			logger.info("GOAL!");
		} else if((y_ballPosition<3 || y_ballPosition >7)){
			logger.info("PARA FORA!");
		} else {
			logger.info("DEFENDEU!");
		}
	}

	
	@OPERATION
	public void kick(String name, int y_position, int x_position){

		int x_increment;
		int y_increment;
		int x_ballPosition;
		int y_ballPosition;
		
		if(count_games==games){
			logger.info("FINAL SCORE " + getObsProperty("scoreB").getValue() + " x " + getObsProperty("scoreR").getValue());
			return;
		}
		
		count_games++;
		logger.info("GAME " + count_games);
		
		//calcula onde deve estar a bola de acordo com quem está chutando
		y_ballPosition = (Integer) y_position;
		if(x_position>=10){
			x_ballPosition = (Integer) x_position - 1;
		} else {
			x_ballPosition = (Integer) x_position + 1;
		}
		
		//atualiza propriedades observaveis da bola
		updateObsProperty("x_ballPosition", x_ballPosition);
		updateObsProperty("y_ballPosition", y_ballPosition);
		
		logger.info("bola está (" + x_ballPosition + "," + y_ballPosition + ")");
		logger.info("player " + name + " chutou a bola");
		
		//calcula onde esta a bola e onde ela deve chegar
		String defender = "";	
		
		//calcula o deslocamento horizontal
		if(x_ballPosition<7){
			x_increment = 10;
			defender = "R";
		} else if(x_ballPosition>7) {
			x_increment = -10;
			defender = "B";
		} else {
			x_increment = 0;
		}
		
		//calcula o deslocamento vertical randômico
		y_increment = ThreadLocalRandom.current().nextInt(-1, 1 + 1);
		
		//atualiza a posicao da bola
		updateObsProperty("x_ballPosition", x_ballPosition + (x_increment));
		updateObsProperty("y_ballPosition", y_ballPosition + (y_increment));
		
		x_ballPosition = (Integer) getObsProperty("x_ballPosition").getValue();
		y_ballPosition = (Integer) getObsProperty("y_ballPosition").getValue();		
		
		logger.info("bola moveu-se para (" + x_ballPosition + "," + y_ballPosition + ")");
				
		signal("defend", defender);
	}
	
	
	
	@OPERATION
	public void defend(String team, int y_defender){
		int y_ballPosition = (Integer) getObsProperty("y_ballPosition").getValue();
		int x_ballPosition = (Integer) getObsProperty("x_ballPosition").getValue();
		int x_position;
		int y_increment;
		int y_new=0;
		
		logger.info("player " + team + " iniciou defesa");

		if(y_ballPosition>y_defender){
			if(y_ballPosition-y_defender>3){
				y_new = y_defender + 3;
			} else {
				y_new = y_defender + (y_ballPosition-y_defender);
			}
			
		} else if(y_ballPosition<y_defender) {
			if(y_defender-y_ballPosition>3){
				y_new = y_defender - 3;
			} else {
				y_new = y_defender - (y_defender-y_ballPosition);
			}
			
		} else {
			y_new = y_defender;
		}
		
		signal("updatePositionY", team, y_new);
		
		incrementScore(y_new);	
	}
}