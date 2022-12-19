import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution { 
    //MAIN IDEA: DP MEMOIZATION
    //
    //SETUP:
    //  where each state of the dp includes info for time remaining + the # of robots of each type + # of minerals of each type, 
    //  since each state of this type corresponds to EXACTLY ONE "max # of geodes one can get by starting from this state"
    //  (this is because this state captures all the information at any point of following some excavation plan / path, i.e.
    //  by knowing the information in this state, we can determine the max # of geodes we can get by starting fromt this state)
    //
    //INITIAL DFS CALL:
    //  We can use a variable "max" to record the max # of geodes excavated in any state we iterated so far
    //  and do a dfs starting from 24 (32 for part II) minutes remaining and 1 ore robot,
    //  and no other robots or minerals, and whenever we reach a state with time = 0, 
    //  we set "max" = max("max", # of geodes of this state), we also memoize all visited state in "visited"
    //
    //WHAT EACH DFS CALL DOES:
    //  In each dfs call (this is equivalent to imagining we are at a certain state), 
    //  we can choose to make some new robot or not any, and if we want to make a robot, we need to have
    //  enough minerals. Making each type of robot (or not making any) leads to 
    //  a different state, and these operations all happen within a minute (this sums up all that we can do in 1 minute), 
    //  so we simulate and call dfs on each of those state (also reduce remaining time in each of them by 1).
    //
    //REMAINING ISSUE:
    //However, this is too much to search exhaustively. (around 3 * 10^10, just an estimate, even with memoization, you are
    //  looking at around 3 * 10^12 computations (assuming each dfs call takes 100 computations)
    //  and 4(long in "visited") * 3 * 10^10 bytes of memory = 120 GB or RAM)
    //  which could be optimized a bit through pruning
    //
    //OPTIMIZATION:
    //I.  I did some pruning with the idea that: if we have t minutes remaining, and have g geodes excavated and r geode robots working,
    //      the maximum number of geodes we can have in the end is g + r + (r + 1) ... + (r + t - 1)
    //      since we can build at most 1 geode robot each minute, so the current minute we can get r geodes, next minute
    //      we can get at most (r + 1), until the last minute where we can (r + t - 1), this equals g + t * (2 * r + t - 1) / 2
    //  So if this value <= "max" (the max # of geodes we recorded so far), it's impossible for this state to ever
    //      achieve the glory of "max" (as high as some previous plan / state we encountered), we just return the function immediately
    //  This helped make the code run in around 3 minutes for Part II (however do mind that memory expense is still large,
    //      around 800MB in my case, and I had to use ssh to CSIL computers to run it, shame on my PC).
    //
    //II. Also, in dfs, we try to call dfs on the state for creating geode robots first, then obsedian robot, then clay, then ore, then no new robots,
    //  to try to greedily boost value of "max" in very early iterations so that part I works better (since we set a higher "max" early on).
    //  Intuitively, greedily creating geode robots whenever possible is much better than most of the order of calling dfs (e.g. better than randomly creating
    //      machines, since we might be slower in getting advanced machines and thus excavating fewer geodes)

    //stores (state, maximum geode one can dig from this state)
    static HashSet<Long> visited;

    //cost[i][j] = # of j-th mineral required to build robot i
    static long[][] cost;

    //robots[0..3] records # of ore, clay, obsedian, geode robots in each dfs call
    //minerals[0..3] records # of ore, clay, obsedian, geode minerals in each dfs call
    static long[] robots, minerals;

    //order of recursive dfs call in each dfs (by iterating this array, we try to create geode robots first, then obsedian, etc.)
    static int[] order = {3, 2, 1, 0};
    static long max;

    //used for encoding each state in a long
    public static long[] mult = {12L, 8L, 8L, 7L, 25L, 23L, 20L, 18L};

    //compress the state with time, robots, minerals into a single long
    //  to fit into the key range for "visited"
    //Similar idea to compressing 2d coordinates of (x, y) into x * (max_y - min_y + 1) + y, so that
    //  each (x, y) maps to a unique long (bijective map)
    public static long encode(long time){
        long res = 0L, m = 1L;
        int ind = mult.length - 1;
        for(int i = 3; i >= 0; --i){
            res += minerals[i] * m;
            m *= mult[ind];
            --ind;
        }
        for(int i = 3; i >= 0; --i){
            res += robots[i] * m;
            m *= mult[ind];
            --ind;
        }
        res += time * m;
        return res;
    }

    //dfs function
    public static void dfs(long time){
        long state = encode(time);

        //memoization and pruning
        if(visited.contains(state) || max >= minerals[3] + (time * (robots[3] * 2 + time - 1) / 2)){
            return ;
        }
        //reached the end of our excavation plan (no time remaining)
        if(time == 0){
            max = Math.max(max, minerals[3]);
            return ;
        }
        visited.add(state);

        //candidate_new_robo[i] is true iff we have enough minerals to make robot[i] at this state
        boolean[] candidate_new_robo = new boolean[4];

        //return value
        long m2 = 0L;

        //check which robots we can add
        loop: for(int r = 0; r < 4; ++r){
            //check if enough mineral
            for(int i = 0; i < 4; ++i){
                if(minerals[i] < cost[r][i]){
                    candidate_new_robo[r] = false;
                    continue loop;
                }
            }
            candidate_new_robo[r] = true;
        }

        //setup for backtracking
        for(int i = 0; i < 4; ++i){
            minerals[i] += robots[i];
        }

        //prioritize adding robot, and adding more advanced robot first
        for(int r : order){
            if(candidate_new_robo[r]){
                for(int i = 0; i < 4; ++i){
                    minerals[i] -= cost[r][i];
                }
                robots[r]++;
                dfs(time - 1);
                for(int i = 0; i < 4; ++i){
                    minerals[i] += cost[r][i];
                }
                robots[r]--;
            }
        }

        //dfs call foradding no robot
        dfs(time - 1);

        //reset for backtracking
        for(int i = 0; i < 4; ++i){
            minerals[i] -= robots[i];
        }
    }

    public static long solve(String S, long T, boolean count_id){
        //reset everything we use in the dfs
        //  those arrays are only allocated once to save memory
        //  and avoid too much cost in memory allocation
        for(int i = 0; i < 4; ++i){
            Arrays.fill(cost[i], 0L);
        }
        Arrays.fill(robots, 0L);
        Arrays.fill(minerals, 0L);
        robots[0] = 1L;
        visited = new HashSet<>();
        max = 0L;

        String[] toks = split(S, "[Blueprint :.adobygEchs]");
        long[] arr = stol(toks);
        long id = arr[0];

        //fill in mineral cost for creating each robot
        cost[0][0] = arr[1];
        cost[1][0] = arr[2];
        cost[2][0] = arr[3];
        cost[2][1] = arr[4];
        cost[3][0] = arr[5];
        cost[3][2] = arr[6];

        //dfs to compute max value
        dfs(T);
        System.out.println("id: " + id + " max: " + max);

        //count_id is true for Part I, false for Part II
        return count_id ? id * max : max;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        cost = new long[4][4];
        robots = new long[4];
        minerals = new long[4];
        String[] input = read_all_String();
        max = 0L;
        long T1 = 24, T2 = 32;
        long score = 0L;
        //Part I
        time();
        for(String S : input){
            long res = solve(S, T1, true);
            score += res;
        }
        System.out.println("Task 1: " + score);
        time();
        print_time();

        //Part II
        time();
        long score2 = 1L;
        for(int q = 0; q < Math.min(input.length, 3); ++q){
            long res = solve(input[q], T2, false);
            score2 *= res;
        }
        System.out.println("Task 2: " + score2);
        time();
        print_time();
    }
    
    //template
    public static Scanner sc;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

    public static long MAX = (long)(1e6 + 0.1), MOD = 1000000007L,  _timer_ind = 0L;
    public static long[] _timer = new long[2];
    public static void time(){
        _timer[(int)_timer_ind] = System.nanoTime();
        _timer_ind = _timer_ind * -1 + 1;
    }

    //print timer result in the form of a formmated String
    public static void print_time(){
        System.out.println("Time: " + String.format("%.3f", (double)(_timer[1] - _timer[0]) / (double)(1e9)) + " sec");
    }

    //pad character c onto String S until it reaches target_len
    //pad on left if side == 0, right otherwise
    public static String pad(String S, char c, int target_len, int side){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < target_len - S.length(); ++i){
            sb.append(c);
        }
        return side == 0 ? (sb.toString() + S) : (S + sb.toString());
    }

    public static int[][] rotate_90(int[][] arr){
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[j][row - 1 - i] = arr[i][j];
            }
        }
        return res;
    }
    public static int[][] transpose(int[][] arr){
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[j][i] = arr[i][j];
            }
        }
        return res;
    }
    //add all characters of a String into a HashSet
    public static HashSet<Character> setify(String S){
        HashSet<Character> set = new HashSet<>();
        char[] a = S.toCharArray();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }
    public static HashSet<Character> setify(char[] a){
        HashSet<Character> set = new HashSet<>();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }
    @SuppressWarnings("unchecked")
    public static <T> T[] subarray(T[] arr, int st, int end){
        assert(end > st);
        T[] res = (T[]) new Object[end - st];
        for(int i = st; i < end; ++i){
            res[i - st] = arr[i];
        }
        return res;
    }
    public static String[] subarray(String[] arr, int st, int end){
        assert(end > st);
        String[] res = new String[end - st];
        for(int i = st; i < end; ++i){
            String S = "";
            S += arr[i];
            res[i - st] = S;
        }
        return res;
    }
    //split with multiple delimiters and clearing empty String
    public static String[] split(String S, String delim){
        String[] toks = S.split(delim);
        List<String> l = new ArrayList<>();
        for(String cur : toks){
            if(!cur.equals("")){
                l.add(cur);
            }
        }
        String[] res = new String[l.size()];
        for(int i = 0; i < res.length; ++i){
            res[i] = l.get(i);
        }
        return res;
    }

    //parse a String[] into an int[]
    public static int[] stoi(String[] toks){
        int[] arr = new int[toks.length];
        for(int i = 0; i < toks.length; ++i){
            arr[i] = Integer.parseInt(toks[i]);
        }
        return arr;
    }

    public static long[] stol(String[] toks){
        long[] arr = new long[toks.length];
        for(int i = 0; i < toks.length; ++i){
            arr[i] = Long.parseLong(toks[i]);
        }
        return arr;
    }

    //read all input in the form of a List of String
    public static List<String> read_input(){
        try{
            sc = new Scanner(new File("input.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");        
        }
        List<String> l = new ArrayList<>();
        while(sc.hasNextLine()){
            String S = read();
            l.add(S);
        }
        return l;
    }

    //read all input into a 2d char array
    public static char[][] read_all_char_arr(){
        List<String> l = read_input();
        char[][] arr = new char[l.size()][l.get(0).length()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i).toCharArray();
        }
        return arr;
    }
    //read all input into an int array
    public static int[] read_all_int(){
        List<String> l = read_input();
        int[] arr = new int[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = Integer.parseInt(l.get(i));
        }
        return arr;
    }
    //read all input into a 2d int array
    public static int[][] read_all_int_arr(){
        char[][] arr = read_all_char_arr();
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[row][col];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[i][j] = (int)(arr[i][j] - '0');
            }
        }
        return res;
    }
    //read all input into a String array
    public static String[] read_all_String(){
        List<String> l = read_input();
        String[] arr = new String[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i);
        }
        return arr;
    }

    public static <T> void print(T[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static <T> void print(T[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa.length; ++j)
                System.out.print(aa[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }
    public static void print(int[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static void print(boolean[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static void print(long[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static void print(char[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i]);
        System.out.println();
    }
    public static void print(int[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j)
                System.out.print(aa[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }
    public static void print(long[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j)
                System.out.print(aa[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }
    public static void print(int[][][] aa){
        for(int k = 0; k < aa[0][0].length; ++k){
            for(int i = 0; i < aa.length; ++i){
                for(int j = 0; j < aa[0].length; ++j){
                    System.out.print(aa[i][j][k] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println();
    }
    public static void print(char[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j)
                System.out.print(aa[i][j]);
            System.out.println();
        }
        System.out.println();
    }
    public static <T, U> void print(HashMap<T, U> map){
        for(T key : map.keySet()){
            System.out.println(key + " : " + map.get(key));
        }
        System.out.println();
    }
    public static <T> void print(HashSet<T> set){
        System.out.print("{");
        for(T key : set){
            System.out.print(key + ", ");
        }
        System.out.print("}");
        System.out.println();
    }
    public static <T> void print(List<T> list){
        System.out.print("[");
        for(T ele : list){
            System.out.print(ele + ", ");
        }
        System.out.print("]");
        System.out.println();
    }
    public static class Pair<_U, _T> { 
        _U a;
        _T b;
        public Pair(_U ap, _T bp){
            a = ap;
            b = bp;
        }
    }
    public static class Triple<_U, _T, _F>{
        _U a;
        _T b;
        _F c;
        public Triple(_U ap, _T bp, _F cp){
            a = ap;
            b = bp;
            c = cp;
        }
    }
    public static int[] dir = {1, 0, -1, 0, 1, 1, -1, -1, 1};
    public static class Union{
        int[] p, size;
        public Union(int n){
            p = new int[n];
            size = new int[n];
            Arrays.fill(size, 1);
            for(int i = 0; i < n; ++i){
                p[i] = i;
            }
        }
        public int find(int n){
            if(p[n] != n){
                p[n] = find(p[n]);
            }
            return p[n];
        }
        public int union(int a, int b){
            int fa = find(a), fb = find(b);
            if(fa == fb){
                return -1;
            }
            if(size[fa] > size[fb]){
                p[fb] = fa;
                size[fa] += size[fb];
                return size[fa];
            }
            else{
                p[fa] = fb;
                size[fb] += size[fa];
                return size[fb];
            }
        }
    }
    public static String read(){
        return sc.nextLine();
    }
    public static String[] read_toks(){
        return sc.nextLine().split(" ");
    }
}