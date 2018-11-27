package bgu.spl.a2.sim.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class tryToRegisterCourse extends Action<Boolean> {
	
	private String studentName;
	private Integer grade;
	List<String>studGrades;
	public tryToRegisterCourse(String studentName, Object grade, List<String>studGrades){
		super.actionName="participatingInCourse";
		this.studGrades=studGrades;
		this.studentName=studentName;
		if(grade instanceof Integer){
			this.grade=(Integer)grade;
		}
		else this.grade=-1;
		this.promise=new Promise<Boolean>();
	}
	@Override
	protected void start() {
		

		if(((CoursePrivateState)actorState).getAvailableSpots()<1){
			complete(false);
			return;
		}
		if(((CoursePrivateState)actorState).getRegStudents().contains(studentName)){
			complete(false);
			return;
		}

		boolean accept=studGrades.size()>=((CoursePrivateState)actorState).getPrequisites().size();
		for(int i=0;accept && i<((CoursePrivateState)actorState).getPrequisites().size(); i++){
			if(!studGrades.contains(((CoursePrivateState)actorState).getPrequisites().get(i))){
				accept=false;
				break;
			}
		}
		if(accept){
			if(((CoursePrivateState)actorState).registerStudent(studentName)){
				((CoursePrivateState)actorState).decreaseAvailableSports();
				((CoursePrivateState)actorState).increaseRegStudents();			
				complete(true);
				return;
			}	
			else{		
				complete(false);
				return;
			}
			
		}
		else{
		complete(false);
		
		return;
		}
	}
}
