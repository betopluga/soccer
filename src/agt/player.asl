// Agent goalkeeper in project soccer

/* Initial beliefs and rules */

/* Initial goals */

!scoreMoreGoals.

/* Plans */

+!scoreMoreGoals 
	: name(X) & startSoccer(ST) & positionY(PY) & positionX(PX)
	<- 
	if(ST==true){
		kick(X, PY, PX);		
	}
	.

+defend(TEAM)
	: name(N) & positionY(Y) & positionX(X) 
	<- 
	if(N==TEAM){
		defend(TEAM, Y);		
		kick(N, Y, X);
	}
	else{
		.print("CHUTEI a bola na posicao (", X,",",Y,")");
	}
	.
	
+updatePositionY(TEAM, NEWY)
	: name(N) & positionY(Y) & positionX(X) 
	<-
	if(N==TEAM){	
		-+positionY(NEWY);
		.print("DEFENDI a bola na posicao (", X,",",NEWY,")");			
	}
	.
	
+getPositionX : true <- positionX.
+getPositionY : true <- positionY.


{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have a agent that always complies with its organization  
//{ include("$jacamoJar/templates/org-obedient.asl") }
