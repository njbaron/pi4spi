package com.devknight.pi4spi;

import com.devknight.pi4spi.MCP3008.mcp3008;

import java.util.ArrayList;

public class executable {

    public static void main(String args[]) {
        System.out.println("Welcome to JavaSPI");
        mcp3008 adc = new mcp3008(18,23,24,25);


        try {
            while (true) {
                ArrayList<Double> voltages = new ArrayList<Double>();
                for (int i = 0; i < 8; i++) {
                    voltages.add(adc.getVoltage(i));
                }
                String s = voltages.toString();
                System.out.println(s);

            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
