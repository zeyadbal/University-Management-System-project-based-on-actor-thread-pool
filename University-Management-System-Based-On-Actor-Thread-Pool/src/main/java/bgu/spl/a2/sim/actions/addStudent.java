package bgu.spl.a2.sim.actions;

import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class addStudent extends Action<Boolean> {

	private String studentName;


	public addStudent(String studentName){
		super.actionName="addStudent";
		this.studentName=studentName;
		this.promise=new Promise<Boolean>();
	}

	@Override
	protected void start(){

			StudentPrivateState studentPVST=new StudentPrivateState();
			LinkedList<Action<Boolean>> actions= new LinkedList<Action<Boolean>>();
			creationAcception creationAcception= new creationAcception();
			actions.add(creationAcception);
			sendMessage(creationAcception, studentName, studentPVST);
			then(actions, ()->{
				((DepartmentPrivateState)actorState).getStudentList().add(studentName);
				complete(true);
			});
	}

}

