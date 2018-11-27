package bgu.spl.a2.sim.actions;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

public class openANewCourse extends Action<Boolean> {

	private String courseName;
	private Integer avaiableSpace;
	private List<String> prequisites;


	public openANewCourse(String courseName, Integer avaiableSpace, List<String> prequisites){
		super.actionName="openANewCourse";
		this.courseName=courseName;
		this.avaiableSpace=avaiableSpace;
		this.prequisites=prequisites;
		this.promise=new Promise<Boolean>();
	}

	@Override
	protected void start(){

		CoursePrivateState coursePVST=new CoursePrivateState();
		coursePVST.setAvailableSpots(avaiableSpace);
		coursePVST.setPrequisites(prequisites);
		LinkedList<Action<Boolean>> actions= new LinkedList<Action<Boolean>>();
		creationAcception creationAcception= new creationAcception();
		actions.add(creationAcception);
		sendMessage(creationAcception, courseName, coursePVST);
		then(actions, ()->{
			((DepartmentPrivateState)actorState).getCourseList().add(courseName);
			complete(true);
		});
	}
	
}