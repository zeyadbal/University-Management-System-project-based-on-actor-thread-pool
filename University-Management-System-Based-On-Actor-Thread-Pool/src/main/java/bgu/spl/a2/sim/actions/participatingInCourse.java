package bgu.spl.a2.sim.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

public class participatingInCourse extends Action<Boolean> {

	private String studentName;
	private Integer grade;

	public participatingInCourse(String studentName, Object grade){
		super.actionName="participatingInCourse";
		this.studentName=studentName;
		if(grade instanceof Integer){
			this.grade=(Integer)grade;
		}
		else this.grade=-1;
		this.promise=new Promise<Boolean>();
	}


	@Override
	protected void start(){

		
		LinkedList<Action<Boolean>> actions= new LinkedList<Action<Boolean>>();

		if(((CoursePrivateState)actorState).getAvailableSpots()<1){// && !super.doubled.equals(studentName)){
			if(super.tries!=0){
				super.tries--;
				sendMessage(this, actorId, new CoursePrivateState());
			}
			complete(false);
			return;
		}
		if(((CoursePrivateState)actorState).getRegStudents().contains(studentName)){
			complete(false);
			return;
		}
		((CoursePrivateState)actorState).decreaseAvailableSports();

		addCourseToAStudent addCourseToAStudent=new addCourseToAStudent(actorId,grade);
		actions.add(addCourseToAStudent);
		sendMessage(addCourseToAStudent, studentName , new StudentPrivateState());
		then(actions, ()-> {
			actions.clear();
			doesTheStudentHasThePrequisites action1=new doesTheStudentHasThePrequisites(((CoursePrivateState)actorState).getPrequisites());
			actions.add(action1);
			sendMessage(action1, studentName, new StudentPrivateState());
			then(actions,()->{
				boolean ans=actions.get(0).getResult().get()==true;
				int a=((CoursePrivateState)actorState).getAvailableSpots();
				if(((CoursePrivateState)actorState).getAvailableSpots()==-1 
						|| !ans ){
					actions.clear();
					removeCourseFromSheet action=new removeCourseFromSheet(actorId);
					actions.add(action);
					sendMessage(action, studentName, new StudentPrivateState());
					then(actions, ()-> {
						if(((CoursePrivateState)actorState).getAvailableSpots()>-1 && !ans){
							((CoursePrivateState)actorState).increaseAvailableSports();
						}
						complete(false);
						return;
					});
				}else{
					actions.clear();
					List<String> arr=new ArrayList<String>();
					arr.add(actorId);
					doesTheStudentHasThePrequisites action2=new doesTheStudentHasThePrequisites(arr);
					actions.add(action2);
					sendMessage(action2, studentName, new StudentPrivateState());
					then(actions,()->{
						if(actions.get(0).getResult().get()==false){
							complete(true);
							return;
						}
						else{
							
							if(((CoursePrivateState)actorState).registerStudent(studentName)){
								((CoursePrivateState)actorState).increaseRegStudents();
							}							
							complete(true);
							return;
						}
					});	
				}
			});
		});
	}
}
