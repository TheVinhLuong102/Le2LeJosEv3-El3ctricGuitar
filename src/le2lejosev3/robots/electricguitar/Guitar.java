/**
 * 
 */
package le2lejosev3.robots.electricguitar;

import java.util.logging.Logger;

import le2lejosev3.logging.Setup;
import le2lejosev3.pblocks.Display;
import le2lejosev3.pblocks.InfraredSensor;
import le2lejosev3.pblocks.MediumMotor;
import le2lejosev3.pblocks.Sound;
import le2lejosev3.pblocks.TouchSensor;
import le2lejosev3.pblocks.Wait;
import lejos.hardware.Button;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

/**
 * El3ctric Guitar.
 * 
 * @author Roland Blochberger
 */
public class Guitar {

	private static Class<?> clazz = Guitar.class;
	private static final Logger log = Logger.getLogger(clazz.getName());

	/**
	 * Main program entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// setup logging to file
		Setup.log2File(clazz);
		log.fine("Starting ...");

		// Display 'LOGO' image file at coordinates 0,0 and clear screen before
		Display.image("LOGO.lni", true, 0, 0);

		// instantiate and initialize the variables
		Variables vars = new Variables();
		// initialize the array of note frequencies
		vars.notes = new int[] { 1318, 1174, 987, 880, 783, 659, 587, 493, 440, 392, 329, 293 };

		// instantiate the lever thread
		Thread leverThread = new LeverThread(vars);
		// start lever thread
		leverThread.start();

		// instantiate the guitar thread
		Thread guitarThread = new GuitarThread(vars);
		// start guitar thread
		guitarThread.start();

		log.fine("The End");
	}

}

/**
 * Variables holder class
 */
class Variables {

	// the robot configuration
	private static final Port motorPortD = MotorPort.D;
	private static final Port touchSensorPort = SensorPort.S1;
	private static final Port infraredSensorPort = SensorPort.S4;

	// the motor
	public static final MediumMotor motorD = new MediumMotor(motorPortD);
	// the sensors
	public static final TouchSensor touch = new TouchSensor(touchSensorPort);
	public static final InfraredSensor infrared = new InfraredSensor(infraredSensorPort);

	// the array of note frequencies
	public int[] notes = null;
	// the lever value
	private int _lever = 0;
	// the fret value
	public int fret = 0;
	// the raw value
	public float raw = 0;

	/**
	 * synchronized getter for the lever value.
	 * 
	 * @return the lever value.
	 */
	public synchronized int getLever() {
		return _lever;
	}

	/**
	 * synchronized setter for the lever value.
	 * 
	 * @param lever the lever value.
	 */
	public synchronized void setLever(int lever) {
		_lever = lever;
	}
}

/**
 * Guitar main thread.
 */
class GuitarThread extends Thread {

	private static final Logger log = Logger.getLogger(GuitarThread.class.getName());

	// variables
	final Variables vars;

	/**
	 * Constructor.
	 * 
	 * @param vars
	 */
	public GuitarThread(Variables vars) {
		this.vars = vars;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {


		// main loop
		float prox;
		int levr;
		String text;
		int lowerBound;
		int higherBound;
		int a;
		int hz;
		while (Button.ESCAPE.isUp()) {

			// clear fret
			vars.fret = 0;

			// measure proximity and store the average in raw
			prox = Variables.infrared.measureProximity();
			prox += Variables.infrared.measureProximity();
			prox += Variables.infrared.measureProximity();
			prox += Variables.infrared.measureProximity();
			vars.raw = (prox / 4F);

			// show lever value on LCD
			levr = vars.getLever();
			text = "b=" + levr + "  ";
			// Display text on grid at 17, 0 with black (false) and normal font (0) no clear
			// screen
			// on LeJOS use column 12 instead
			Display.textGrid(text, false, 12, 0, Display.COLOR_BLACK, Display.FONT_NORMAL);

			// loop through notes array
			for (int i = 0; i < vars.notes.length; i++) {
				// calculate the bounds
				lowerBound = 5 * i - 1;
				higherBound = 5 * (i + 1);
				// check raw value inside the bounds
				if ((vars.raw >= lowerBound) && (vars.raw <= higherBound)) {
					vars.fret = i;
				}
			}

			// show fret value on LCD
			text = "f=" + vars.fret + "  ";
			// Display text on grid at 17, 11 with black (false) and normal font (0) no
			// clear screen
			// on LeJOS use column 12 and line 6 instead
			Display.textGrid(text, false, 12, 6, Display.COLOR_BLACK, Display.FONT_NORMAL);

			// limit the fret value
			if (vars.fret > vars.notes.length) {
				vars.fret = vars.notes.length;
			}

			// check touch sensor state
			if (Variables.touch.compareState(TouchSensor.RELEASED)) {
				// touch sensor released:
				// calculate the tone frequency
				a = vars.notes[vars.fret];
				hz = a - 11 * levr;
				// play the tone
				log.fine("raw: " + vars.raw + ", levr: " + levr + ", fret: " + vars.fret + ", hz: " + hz);
				Sound.playTone(hz, 0.1F, 100, Sound.ONCE);

			} else {
				// touch sensor pressed:
				// stop sound
				Sound.stop();

			}
		}

		// Wait 1 second
		Wait.time(1F);
		log.fine("The End");
	}
}

/**
 * Lever Handler Thread
 */
class LeverThread extends Thread {

	// variables
	final Variables vars;

	/**
	 * Constructor
	 * 
	 * @param vars
	 */
	public LeverThread(Variables vars) {
		this.vars = vars;
		// let this thread stop when the main thread stops
		setDaemon(true);
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// set lever to zero
		vars.setLever(0);

		// medium motor D on for 1 second with power 5 and don't brake
		Variables.motorD.motorOnForSeconds(5, 1F, false);
		// medium motor D on for 30 degrees with power -5 and brake
		Variables.motorD.motorOnForDegrees(-5, 30, true);

		// Wait 0.1 seconds
		Wait.time(0.1F);
		// Reset motor D rotation
		Variables.motorD.rotationReset();

		// thread loop
		int degrees = 0;
		while (true) {

			// measure motor rotation degrees
			degrees = Variables.motorD.measureDegrees();

			// check degrees inside range of -4..+4
			if ((degrees >= -4) && (degrees <= 4)) {
				// reset lever value
				vars.setLever(0);
			} else {
				// set lever value
				vars.setLever(degrees);
			}
		}
	}

}
