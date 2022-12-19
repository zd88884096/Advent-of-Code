import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    static HashMap<Long, Long> map;
    static long[][] cost;
    static long[] robots, minerals;
    static int[] order = {3, 2, 1, 0};
    static long max, iter;
    public static long[] mult = {12L, 8L, 8L, 7L, 25L, 23L, 20L, 18L};
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
    public static long dfs(long time){
        ++iter;
        if(iter % 1000000 == 0L){
            System.out.println("iter: " + iter + " max: " + max);
        }
        long lim = 40;
        if(time == lim){
            print(robots);
            print(minerals);
            System.out.println();
        }
        long state = encode(time);
        if(map.containsKey(state)){
            return map.get(state);
        }
        if(time == 0){
            //map.put(state, minerals[3]);
            max = Math.max(max, minerals[3]);
            return minerals[3];
        }
        boolean[] candidate_new_robo = new boolean[4];
        long m2 = 0L;
        //pruning
        if(max - minerals[3] >= (time * (robots[3] * 2 + time - 1) / 2)){
            return max;
        }
        //check which robots we can add
        loop: for(int r = 3; r >= 0; --r){
            //check if enough mineral
            for(int i = 0; i < 4; ++i){
                if(minerals[i] < cost[r][i]){
                    candidate_new_robo[r] = false;
                    continue loop;
                }
            }
            if(time == lim)
                System.out.println("robo: " + r);
            candidate_new_robo[r] = true;
        }

        if(time == lim)
            System.out.println("check 1:");
        //no new robots
        for(int i = 0; i < 4; ++i){
            minerals[i] += robots[i];
        }
        //prioritize adding robot, and adding more advanced robot
        for(int r : order){
            if(time == lim)
                print(candidate_new_robo);
            if(candidate_new_robo[r]){
                if(time == lim)
                    System.out.println("check 3:");
                for(int i = 0; i < 4; ++i){
                    minerals[i] -= cost[r][i];
                }
                robots[r]++;
                if(time == lim){
                    System.out.println("in: " + r);
                }
                m2 = Math.max(m2, dfs(time - 1));
                for(int i = 0; i < 4; ++i){
                    minerals[i] += cost[r][i];
                }
                robots[r]--;
            }
        }
        m2 = Math.max(m2, dfs(time - 1));
        if(time == lim)
            System.out.println("check 2:");
        //some new robot
        //reset for backtracking
        for(int i = 0; i < 4; ++i){
            minerals[i] -= robots[i];
        }
        map.put(state, m2);
        return m2;
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
        /*for(String S : input){
            for(int i = 0; i < 4; ++i){
                Arrays.fill(cost[i], 0L);
            }
            Arrays.fill(robots, 0L);
            Arrays.fill(minerals, 0L);
            robots[0] = 1L;
            map = new HashMap<>();
            max = 0L;
            iter = 0L;

            String[] toks = split(S, "[Blueprint :.adobygEchs]");
            //print(toks);
            long[] arr = stol(toks);
            long id = arr[0];
            cost[0][0] = arr[1];
            cost[1][0] = arr[2];
            cost[2][0] = arr[3];
            cost[2][1] = arr[4];
            cost[3][0] = arr[5];
            cost[3][2] = arr[6];
            //print(cost);
            dfs(T1);
            score += id * max;
            System.out.println(max);
        }
        System.out.println("Task 1: " + score);*/
        long score2 = 1L;
        for(int q = 0; q < Math.min(input.length, 3); ++q){
            String S = input[q];
            for(int i = 0; i < 4; ++i){
                Arrays.fill(cost[i], 0L);
            }
            Arrays.fill(robots, 0L);
            Arrays.fill(minerals, 0L);
            robots[0] = 1L;
            map = new HashMap<>();
            max = 0L;
            iter = 0L;

            String[] toks = split(S, "[Blueprint :.adobygEchs]");
            //print(toks);
            long[] arr = stol(toks);
            long id = arr[0];
            cost[0][0] = arr[1];
            cost[1][0] = arr[2];
            cost[2][0] = arr[3];
            cost[2][1] = arr[4];
            cost[3][0] = arr[5];
            cost[3][2] = arr[6];
            dfs(T2);
            score2 *= max;
            System.out.println(max);
        }
        System.out.println("Task 2: " + score2);
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