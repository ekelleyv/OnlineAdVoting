import java.io.*;
import java.util.Random;
import java.util.regex.*;

public class Ekelley
{       
        int myId, myVal, budget, numrounds, nagents, remaining_budget;
        int[] valuations;
        int[][] bids;
        int[][] slots;
        int[][] payments;
        int[][] clicks;
        int round;

        //From @311 on Piazza
        void parseHeader(String s) {
            Pattern headerpattern = Pattern.compile("c:(\\d+),(\\d+),(\\d+),(\\d+);\\[([\\d,]+)\\]");
            Matcher m = headerpattern.matcher(s);
            m.matches();

            myId = Integer.parseInt(m.group(1));
            myVal = Integer.parseInt(m.group(2));
            budget = Integer.parseInt(m.group(3));
            numrounds = Integer.parseInt(m.group(4));
            
            String[] vals = m.group(5).split(",");
            nagents = vals.length;
            valuations = new int[nagents];
            for (int i = 0; i < nagents; ++i) {
                valuations[i] = Integer.parseInt(vals[i]);
            }
            // Initialize arrays for bids, slots, payments, clicks
            // Note that bids has size nagents, but slots, payments, clicks have size nagents-1
            bids = new int[numrounds][nagents];
            slots = new int[numrounds][nagents-1];
            payments = new int[numrounds][nagents-1];
            clicks = new int[numrounds][nagents-1];
        }

        //From @311 on Piazza
        void parseRound(String s) {
            Pattern roundpattern = Pattern.compile("b:(\\d+);\\[([\\d,]+)\\],\\[([\\d,]+)\\],\\[([\\d,]+)\\],\\[([\\d,]+)\\]");
            Matcher m = roundpattern.matcher(s);
            m.matches();

            String[] sbids = m.group(2).split(",");
            String[] sslots = m.group(3).split(",");
            String[] spayments = m.group(4).split(",");
            String[] sclicks = m.group(5).split(",");
            for (int i = 0; i < nagents-1; ++i) {
                bids[round-1][i] = Integer.parseInt(sbids[i]);
                slots[round-1][i] = Integer.parseInt(sslots[i]);
                payments[round-1][i] = Integer.parseInt(spayments[i]);
                clicks[round-1][i] = Integer.parseInt(sclicks[i]);
            }
            bids[round][nagents-1] = Integer.parseInt(sbids[nagents-1]);
        }

        int mySlot(int round_number) {
                int slot_number = -1; //didn't get one
                for (int i = 0; i < (nagents-1); i++) {
                        if (slots[round_number][i] == myId) {
                                slot_number = i;
                                return slot_number;
                        }
                }
                return slot_number;
        }

        int playRound(String round_info) {
                int bid_value = 0;
                if (round == 0) {
                        //bid_value = myVal;
                        bid_value = 84;
                        remaining_budget = budget;
                }
                else {
                        parseRound(round_info);
                        remaining_budget -= payments[round-1][myId];
                        int last_slot = mySlot(round-1);
                        int last_bid = bids[round-1][myId];
                        System.out.println("Last slot = " + last_slot + " Last bid = " + last_bid);

                        //Estimated value of each slot based on last turn
                        int[] estimated_util = new int[nagents-1];
                        int max_util_index = nagents-1;
                        int max_util = 0;

                        // System.out.println("i" + " : " + "clickrate" + " - " + "myVal" + " - " + "bids" + " - " + "payments" + " - " + "this_util");

                        for (int i = 0; i < (nagents-1); i++) {
                                int clickrate = (int)clickRate(round, i);
                                int this_util = clickrate*(myVal-payments[round-1][i]);
                                System.out.println(i + " : " + clickrate + " - " + myVal + " - " + bids[round-1][i] + " - " + payments[round-1][i] + " - " + this_util);
                                estimated_util[i] = this_util;
                                if (this_util > max_util) {
                                        max_util_index = i;
                                        max_util = this_util;
                                }
                        }


                        System.out.println("Best index is " + max_util_index);
                        System.out.println("With a max_util of " + max_util);
                        
                        
                }

                round++;
                return bid_value;
        }

        int clickRate(int round_number, int slot) {
                int c0 = (int)Math.round(30*Math.cos((2*Math.PI*round_number)/numrounds) + 50);
                return (int)(Math.pow(0.75, slot)*c0);
        }

        public static void main(String[] args) throws java.io.IOException
        {       
                Ekelley auction = new Ekelley();
                auction.round = 0;
                BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
                Random random = new Random();
                String header=in.readLine();
                auction.parseHeader(header);
                while (true)
                {       

                        String round_info = in.readLine();
                        System.out.println(auction.playRound(round_info));
                }
        }
}

