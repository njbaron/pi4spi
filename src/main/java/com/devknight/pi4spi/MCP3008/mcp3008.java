package com.devknight.pi4spi.MCP3008;

import com.pi4j.io.gpio.*;

public class mcp3008 {

    private GpioPinDigitalOutput clockpin;
    private GpioPinDigitalOutput mosipin;
    private GpioPinDigital misopin;
    private GpioPinDigitalOutput cspin;

    private static final double maxGpioVoltage = 3.3;

    private double maxInputVoltage = 3.3;

    private double resistor1 = 10000;
    private double resistor2 = 10000;

    public mcp3008(int clockpin, int misopin, int mosipin, int cspin) {
        GpioController gpio = GpioFactory.getInstance();
        this.clockpin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(clockpin),PinState.LOW);
        this.mosipin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(mosipin), PinState.LOW);
        this.misopin = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(misopin));
        this.cspin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(cspin),PinState.LOW);
    }

    private int getBitValue(int pin) throws Exception{
        if(pin > 7 || pin < 0) {
            throw new IndexOutOfBoundsException("pin out of bounds");
        }
        cspin.high();
        clockpin.low();
        cspin.low();
        int commandout = pin;
        commandout |= 0x18;
        commandout = commandout << 3;
        for(int i = 0; i < 5; i++) {
            if((commandout & 0x80) == 1) {
                mosipin.high();
            }
            else {
                mosipin.low();
            }
            clockpin.high();
            clockpin.low();
        }
        int adcout = 0;
        for(int i = 0; i < 12; i++) {
            clockpin.high();
            clockpin.low();
            adcout = pin << 1;
            if (misopin.isHigh()) {
                adcout |= 0x1;
            }
        }
        cspin.high();
        adcout = adcout >> 1;
        return adcout;
    }

    private double voltageDivider(double r1, double r2, double vin) {
        return (vin * (r2 / (r1 + r2)));
    }

    public double getVoltage(int pin) throws Exception {
        //this is the effective max voltage, prior to the divider, that the ADC can register
        double conversionFactor = (maxGpioVoltage / voltageDivider(resistor1, resistor2, maxInputVoltage) * maxInputVoltage);
        //read the analog pins on the ACD (range 0-1023) and convert to 0.0-1.0
        double fracVoltage = getBitValue(pin) / 1023.0;
        //Calculate the true voltage
        double realVoltage = fracVoltage * conversionFactor;
        return realVoltage;
    }

    public void setMaxInputVoltage(double maxInputVoltage) {
        this.maxInputVoltage = maxInputVoltage;
    }

    public void setResistor1(double resistor1) {
        this.resistor1 = resistor1;
    }

    public void setResistor2(double resistor2) {
        this.resistor2 = resistor2;
    }
}
