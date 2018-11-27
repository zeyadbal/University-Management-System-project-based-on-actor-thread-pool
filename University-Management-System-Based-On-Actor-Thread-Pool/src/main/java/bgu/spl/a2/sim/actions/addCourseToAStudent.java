package bgu.spl.a2.sim.actions;



import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class addCourseToAStudent extends Action<Boolean>{
	private String course;
	private Integer grade;


	public addCourseToAStudent(String course, Integer grade){
		super.actionName="addCourseToAStudent";
		this.course=course;
		this.grade=grade;
		this.promise=new Promise<Boolean>();
	}

	@Override
	protected void start(){
	
		if(((StudentPrivateState)actorState).getGrades().containsKey(course)){
			complete(false);
		}
		else{
			((StudentPrivateState)actorState).getGrades().put(course, grade);
			complete(true);
		}
	}
}

