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

            int round = Integer.parseInt(m.group(1));
            String[] sbids = m.group(2).split(",");
            String[] sslots = m.group(3).split(",");
            String[] spayments = m.group(4).split(",");
            String[] sclicks = m.group(5).split(",");
            for (int i = 0; i < nagents-1; ++i) {
                bids[round][i] = Integer.parseInt(sbids[i]);
                slots[round][i] = Integer.parseInt(sslots[i]);
                payments[round][i] = Integer.parseInt(spayments[i]);
                clicks[round][i] = Integer.parseInt(sclicks[i]);
            }
            bids[round][nagents-1] = Integer.parseInt(sbids[nagents-1]);
        }

        int playRound(String round_info) {
                int bid_value;

                if (round == 0) {
                        bid_value = myVal;
                }
                else {
                        parseRound(round_info);
                        bid_value = 20;
                }

                round++;
                remaining_budget -= bid_value;
                return bid_value;
        }

        double clickRate(int round, int slot) {
                int c1 = (int)Math.round(30*Math.cos((2*Math.PI*round)/numrounds) + 50);
                return (int)Math.pow(0.75, round-1)*c1;
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

