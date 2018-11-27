package bgu.spl.a2.sim.actions;
import java.util.LinkedList;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

public class closeACourse extends Action<Boolean> {
	private String course;

	public closeACourse(String course) {
		super.actionName="closeACourse";
		this.course=course;
		this.promise= new Promise<Boolean>();
	}

	@Override
	protected void start(){

		LinkedList<Action<Boolean>> actions= new LinkedList<Action<Boolean>>();
		closeThisCourse closeThisCourse= new closeThisCourse();
		sendMessage(closeThisCourse, course, new CoursePrivateState());
		actions.add(closeThisCourse);

		then(actions, ()->{
			((DepartmentPrivateState)actorState).getCourseList().remove(course);
			complete(true);
		});

	}
}
