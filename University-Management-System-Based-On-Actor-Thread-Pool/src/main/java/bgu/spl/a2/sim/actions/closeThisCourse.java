package bgu.spl.a2.sim.actions;

import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class closeThisCourse extends Action<Boolean> {

	public closeThisCourse() {
		super.actionName="closeThisCourse";
		this.promise= new Promise<Boolean>();
	}

	@Override
	protected void start(){
		
		((CoursePrivateState)actorState).setAvailableSpots(new Integer(-1));
		LinkedList<Action<Boolean>> actions= new LinkedList<Action<Boolean>>();
		for(int i=0; i<((CoursePrivateState)actorState).getRegStudents().size(); i++){
			removeCourseFromSheet action=new removeCourseFromSheet(actorId);
			actions.add(action);
			sendMessage(action,((CoursePrivateState)actorState).getRegStudents().get(i), new StudentPrivateState());
		}
		if(actions.isEmpty()){
			complete(true);
			return;
		}
		then(actions, ()-> {
			((CoursePrivateState)actorState).setRegistered(0);
			((CoursePrivateState)actorState).getRegStudents().clear();
			complete(true);
		});

	}
}
