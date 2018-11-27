package bgu.spl.a2.sim.actions;
import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class removeCourseFromSheet extends Action<Boolean>{
	private String course;


	public removeCourseFromSheet(String course){
		super.actionName="removeCourseFromSheet";
		this.course=course;
		this.promise=new Promise<Boolean>();
	}


	@Override
	protected void start(){

		if(((StudentPrivateState)actorState).getGrades().containsKey(course)){
			((StudentPrivateState)actorState).getGrades().remove(course);
			complete(true);
		}
		else{
			complete(false);
		}
	}
}

