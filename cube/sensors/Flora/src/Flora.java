
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.Timer;

public class Flora
{
	public static void main(String args[])
	{
		// USER INPUT
		int gyro_Address = 0x6B;
		int accl_mag_Address = 0x1D;
		int sleep = 100;

		// Initialize GYRO + ACCL + MAG
		int[] gyro = new int[3];
		int[] accl = new int[3];
		int[] mag = new int[3];

		try
		{
			// Create I2CBus
			I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			// Get I2C device, LSM9DSO GYRO I2C address is 0x6B(107)
			I2CDevice device_gyro = bus.getDevice(gyro_Address);
			// Get I2C device, LSM9DSO ACCELERO MAGNETO I2C address is 0x1D(29)
			I2CDevice device_acc_mag = bus.getDevice(accl_mag_Address);

			// GYRO + ACCL + MAG
			if(gyro_Init(device_gyro, sleep) && accl_mag_Init(device_acc_mag, sleep))
			{
				while(true){
					print( gyroConvert(gyroRead(device_gyro)), acclConvert(acclRead(device_acc_mag)), magConvert(magRead(device_acc_mag)) );
					Thread.sleep(60);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("trouble in paradise: " + e);
		}
	}

	public static boolean gyro_Init(I2CDevice device_gyro, int sleep)
	{
		try
		{
			// Select control register1
			// X, Y and Z axis enabled, power on mode, data rate o/p 95 Hz
			device_gyro.write(0x20, (byte)0x0F);
			// Select control register4
			// Full scale 2000 dps, continuous update
			device_gyro.write(0x23, (byte)0x30);
			Thread.sleep(sleep);
		}
		catch(Exception e)
		{
			System.out.println("FAILED at gyro_Init: " + e);
			return false;
		}
		return true;
	}

	public static byte[] gyroRead(I2CDevice device_gyro)
	{
		// Read 6 bytes of data
		// xGyro lsb, xGyro msb, yGyro lsb, yGyro msb, zGyro lsb, zGyro msb
		byte[] data = new byte[6];
		try
		{
			data[0] = (byte)device_gyro.read(0x28);
			data[1] = (byte)device_gyro.read(0x29);
			data[2] = (byte)device_gyro.read(0x2A);
			data[3] = (byte)device_gyro.read(0x2B);
			data[4] = (byte)device_gyro.read(0x2C);
			data[5] = (byte)device_gyro.read(0x2D);
		}
		catch(IOException e)
		{
			System.out.println("FAILED at gyroRead: " + e );
			return null;
		}
		return data;
	}

	public static int[] gyroConvert(byte[] data)
	{
		// Convert the data
		int[] returnData = new int[3];

		int xGyro = ((data[1] & 0xFF) * 256 + (data[0] & 0xFF)) ;
		if(xGyro > 32767)
		{
			xGyro -= 65536;
		}
		returnData[0] = xGyro;

		int yGyro = ((data[3] & 0xFF) * 256 + (data[2] & 0xFF)) ;
		if(yGyro > 32767)
		{
			yGyro -= 65536;
		}
		returnData[1] = yGyro;

		int zGyro = ((data[5] & 0xFF) * 256 + (data[4] & 0xFF)) ;
		if(zGyro > 32767)
		{
			zGyro -= 65536;
		}
		returnData[2] = zGyro;

		return returnData;
	}

	public static boolean accl_mag_Init(I2CDevice device_acc_mag, int sleep)
	{
		try
		{
			// Select control register1
			// X, Y and Z axis enabled, power on mode, accelero data rate o/p 100 Hz
			device_acc_mag.write(0x20, (byte)0x67);
			// Select control register2
			// Full scale selection, +/- 16g
			device_acc_mag.write(0x21, (byte)0x20);
			// Select control register5
			// Magnetic high resolution, o/p data rate 50 Hz
			device_acc_mag.write(0x24, (byte)0x70);
			// Select control register6
			// Magnetic full scale selection, +/- 12 gauss
			device_acc_mag.write(0x25, (byte)0x60);
			// Select control register7
			// Normal mode, magnetic continuous conversion mode
			device_acc_mag.write(0x26, (byte)0x00);
			Thread.sleep(sleep);
		}catch(Exception e){
			System.out.println("FAILED at accl_mag_Init: " + e);
			return false;
		}
		return true;
	}

	public static byte[] acclRead(I2CDevice device_acc_mag)
	{
		// Read 6 bytes of data
		// xAccl lsb, xAccl msb, yAccl lsb, yAccl msb, zAccl lsb, zAccl msb
		byte[] data = new byte[6];

		try
		{
			data[0] = (byte)device_acc_mag.read(0x28);
			data[1] = (byte)device_acc_mag.read(0x29);
			data[2] = (byte)device_acc_mag.read(0x2A);
			data[3] = (byte)device_acc_mag.read(0x2B);
			data[4] = (byte)device_acc_mag.read(0x2C);
			data[5] = (byte)device_acc_mag.read(0x2D);
		}
		catch(Exception e)
		{
			System.out.println("FAILED at acclRead: " + e);
			return null;
		}
		return data;
	}

	public static int[] acclConvert(byte[] data)
	{
		// Convert the data
		int[] returnData = new int[3];
		int xAccl = ((data[1] & 0xFF) * 256 + (data[0] & 0xFF)) ;
		if(xAccl > 32767)
		{
			xAccl -= 65536;
		}
		returnData[0] = xAccl;

		int yAccl = ((data[3] & 0xFF) * 256 + (data[2] & 0xFF)) ;
		if(yAccl > 32767)
		{
			yAccl -= 65536;
		}
		returnData[1] = yAccl;

		int zAccl = ((data[5] & 0xFF) * 256 + (data[4] & 0xFF)) ;
		if(zAccl > 32767)
		{
			zAccl -= 65536;
		}
		returnData[2] = zAccl;

		return returnData;
	}

	public static byte[] magRead(I2CDevice device_acc_mag)
	{
		// Read 6 bytes of data
		// xMag lsb, xMag msb, yMag lsb, yMag msb, zMag lsb, zMag msb
		byte[] data = new byte[6];

		try
		{
			data[0] = (byte)device_acc_mag.read(0x08);
			data[1] = (byte)device_acc_mag.read(0x09);
			data[2] = (byte)device_acc_mag.read(0x0A);
			data[3] = (byte)device_acc_mag.read(0x0B);
			data[4] = (byte)device_acc_mag.read(0x0C);
			data[5] = (byte)device_acc_mag.read(0x0D);
		}
		catch(Exception e)
		{
			System.out.println("FAILED at magRead: " + e);
			return null;
		}
		return data;
	}

	public static int[] magConvert(byte[] data)
	{
		int[] returnData = new int[3];
		int xMag = ((data[1] & 0xFF) * 256 + (data[0] & 0xFF));
		if(xMag > 32767)
		{
			xMag -= 65536;
		}
		returnData[0] = xMag;

		int yMag = ((data[3] & 0xFF) * 256 + (data[2] & 0xFF)) ;
		if(yMag > 32767)
		{
			yMag -= 65536;
		}
		returnData[1] = yMag;

		int zMag = ((data[5] & 0xFF) * 256 + (data[4] & 0xFF)) ;
		if(zMag > 32767)
		{
			zMag -= 65536;
		}
		returnData[2] = zMag;

		return returnData;
	}

	// Output data to screen
	public static void print(int[] gyro, int[] accl, int[] mag)
	{
		System.out.printf("X-axis Of Rotation : %d %n", gyro[0]);
		System.out.printf("Y-axis Of Rotation : %d %n", gyro[1]);
		System.out.printf("Z-axis Of Rotation : %d %n", gyro[2]);
		System.out.printf("Acceleration in X-Axis : %d %n", accl[0]);
		System.out.printf("Acceleration in Y-Axis : %d %n", accl[1]);
		System.out.printf("Acceleration in Z-Axis : %d %n", accl[2]);
		System.out.printf("Magnetic field in X-Axis : %d %n", mag[0]);
		System.out.printf("Magnetic field in Y-Axis : %d %n", mag[1]);
		System.out.printf("Magnetic field in Z-Axis : %d %n", mag[2]);
	}
}
