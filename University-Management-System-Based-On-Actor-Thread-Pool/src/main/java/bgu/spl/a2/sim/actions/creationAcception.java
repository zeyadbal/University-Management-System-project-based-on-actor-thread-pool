package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;

public class creationAcception extends Action<Boolean>{

	public creationAcception() {
		super.actionName="creationAcception";
		this.promise=new Promise<Boolean>();
	}

	@Override
	protected void start() {
		complete(true);		
	}


}
