package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;

public class openningNewPlacesInACourse extends Action<Boolean>{
	private Integer numOfNewPlaces;

	public openningNewPlacesInACourse(Integer numOfNewPlaces) {
		super.actionName="openningNewPlacesInACourse";
		this.numOfNewPlaces=numOfNewPlaces;
		this.promise= new Promise<Boolean>();
	}

	@Override
	protected void start(){
		
		if(((CoursePrivateState)actorState).getAvailableSpots()!=-1){
			((CoursePrivateState)actorState).upDateAvailableSpots(numOfNewPlaces);
			complete(true);
		}
		else{
			complete(false);
		}

	}
}
