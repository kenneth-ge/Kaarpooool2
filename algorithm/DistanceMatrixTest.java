import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;

import static java.lang.Math.*;

public class DistanceMatrixTest {

    public static long[][] distances;
    public static int[][] prev;
    public static long[][] dp;

    public static final long INF = Long.MAX_VALUE / 2;

    public static int n;

    public static void main(String[] args) throws Exception {
        String[] locations = new String[] {
                "200 White Oak Ln, Scarsdale, NY 10583",
                "30 Highview Dr, Scarsdale, NY 10583",
                "6 Cherrywood Rd, Scarsdale, NY 10583",
                "2 Penny Ln, Scarsdale, NY 10583"
        };

        boolean[] driver = new boolean[] {
                false,
                true,
                false
        };

        calculate(locations, driver);
    }

    public static void calculate(String[] locations, boolean[] driver) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIz.....")
                .build();

        DistanceMatrix request = DistanceMatrixApi.getDistanceMatrix(context,
                locations, locations).await();

        n = locations.length;

        distances = new long[n][n];

        System.out.println("---Begin matrix---");
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                distances[i][j] = request.rows[i].elements[j].duration.inSeconds;
                System.out.print(distances[i][j] + " ");
            }
            System.out.println();
        }

        dp = new long[1 << n][n];
        prev = new int[1 << n][n];

        for(int i = 0; i < (1 << n); i++){
            for(int j = 0; j < n; j++){
                dp[i][j] = INF;
                prev[i][j] = -1;
            }
        }

        for(int i = 0; i < n; i++){
            dp[1 << i][i] = 0;
            dp[0][i] = 0;
            prev[1 << i][i] = -1;
        }

        calculateTravelingSalesman();

        for(int i = 0; i < (1 << n); i++){
            System.out.println("---" + Integer.toBinaryString(i) + "---");
            for(int j = 0; j < n; j++){
                System.out.println(j + ": " + prev[i][j]);
            }
        }

        int min_ending = -1;
        long min_dist = INF;

        for(int i = 0; i < n; i++){
            if(dp[(1 << n) - 1][i] < min_dist){
                min_dist = dp[(1 << n) - 1][i];
                min_ending = i;
            }
        }

        System.out.println(min_dist);

        System.out.println("Start path");
        int last = min_ending;
        int mask = (1 << n) - 1;
        while(last != -1){
            System.out.println(last);

            int oldmask = mask;
            int oldlast = last;

            mask = oldmask & (~(1 << oldlast));
            last = prev[oldmask][oldlast];
        }

        context.shutdown();
    }

    public static void calculateTravelingSalesman() {
        for(int mask = 0; mask < (1 << n); mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0)
                    continue;

                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0)
                        continue;

                    int new_msk = mask | (1 << next);

                    long new_dist = dp[mask][last] + distances[last][next];

                    if(new_dist < dp[new_msk][next]){
                        dp[new_msk][next] = new_dist;
                        prev[new_msk][next] = last;
                    }
                }
            }
        }
    }

}
