package com.emme.palmarebdibimbi;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class BluetoothConnectionInsecureExample {

    String tipo;
    String bluetooth;
    String przV, przP, desc, codArt, alias;
    int qta;

    public BluetoothConnectionInsecureExample(String tipo, String bluetooth, String przV, String przP, String desc, String codArt, String alias, int qta){
        this.tipo = tipo;
        this.bluetooth = bluetooth;
        this.przV = przV;
        this.przP = przP;
        this.desc = desc;
        this.codArt = codArt;
        this.alias = alias;
        this.qta = qta;
    }

    public void main() {

        BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(tipo, bluetooth, przV, przP, desc, codArt, alias, qta);

        // The Bluetooth MAC address can be discovered, scanned, or typed in

        String theBtMacAddress = "00:0A:3A:33:1B:5C";

        example.sendZplOverBluetooth(bluetooth);

    }


    private void sendZplOverBluetooth(final String theBtMacAddress) {

        new Thread(new Runnable() {

            public void run() {

                try {
                    // Instantiate insecure connection for given Bluetooth MAC Address.

                    Connection connection = new BluetoothConnection(theBtMacAddress);
                    //Connection connection = new TcpConnection("192.168.1.15",1601);

                    // Verify the printer is ready to print

                    //if (isPrinterReady(connection)) {


                        // Initialize

                        //Looper.prepare();


                        // Open the connection - physical connection is established here.

                        connection.open();

                        String desc1, desc2;
                        if(desc.length() > 28){
                            desc1 = desc.substring(0,28);
                            desc2 = desc.substring(28);
                        }else{
                            desc1 = desc;
                            desc2 = "";
                        }


                        // This example prints "This is a ZPL test." near the top of the label.
                        przV = przV.replace(".", ",");
                        przP = przP.replace(".", ",");

                        String cpclData = "";
                        switch(tipo){
                            case "Etichetta piccola":
                                cpclData = "!LABEL " +
                                        "\n" + "\n" +
                                        "! 0 0 0 275 1" + "CENTER" +
                                        "\n" +
                                        "TEXT 4 0 0 0 " + przV +
                                        "\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                        "TEXT 7 0 5 55 " + desc1 +
                                        "\n" + "\n" +
                                        "TEXT 7 0 5 80 " + desc2 +
                                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                        "TEXT 7 0 5 110 " + codArt +
                                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                        "B 128 1 0 50 5 140 " + alias +
                                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                        "TEXT 5 0 5 195 " + alias +
                                        "\n" + "\n" + "PRINT" + "\n" + "\n";
                                break;
                            case "Frontalino":
                                cpclData = "! 100 0 0 720 1" +
                                        "\n" +
                                        "T270 4 1 580 170 " + przV +
                                        "\n" + "\n" + "\n" + "\n" +
                                        "T270 0 3 100 40 " + desc +
                                        "\n" + "\n" +
                                        "T270 0 2 70 70 " + codArt +
                                        "\n" + "\n" +
                                        alias +
                                        "\n" +
                                        "VBARCODE 128 1 1 40 5 350  " + alias +
                                        "\n" + "\n" +
                                        "T270 5 0 0 150 " + alias +
                                        "\n" + "\n" + "PRINT" + "\n" + "\n";
                                break;
                            case "Etichetta promo":
                                cpclData = "! 0 0 0 285 1" +
                                        "TEXT 5 0 40 25 " + przV +
                                        "\n" + "LINE 8 8 150 8 1 " + "\n" +
                                        "TEXT 4 0 170 25 " + przP +
                                        "\n" + "\n" + "\n" + "\n" +
                                        "TEXT 5 0 40 55 Sc% " + "20%" +
                                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                        "TEXT 7 0 5 90 " + desc1 +
                                        "\n" + "\n" +
                                        "TEXT 7 0 5 110 " + desc2 +
                                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                        "TEXT 5 0 5 145 " + codArt +
                                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                        "TEXT 5 0 5 175 " + alias +
                                        "\n" + "\n" + "PRINT" + "\n" + "\n";
                                break;
                            case "Frontalino promo":
                                cpclData = "! 80 0 0 720 1" +
                                        "\n" +
                                        "T270 4 1 240 150  OFFERTA " +
                                        "\n" + "\n" + "\n" + "\n" +
                                        "T270 0 3 150 40 " + desc1 +
                                        "\n" + "\n" +
                                        "T270 0 3 130 40 " +
                                        desc2 +
                                        "\n" + "\n" +
                                        "T270 0 5 85 10 " +
                                        "\n" + "\n" +
                                        "T270 0 5 85 40 " + przV +
                                        "\n" + "\n" +
                                        "LINE 70 5 70 150  1 " +
                                        "\n" + "\n" +
                                        "T270 4 1 120 260 " + przP +
                                        "\n" + "\n" +
                                        "T270 0 2 0 70 " + codArt +
                                        "\n" + "\n" +
                                        "T270 5 0 20 150 " + alias +
                                        "\n" + "\n" +
                                        "PRINT" +
                                        "\n" + "\n";
                                break;
                            default:
                                break;
                        }

                        for(int i=0; i<qta; i++){
                            connection.write(cpclData.getBytes());
                        }

                        // Make sure the data got to the printer before closing the connection


                        // Close the insecure connection to release resources.

                        connection.close();

                        //Looper.myLooper().quit();

                    //}

                } catch (Exception e) {

                    // Handle communications error here.

                    e.printStackTrace();

                }

            }

        }).start();
    }

        private boolean isPrinterReady(Connection connection){

            boolean isOK = false;

            try {

                connection.open();

                // Creates a ZebraPrinter object to use Zebra specific functionality like getCurrentStatus()

                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);


                PrinterStatus printerStatus = printer.getCurrentStatus();

                if (printerStatus.isReadyToPrint) {

                    isOK = true;

                } else if (printerStatus.isPaused) {

                    System.out.println("Cannot Print because the printer is paused.");

                } else if (printerStatus.isHeadOpen) {

                    System.out.println("Cannot Print because the printer media door is open.");

                } else if (printerStatus.isPaperOut) {

                    System.out.println("Cannot Print because the paper is out.");

                } else {

                    System.out.println("Cannot Print.");

                }

            } catch (ConnectionException e) {

                e.printStackTrace();

            } catch (ZebraPrinterLanguageUnknownException e) {

                e.printStackTrace();

            }

            return isOK;

        }

    }
