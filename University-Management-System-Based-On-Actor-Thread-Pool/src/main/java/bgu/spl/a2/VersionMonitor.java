package bgu.spl.a2;
/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor  {
	private volatile int version=0;

	public int getVersion() {
		return version;
	}

	public synchronized void inc() {
		/*
		 * in the general case, this method should be
		 * synchronized because the increment operation is 
		 * not atomic, and if n threads trying to increment 
		 * in the same moment, the version value will not be
		 *  determinestic.
		 * in our case,
		 * they may be more than one thread which may try to
		 *  call the method inc when submitting a new action
		 *   to the actors
		 */
		version++;
		this.notify();
	}


	public synchronized void await(int version) {
		/*
		 * if we don't synchronized here, there may be a case
		 * such that more than two threads enters this method and tries
		 * to wait at the same time.however, according to the description
		 * of the wait method, the calling thread must own the monitor
		 * and ONLY him.
		 */
		int vrs= this.getVersion();
		if(vrs!=this.getVersion()){
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
	}
}
