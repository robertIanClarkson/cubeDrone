// Distributed with a free-will license.
// Use it any way you want, profit or free, provided it fits in the licenses of its associated works.
// LSM9DS0
// This code is designed to work with the LSM9DS0_I2CS I2C Mini Module available from ControlEverything.com.
// https://www.controleverything.com/content/Accelorometer?sku=LSM9DS0_I2CS#tabs-0-product_tabset-2

#include <stdio.h>
#include <stdlib.h>
#include <linux/i2c-dev.h>
#include <sys/ioctl.h>
#include <fcntl.h>

void main()
{
    // Create I2C bus
    int file;
    char *bus = "/dev/i2c-1";
    if((file = open(bus, O_RDWR)) < 0)
    {
        printf("Failed to open the bus. \n");
        exit(1);
    }
    // Get I2C device, LSM9DSO GYRO I2C address is 0x6B(106)
    ioctl(file, I2C_SLAVE, 0x6B);

    // Select control register1(0x20)
    // X, Y and Z Axis enable, power on mode, data rate o/p 95 Hz(0x0F)
    char config[2] = {0};
    config[0] = 0x20;
    config[1] = 0x0F;
    write(file, config, 2);

    // Select control register4(0x23)
    // Full scale 2000 dps, continuous update(0x30)
    config[0] = 0x23;
    config[1] = 0x30;
    write(file, config, 2);
    sleep(1);

    // Read 6 bytes of data
    // lsb first
    // Read xGyro lsb data from register(0x28)
    char reg[1] = {0x28};
    write(file, reg, 1);
    char data[1] = {0};
    if(read(file, data, 1) != 1)
    {
        printf("Erorr : Input/output Erorr \n");
        exit(1);
    }
    char data_0 = data[0];

    // Read xGyro msb data from register(0x29)
    reg[0] = 0x29;
    write(file, reg, 1);
    read(file, data, 1);
    char data_1 = data[0];

    // Read yGyro lsb data from register(0x2A)
    reg[0] = 0x2A;
    write(file, reg, 1);
    read(file, data, 1);
    char data_2 = data[0];

    // Read yGyro msb data from register(0x2B)
    reg[0] = 0x2B;
    write(file, reg, 1);
    read(file, data, 1);
    char data_3 = data[0];

    // Read zGyro lsb data from register(0x2C)
    reg[0] = 0x2C;
    write(file, reg, 1);
    read(file, data, 1);
    char data_4 = data[0];

    // Read zGyro msb data from register(0x2D)
    reg[0] = 0x2D;
    write(file, reg, 1);
    read(file, data, 1);
    char data_5 = data[0];

    // Convert the data
    int xGyro = (data_1 * 256 + data_0);
    if(xGyro > 32767)
    {
        xGyro -= 65536;
    }

    int yGyro = (data_3 * 256 + data_2);
    if(yGyro > 32767)
    {
        yGyro -= 65536;
    }

    int zGyro = (data_5 * 256 + data_4);
    if(zGyro > 32767)
    {
        zGyro -= 65536;
    }

    // Get I2C device, LSM9DSO Accelero Magneto I2C address is 0x1D(30)
    ioctl(file, I2C_SLAVE, 0x1D);

    // Select control register1(0x20)
    // X, Y and Z Axis enable, power on mode, data rate o/p 100 Hz(0x67)
    config[0] = 0x20;
    config[1] = 0x67;
    write(file, config, 2);

    // Select control register2(0x21)
    // Full scale selection, +/- 16g(0x20)
    config[0] = 0x21;
    config[1] = 0x20;
    write(file, config, 2);

    // Select control register5(0x24)
    // Magnetic high resolution, o/p data rate 50 Hz(0x70)
    config[0] = 0x24;
    config[1] = 0x70;
    write(file, config, 2);

    // Select control register6(0x25)
    // Magnetic full scale selection, +/- 12 gauss(0x60)
    config[0] = 0x25;
    config[1] = 0x60;
    write(file, config, 2);

    // Select control register7(0x26)
    // Normal mode, magnetic continuous conversion mode(0x00)
    config[0] = 0x26;
    config[1] = 0x00;
    write(file, config, 2);
    sleep(1);

    // Read 6 bytes of data
    // lsb first
    // Read xAccl lsb data from register(0x28)
    reg[0] = 0x28;
    write(file, reg, 1);
    if(read(file, data, 1) != 1)
    {
        printf("Erorr : Input/output Erorr \n");
        exit(1);
    }
    data_0 = data[0];

    // Read xAccl msb data from register(0x29)
    reg[0] = 0x29;
    write(file, reg, 1);
    read(file, data, 1);
    data_1 = data[0];

    // Read yAccl lsb data from register(0x2A)
    reg[0] = 0x2A;
    write(file, reg, 1);
    read(file, data, 1);
    data_2 = data[0];

    // Read yAccl msb data from register(0x2B)
    reg[0] = 0x2B;
    write(file, reg, 1);
    read(file, data, 1);
    data_3 = data[0];

    // Read zAccl lsb data from register(0x2C)
    reg[0] = 0x2C;
    write(file, reg, 1);
    read(file, data, 1);
    data_4 = data[0];

    // Read zAccl msb data from register(0x2D)
    reg[0] = 0x2D;
    write(file, reg, 1);
    read(file, data, 1);
    data_5 = data[0];

    // Convert the data
    int xAccl = (data_1 * 256 + data_0);
    if(xAccl > 32767)
    {
        xAccl -= 65536;
    }

    int yAccl = (data_3 * 256 + data_2);
    if(yAccl > 32767)
    {
        yAccl -= 65536;
    }

    int zAccl = (data_5 * 256 + data_4);
    if(zAccl > 32767)
    {
        zAccl -= 65536;
    }

    // Read 6 bytes of data
    // lsb first
    // Read xMag lsb data from register(0x08)
    reg[0] = 0x08;
    write(file, reg, 1);
    if(read(file, data, 1) != 1)
    {
        printf("Erorr : Input/output Erorr \n");
        exit(1);
    }
    data_0 = data[0];

    // Read xMag msb data from register(0x09)
    reg[0] = 0x09;
    write(file, reg, 1);
    read(file, data, 1);
    data_1 = data[0];

    // Read yMag lsb data from register(0x0A)
    reg[0] = 0x0A;
    write(file, reg, 1);
    read(file, data, 1);
    data_2 = data[0];

    // Read yMag msb data from register(0x0B)
    reg[0] = 0x0B;
    write(file, reg, 1);
    read(file, data, 1);
    data_3 = data[0];

    // Read zMag lsb data from register(0x0C)
    reg[0] = 0x0C;
    write(file, reg, 1);
    read(file, data, 1);
    data_4 = data[0];

    // Read zMag msb data from register(0x0D)
    reg[0] = 0x0D;
    write(file, reg, 1);
    read(file, data, 1);
    data_5 = data[0];

    // Convert the data
    int xMag = (data_1 * 256 + data_0);
    if(xMag > 32767)
    {
        xMag -= 65536;
    }

    int yMag = (data_3 * 256 + data_2);
    if(yMag > 32767)
    {
        yMag -= 65536;
    }

    int zMag = (data_5 * 256 + data_4);
    if(zMag > 32767)
    {
        zMag -= 65536;
    }

    // Output data to screen
    printf("Rotation in X-axis : %d \n", xGyro);
    printf("Rotation in Y-axis : %d \n", yGyro);
    printf("Rotation in Z-axis : %d \n", zGyro);
    printf("Acceleration in X-axis : %d \n", xAccl);
    printf("Acceleration in Y-axis : %d \n", yAccl);
    printf("Acceleration in Z-axis : %d \n", zAccl);
    printf("Magnetic field in X-axis : %d \n", xMag);
    printf("Magnetic field in Y-axis : %d \n", yMag);
    printf("Magnetic field in Z-axis : %d \n", zMag);
}
