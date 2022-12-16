import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static int[][][] dp;
    public static int[][] adj;
    public static int[] flow, dist_from_AA;
    public static int max_flow, N, max_dist_pos_flow_valves, max_dist_AA;
    public static int flow_sum(int mask){
        int res = 0;
        for(int i = 0; i < flow.length; ++i){
            if(((1 << i) & mask) > 0){
                res += flow[i];
            }
        }
        return res;
    }
    public static int dfs(int time, int cur, int mask, int allowed_mask){
        if(dp[time][cur][mask] >= 0){
            return dp[time][cur][mask];
        }
        //max flow if we do nothing starting from now
        dp[time][cur][mask] = flow_sum(mask) * time;

        for(int i = 0; i < N; ++i){
            int d = adj[cur][i] + 1;
            //valves that are already open or not allowed to visit or not enough time to reach valve and turn it on
            if(((1 << i) & mask) > 0 || ((1 << i) & allowed_mask) == 0 || d > time)
                continue;
            dp[time][cur][mask] = Math.max(dp[time][cur][mask], dfs(time - d, i, mask + (1 << i), allowed_mask) + flow_sum(mask) * d);
        }
        max_flow = Math.max(max_flow, dp[time][cur][mask]);
        return dp[time][cur][mask];
    }

    public static int compute_flow(int allowed_mask, int tot_time){
        max_flow = 0;
        //make dp[0] equal to 0 (0 time, no flow can be obtained), and the rest as -1 (representing not computed yet)
        for(int i = 1; i < 31; ++i){
            for(int j = 0; j < N; ++j){
                Arrays.fill(dp[i][j], -1);
            }
        }
        //since we start at AA, we have to first move from AA to vertices with positive flow
        //then dfs, all valves are allowed to be used
        for(int i = 0; i < N; ++i){
            if(((1 << i) & allowed_mask) > 0)
                dfs(tot_time - dist_from_AA[i] - 1, i, 1 << i, allowed_mask);
        }
        return max_flow;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        String[] input = read_all_String();
        N = input.length;
        max_flow = 0;
        int AA_index = 0;
        adj = new int[N][N];
        List<Triple<Integer, Integer, Integer>> pos_flow = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();
        for(int i = 0; i < N; ++i){
            String[] toks = split(input[i], "[ =;,rateflowvunsdh]");
            map.put(toks[1], i);
            if(toks[1].equals("AA")){
                AA_index = i;
            }
            int f = Integer.parseInt(toks[2]);
            if(f > 0){
               pos_flow.add(new Triple<>(pos_flow.size(), i, f));
            }
        }
        for(int i = 0; i < N; ++i){
            String[] toks = split(input[i], "[ =;,rateflowvunsdh]");
            for(int j = 3; j < toks.length; ++j){
                //System.out.println(i + " " + map.get(toks[j]));
                adj[i][map.get(toks[j])] = 1;
            }
        }
        //compute shortest distance between all pairs of points
        //  and compress graph to only vertices with >0 flow (as we'll never open valves with 0 flow)
        //  So we only use those to reach vertices with >0 flow (per Prof. Sotomayor's idea)
        int[][] dist = new int[N][N];
        for(int i = 0; i < N; ++i)
            Arrays.fill(dist[i], 10000000);
        for(int i = 0; i < N; ++i){
            dist[i][i] = 0;
            for(int j = 0; j < N; ++j){
                if(adj[i][j] > 0){
                    dist[i][j] = 1;
                }
            }
        }
        for(int k = 0; k < N; ++k){
            for(int i = 0; i < N; ++i){
                for(int j = 0; j < N; ++j){
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                }
            }
        }
        //print(dist);
        
        //compress graph
        N = pos_flow.size();
        adj = new int[N][N];
        flow = new int[N];
        dist_from_AA = new int[N];
        for(Triple<Integer, Integer, Integer> src : pos_flow){
            flow[src.a] = src.c;
            dist_from_AA[src.a] = dist[AA_index][src.b];
            max_dist_AA = Math.max(max_dist_AA, dist_from_AA[src.a]);
            for(Triple<Integer, Integer, Integer> dest : pos_flow){
                adj[src.a][dest.a] = dist[src.b][dest.b];
                max_dist_pos_flow_valves = Math.max(max_dist_pos_flow_valves, adj[src.a][dest.a]);
            }
        }
        //print(adj);
        //print(flow);

        //dp[i][j][k] = max flow one can obtain by being at valve j with valves in k (bitmasked)
        //  already open and with i minutes remaining
        dp = new int[31][N][1 << N];
        int all_mask = (1 << N) - 1;
        
        System.out.println("Task 1: " + compute_flow(all_mask, 30));
        int mask_2_flow = 0;

        for(int person_mask = 0; person_mask < (1 << (N - 1)); ++person_mask){
            if(person_mask % 1000 == 0){
                System.out.println("Iter: " + person_mask);
            }
            mask_2_flow = Math.max(mask_2_flow, compute_flow(person_mask, 26) + compute_flow(all_mask - person_mask, 26));
        }
        System.out.println("Task 2: " + mask_2_flow);
    }
    

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

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