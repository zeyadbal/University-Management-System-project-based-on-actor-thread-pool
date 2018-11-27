package a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import bgu.spl.a2.VersionMonitor;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.Assert;

public class VersionMonitorTest {
	VersionMonitor vm;

	@Before
	public void setUp() throws Exception {
		VersionMonitor vm= new VersionMonitor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetVersion() {
		/*
		 * creating 10 threads that calls inc function , and expect the version value to be increased
		 * by the number of the inc function calls. i.e. {old version + 10}
		 */
		try{

			int oldVersion= vm.getVersion();

			for(int i=0; i<10; i++){
				Thread thread= new Thread( ()-> { vm.inc();  });
				thread.start();
			}

			try{
				Assert.fail();
			}
			catch(Exception ex){
				assertEquals(vm.getVersion(),oldVersion+10);
			}
		}
		catch(Exception ex){
			Assert.fail();
		}
	}

	@Test
	public void testInc() {
		/*
		 * creating two threads that calls inc function , and expect the version to be updated
		 * to { (old version)+2 }
		 */
		try{
			int oldVersion= vm.getVersion();

			Thread thread1= new Thread( ()-> { vm.inc();  });
			Thread thread2= new Thread( ()-> { vm.inc();  });

			/*
			 * the threads have not been started yet, so we didn't expect the version value to be changed.
			 */

			try{
				Assert.fail();
			}
			catch(Exception ex){
				assertEquals(vm.getVersion(),oldVersion);
			}

			/*
			 * now we are going to start the threads, since the mission of each one is to increase
			 * the version by 1, so we expect the version value to be equal to 2.
			 */

			try{
				thread1.start();
				thread2.start();
				Assert.fail();
			}
			catch(Exception ex){
				assertEquals(vm.getVersion(),oldVersion+2);
			}
		}
		catch(Exception ex){
			Assert.fail();
		}
	}


	@Test
	public void testAwait()  {
		/*
		 * creating two threads that calls await function within some version, and waits the version to be updated
		 * to continue there work.
		 * each thread mission is to increase the value of the effectively final variable x by 1.
		 */
		try{
			AtomicInteger x= new AtomicInteger(0);

			Thread thread1= new Thread( ()-> {
				vm.await(vm.getVersion());
				x.set(x.get()+1);
			});

			thread1.start();

			Thread thread2= new Thread( ()-> { 
				vm.await(vm.getVersion());
				x.set(x.get()+1);

			});

			thread2.start();
			/*
			 * since the threads should wait till the version gets updated,
			 * the current x value must be 0. (means both threads are waiting the version to be updated
			 * and do nothing till that moment)
			 */
			try{
				Assert.fail();
			}
			catch(Exception ex){
				assertEquals(x.get(),0);
			}
			/*
			 * now, we are going to update the version, (we are doing this by inc method),
			 * at this point, the two threads should wake and continue working at the point they have stopped.
			 * as we expect the x value to be increased twice.
			 */
			try{
				vm.inc();	
				Assert.fail();
			}
			catch(Exception ex){
				assertEquals(x.get(),2);
			}
		}
		catch(Exception ex){
			Assert.fail();;
		}
	}
}
