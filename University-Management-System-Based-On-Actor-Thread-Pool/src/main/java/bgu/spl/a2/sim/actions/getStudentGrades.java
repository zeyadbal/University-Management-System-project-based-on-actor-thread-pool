package bgu.spl.a2.sim.actions;

import java.util.Map;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class getStudentGrades extends Action<Map<String, Integer>> {

	
	public getStudentGrades() {
		super.actionName="getStudentGrades";
		this.promise= new Promise<Map<String, Integer>>();
	} 
	@Override
	protected void start() {
		complete(((StudentPrivateState)actorState).getGrades());	
	}
}
