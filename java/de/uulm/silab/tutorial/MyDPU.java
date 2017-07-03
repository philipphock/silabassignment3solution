package de.uulm.silab.tutorial;
import java.awt.BorderLayout;
import java.awt.Font;
import de.wivw.silab.sys.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.util.Random;
import de.wivw.silab.sys.JPU;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Java class 'MyDPU'.<br>
  *  A test DPU
  * <br>
  * Created: 02.09.2016 (SILABDPUWizard).<br>
  * @author Philipp Hock
  * <p>
  * A class that can be loaded as a DPU (using the DPUJava) into SILAB.
  * The {@link #trigger} method will be called
  * periodically while the simulation is running.<br>
  * More callbacks ({@link #prepare}, {@link #start}, {@link #stop} and {@link #release}) are available
  * that are called at respective times during the simulation's lifetime.<p>
  * Communication with other SILAB DPUs is most easily implemented by annotating
  * the fields using the "VarIn", "VarOut" and "VarIO" annotations.
  */
class MyDPU extends JPU
{
	/** The JPU's constructor.
	  * Every derived JPU class must implement a constructor
	  * that takes one long argument and pass that argument to
	  * the superclass (JPU) constructor.<br>
	  * Within the constructor, the varXXX() methods may be used
	  * to create additional SILAB variables.
	  */
	
	
	private int rand;
	private Random generator = new Random(); 
	private double pos;
	private double tmp;
	private String fileName = "C:/SILAB/output.txt";
	private int LaneSBtmp = 2  ;// tmp used for the lane where car should be
	private int LaneSB = 2 ;
	private  byte[] receiveData = new byte[1024];
	
	

	@VarIn(def=0) private double v;

	private BufferedWriter out;

	public MyDPU(long peer)
	{
		super(peer);

	}
	
	public void LCT( )
	{
		pos = (VDynX % 200);
		if (VDynX - tmp > 5){
		if (pos > 49.0 && pos < 51.0 )
		{
                  tmp = VDynX; 
               
			if (LaneIndex == 2 ) // right lane 
			{
				rand =  generator.nextInt(100);
				if (rand < 50)
				{
					
					visibleL = true;
					visibleC = false;
					visibleR = false;
					PosRightX = VDynX +150;
					PosRightY = 6;
					LaneSBtmp = 1;
				}
				if (rand >= 50)
				{
					
					visibleL = false;
					visibleC = true;
					visibleR = false;
					PosRightX = VDynX +150;
					PosRightY = 6;
					LaneSBtmp = 2;
				}	
				
					
			} 
			else if (LaneIndex == 1 ) // middle lane
			{	
				rand =  generator.nextInt(100);
				if (rand <34)
				{
					visibleL = true;
					visibleC = false;
					visibleR = false;
					PosRightX = VDynX +150;
					PosRightY = 6;
					LaneSBtmp= 0;
				}
				if (rand >33 && rand <68)
				{
					visibleL = false;
					visibleC = false;
					visibleR = true;
					
					PosRightX = VDynX +150;
					PosRightY = 6;
					LaneSBtmp = 2;
				}
				if (rand >67)
				{
					visibleL = false;
					visibleC = true;
					visibleR = false;
					PosRightX = VDynX +150;
					PosRightY = 6;
					LaneSBtmp = 1;
				}
				
			}
			else if (LaneIndex == 0 )// left lane
			{
				rand =  generator.nextInt(100);
				if (rand < 50)
				{
					
					visibleL = false;
					visibleC = false;
					visibleR = true;
					PosLeftX = VDynX +150;
					PosLeftY = -6;
					LaneSBtmp = 1;
				}
				if (rand >= 50)
				{
					
					visibleL = false;
					visibleC = true;
					visibleR = false;
					PosLeftX = VDynX +150;
					PosLeftY = -6;
					LaneSBtmp = 0;
				}	
				
			}
			
		
		}
				
	}
	 if ( pos < 2){
		LaneSB = LaneSBtmp;
		
	}
		
	}


	private DatagramSocket serverSocket;
	private boolean isRunning;
	private final ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<String>();


	/** Called periodically once the user clicks 'Launch'.<br>
	  * Task: Run preparations that have to be done whenever the
	  * simulation is re-started. The tasks must be subdivided into
	  * small steps (approx. 1 ms). The method will be called repeatedly
	  * as long as JPU_NOTREADY is returned.
	  * @param step Call counter (0 for the first call, 1 for the second, etc.)
	  * @return JPU_READY when the preparations are complete.<br>
	  *         JPU_NOTREADY when more time is needed. The method will be called again.<br>
	  *         JPU_ABORT when an error occurred. The simulation will be stopped.
	  */

	@Override public int start(int step)
	{
		isRunning = true;
		q.clear();
		try {
			serverSocket = new DatagramSocket(7777);
			Thread t = new Thread( new Runnable() {
				public void run() {
					while (isRunning){
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						try {
							serverSocket.receive(receivePacket);
						} catch (IOException e) {
							isRunning = false;
							SILAB.logErr("socket recv error");
						}
						String sentence =  new String(receivePacket.getData(),0,receivePacket.getLength());
						q.add(sentence);
					}

				}
			});
			t.start();
		} catch (SocketException e) {
			SILAB.logErr("socket error");

		}

		try {
			out = new BufferedWriter(new FileWriter("inventory.txt", true));
		} catch (IOException e) {
			SILAB.logErr("error buffered reader");
		}

		return JPU_READY;
	}

	private final StringBuilder recv = new StringBuilder();


	/** Called periodically while the simulation is running.<br>
	  * Task: Implement the JPU's functionality during the simulation.
	  * Each call shouldn't take more than 1 ms to complete.
	  */
	@Override public void trigger(double time, double timeError)
	{
		
		
		LCT();
		recv.setLength(0);

		while (!q.isEmpty()){
			recv.append(q.poll());
		}
		if (recv.length() != 0){
			SILAB.logSys(recv.toString());
		}



		try
		{
			// write the text string to the file
			out.write(String.valueOf(time));
			out.write(",");
			out.write(String.valueOf(LaneIndex));
			out.write(",");
			out.write(String.valueOf(LaneSB	));
			out.write(",");
			out.write(String.valueOf(VDynX));
			out.write(",");

			double latdist = 0;
			if (LaneIndex == 0)
			{
				latdist = Math.abs(-4 - VDynY);
			}
			else if ( LaneIndex == 1)
			{
				latdist = Math.abs(VDynY);
			}
			else if (LaneIndex == 2){
				latdist = Math.abs(4 - VDynY);
			}
			out.write(""+latdist);
			out.write(",");
			out.write(recv.toString());
			// write a `newline` to the file
			out.newLine();

		}

			// handle exceptions
		catch (IOException ioe)
		{
			SILAB.logErr("Error writing logs");
		}



	}

	/** Generic cleanup of the JPU.<br>
	  * The method will be called when the simulation is shut down,
	  * no matter if the shutdown is regular or using the 'emergency stop' (x)
	  * button.<br>
	  * <b>Warning:</b> release() may be called more than once.<br>
	  * <b>Warning:</b> release() may be called in any state of the simulation, because
	  * it is always possible to shut down the simulation.<br>
	  * Task: Release all resources held by the JPU.
	  */
	@Override public void release()
	{

	}

	
	/** Called periodically once the user clicks 'Stop', or when one
	  * DPU's preparations (during 'Launch') have failed.<br>
	  * Task: Cleanup things before the simulation is stopped. The tasks must be subdivided into
	  * small steps (approx. 1 ms). The method will be called repeatedly
	  * as long as JPU_NOTREADY is returned.
	  * @param step Call counter (0 for the first call, 1 for the second, etc.)
	  * @return JPU_READY when the cleanup is complete.<br>
	  *         JPU_NOTREADY when more time is needed. The method will be called again.<br>
	  *         JPU_ABORT when an error occurred. The simulation will be stopped.
	  */
	@Override public int stop(int step)
	{


		isRunning = false;
		serverSocket.close();
		try {
			out.close();
		} catch (IOException e) {
			SILAB.logErr("error closing file");
		}

		return JPU_READY;
	}
	/** A sample input variable. The default value is set to 0. */
	@VarIn(def=0) private double In;
		@VarIn(def=0) private double VDynX;
		@VarIn(def=0) private double VDynY;
        @VarIn(def=0) private double VDynS;
        @VarIn(def=0) private int LaneIndex;
	/** A sample output variable. The default value is set to 0. */
	@VarOut(def=0) private double Out;
		@VarOut(def=0) private double PosLeftX;
		@VarOut(def=0) private double PosLeftY;
		@VarOut(def=0) private double PosRightX;
		@VarOut(def=0) private double PosRightY;
		@VarOut(def=0) private boolean visibleL;
		@VarOut(def=0) private boolean visibleR;
		@VarOut(def=0) private boolean visibleC;
	
}
