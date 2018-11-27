package bgu.spl.a2.sim.actions;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;

public class setSigForStudent extends Action<Boolean> {
	long signature;
	public setSigForStudent(long signature) {
		super.actionName="setSigForStudent";
		this.signature=signature;
		this.promise= new Promise<Boolean>();
	} 
	
	@Override
	protected void start() {
		((StudentPrivateState)actorState).setSignature(signature);
		complete(true);
	}
}
