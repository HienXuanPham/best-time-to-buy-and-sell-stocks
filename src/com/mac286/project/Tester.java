package com.mac286.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Tester {
    public static void main(String[] args) {

        float[] test_factor = new float[]{0.5f, 1f, 2f, 5f, 10f};

        System.out.println("\n-------------------------STOCKS--------------------------");
        for (int j = 0; j < test_factor.length; j++) {
            //add tickets of stocks from Stocks.txt to a vector
            Vector<String> Stock_ticket = new Vector<String>(loadTicket(/* Path to Stocks.txt */));

            Vector<Trade> Stock_Trades = new Vector<Trade>(3000);

            for (int i = 0; i < Stock_ticket.size(); i++) {
                //create a symbol tester for every stock
                symbolTester tester = new symbolTester(Stock_ticket.elementAt(i), /* Path to Stocks.txt */, test_factor[j]);
                //test for 10 days / 20 days
                tester.test(20);
                Stock_Trades.addAll(tester.getTrades());
            }

            //Compute the stats
            System.out.println("\nRisk factor: " + test_factor[j]);
            Compute_Statistic(Stock_Trades);
        }

        System.out.println("\n-------------------------ETFs--------------------------");
        for (int j = 0; j < test_factor.length; j++) {

            //add tickets of ETF from ETF.txt to a vector
            Vector<String> ETF_ticket = new Vector<String>(loadTicket(/* Path to ETF.txt */));

            Vector<Trade> ETF_Trades = new Vector<Trade>(3000);

            for(int n = 0; n < ETF_ticket.size(); n++) {
                symbolTester ETF_tester = new symbolTester(ETF_ticket.elementAt(n), /* Path to ETF.txt */, test_factor[j]);
                //test for 10 days / 20 days
                ETF_tester.test(10);
                ETF_Trades.addAll(ETF_tester.getTrades());
            }

            //Compute the stats
            System.out.println("\nRisk factor: " + test_factor[j]);
            Compute_Statistic(ETF_Trades);
        }
    }

    public static Vector<String> loadTicket(String fileName) {
        Vector<String> ticket = new Vector<String>();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while((line = br.readLine()) != null) {
                ticket.add(line);
            }
            br.close();
            fr.close();
        }catch(IOException e) {
            System.out.println("Something is wrong: " + e.getMessage());
        }
        return ticket;
    }

    public static void Compute_Statistic(Vector<Trade> Trades) {
        int shortTrades = 0, longTrades = 0;
        float winners = 0, winnersLong = 0, winnersShort = 0;
        float profit = 0, profitLong = 0, profitShort = 0;
        float holdingPeriod = 0;

        for(int i= 0; i < Trades.size(); i++) {
            holdingPeriod = holdingPeriod + Trades.elementAt(i).getHoldingPeriod();

            //Counting the total amount of short/long trades, Trades.size() can be used for total
            if (Trades.elementAt(i).getDir() == Direction.SHORT)
                shortTrades++;
            else
                longTrades++;

            //counting the trades that secured a profit
            //if a long trade closes above the entry price, then it has profited
            if (Trades.elementAt(i).percentPL() > 0 && Trades.elementAt(i).getDir() == Direction.LONG) {
                winners++;
                winnersLong++;
            }
            //if a short trade closes below the entry price, then it has profited
            else if (Trades.elementAt(i).percentPL() > 0 && Trades.elementAt(i).getDir() == Direction.SHORT) {
                winners++;
                winnersShort++;
            }

            if (Trades.elementAt(i).getDir() == Direction.LONG) {
                //determining how much profit trades made
                //subtract entry from exit, in a profit situation it will be positive and in a loss it will be negative
                profit = profit + Trades.elementAt(i).percentPL();
                profitLong = profitLong + Trades.elementAt(i).percentPL();
            }
            //subtract exit from entry, positive if it won and negative if it lost
            else if (Trades.elementAt(i).getDir() == Direction.SHORT) {
                profit = profit + Trades.elementAt(i).percentPL();
                profitShort = profitShort + Trades.elementAt(i).percentPL();
            }

        }

        System.out.println("\nTotal Trades: " + Trades.size() + " Short Trades: " + shortTrades + " Long Trades: " + longTrades);
        System.out.println("Total percentage of winning trades: " + ((winners / Trades.size()) * 100) + "%");
        System.out.println("Total percentage of winning long trades: " + ((winnersLong / longTrades) * 100) + "%");
        System.out.println("Total percentage of winning short trades: " + ((winnersShort / shortTrades) * 100) + "%");
        System.out.println("Average holding period: " + (holdingPeriod / Trades.size()));
        System.out.println("\nAverage profit per trade: " + ((profit / Trades.size())) + "%");
        System.out.println("Average long trade profit: " + ((profitLong / longTrades)) + "%");
        System.out.println("Average short trade profit: " + ((profitShort / shortTrades)) + "%");
        System.out.println("Average profit per holding period: " + ((profit / Trades.size())) / (holdingPeriod / Trades.size()));
    }
}
