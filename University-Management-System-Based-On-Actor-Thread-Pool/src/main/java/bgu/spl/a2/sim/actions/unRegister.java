package bgu.spl.a2.sim.actions;

import java.util.ArrayList;
import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

public class unRegister extends Action<Boolean> {

	private String studentName;


	public unRegister(String studentName){
		super.actionName="unRegister";
		this.studentName=studentName;
		this.promise=new Promise<Boolean>();
	}

	@Override
	protected void start(){

		if(((CoursePrivateState)actorState).getAvailableSpots()==-1){
			complete(true);
			return;
		}
		LinkedList<Action<Boolean>> actions= new LinkedList<Action<Boolean>>();
		removeCourseFromSheet removeCourseFromSheet=new removeCourseFromSheet(actorId);
		actions.add(removeCourseFromSheet);
		sendMessage(removeCourseFromSheet, studentName, new StudentPrivateState());
		
		then(actions, ()-> {
				boolean accept=actions.get(0).getResult().get();
					if(accept){
			
					((CoursePrivateState)actorState).unRegisterStudent(studentName);
					
					((CoursePrivateState)actorState).decreaseRegStudents();
					if(((CoursePrivateState)actorState).getAvailableSpots()!=-1){
						((CoursePrivateState)actorState).increaseAvailableSports();
						}
					complete(true);
				}
					else{
						complete(true);
					}
			});
	}
}

