import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    //Idea: Part I: simulate with HashSet, add the coordinates of each tetris as if the bottom block is on row 0 into HashSet[] "tetris"
    //  Also record "top" for the top height after each block stops.
    //  Then for every iteration of the simulation of some block i (0 <= i <= 4), initialize it by moving the values in tetris[i] up by (top + 4)
    //      (problem statement incorrectly said we should initialize 3 above top, but actually it is 4 above)
    //  Then simulate it falling by alternating "turn"

    //Part II: observe whenever we iterate "wind" and return to the start of "wind", the height increase is the same (except the first cycle)
    //  Credit to Prof. Borja Sotomayor's idea
    //  So we compute the height increase and block # increase per cycle (recorded in top_changes and e_changes, respectively), 
    //  and use this to compute the final height
    public static int[][] t1 = {{0, 2}, {0, 3}, {0, 4}, {0, 5}}, t2 = {{0, 3}, {1, 2}, {1, 3}, {1, 4}, {2, 3}}, t3 = {{0, 2}, {0, 3}, {0, 4}, {1, 4}, {2, 4}}, t4 = {{0, 2}, {1, 2}, {2, 2}, {3, 2}}, t5 = {{0, 2}, {0, 3}, {1, 2}, {1, 3}};
    //compress int coordinates to a single long
    public static long convert(int a, int b){
        return 7 * (long)a + b;
    }
    //move elements in set vertically, up for "dir" distance
    public static HashSet<Long> move_ver(HashSet<Long> set, long dir){
        HashSet<Long> res = new HashSet<>();
        for(long ele : set){
            res.add(ele + 7 * dir);
        }
        return res;
    }
    //move elements in set horizontally, towards the right by one if c == '>', otherwise left by once
    public static HashSet<Long> move_hor(HashSet<Long> set, char c){
        HashSet<Long> res = new HashSet<>();
        int dir = c == '>' ? 1 : -1;
        for(long ele : set){
            long targ_col = ele % 7 + dir;
            //unmovable, set dir = 0 (res will be have the same values as set due to 0 offset)
            if(targ_col < 0 || targ_col >= 7){
                dir = 0;
                break;
            }
        }
        for(long ele : set){
            res.add(ele + dir);
        }
        return res;
    }
    //true if m1 intersects with m2 (made sure m1 is smaller than m2 in my code in "main")
    //  but could be easily optimized to make m1 smaller than m2 (just swap them, then swap back after finished, more efficient).
    public static boolean intersect(HashSet<Long> m1, HashSet<Long> m2){
        for(long ele : m1){
            if(m2.contains(ele)){
                return true;
            }
        }
        return false;
    }
    //max height of elements in a set
    public static long max_height(HashSet<Long> set){
        long max = 0L;
        for(long ele : set){
            max = Math.max(max, ele / 7);
        }
        return max;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        long N = 100000L;
        char[] wind = read_all_String()[0].toCharArray();
        //5 type of tetris block's coordinate compressed and stored in HashSet, with the bottom block at
        //  row 0, and the vertical position the same as how they appear each time
        List<int[][]> shape = new ArrayList<>();
        shape.add(t1); shape.add(t2); shape.add(t3); shape.add(t4); shape.add(t5); 
        HashSet<Long>[] tetris = new HashSet[5];
        HashSet<Long> rocks = new HashSet<>();
        for(int i = 0; i < 5; ++i){
            tetris[i] = new HashSet<>();
            for(int[] point : shape.get(i)){
                tetris[i].add(convert(point[0], point[1]));
            }
        }
        //fill rocks at row 0
        for(long i = 0; i < 7L; ++i){
            rocks.add(i);
        }
        //old_top and old_e for observing cycle of height increase through cycling through "wind" array
        long top = 0, old_top = 0, ans1 = 0, old_e = -1;
        //wind_ind is for current index of wind in the "wind" array
        //simulate block fall if turn == 1, else simulate wind shift
        int wind_ind = 0, turn = 0;

        //first_10000_top records the top in first 10000 iterations, useful later
        List<Long> top_changes = new ArrayList<>(), e_changes = new ArrayList<>(), first_10000_top = new ArrayList<>();
        for(long _e = 0; _e < N; ++_e){
            //initialize the (_e % 5)-th type of block to initial height of top + 4
            HashSet<Long> block = move_ver(tetris[(int)(_e % 5)], top + 4);
            //to detect whether we reached the start of "wind" again
            boolean cycled_back = false;
            while(true){
                //simulate fall
                if(turn == 1){
                    HashSet<Long> block_fall_once = move_ver(block, -1);
                    --turn;
                    if(intersect(block_fall_once, rocks)){
                        break;
                    }
                    block = block_fall_once;
                }
                //simulate wind shift
                else{
                    //cycled back to the start of wind array, need to record in top_changes and e_changes after simulating
                    //  this block
                    //Since after all our answer for part II depends on how many blocks to simulate, not wind_ind
                    //  it would be better to show the effect each cycle has on "top" by updating top_changes based on cycles on block #, not wind_ind
                    if(wind_ind == 0 && top != 0L){
                        cycled_back = true;
                    }
                    HashSet<Long> block_shift_once = move_hor(block, wind[wind_ind]);
                    ++turn;
                    if(!intersect(block_shift_once, rocks)){
                        block = block_shift_once;
                    }
                    wind_ind = (wind_ind + 1) % wind.length;
                }
            }

            rocks.addAll(block);
            //update top height by maxing the original top height and the top height of elements in current block
            //  when it stops falling
            top = Math.max(top, max_height(block));

            if(_e < 10000)
                first_10000_top.add(top);

            //add the increase of top from each full cycle of wind to "top_changes" to observe cycle
            //add increase of blocks to "e_changes"
            if(cycled_back){
                top_changes.add(top - old_top);
                old_top = top;
                e_changes.add(_e - old_e);
                old_e = _e;
            }

            //storing Part I's answer
            if(_e == 2021L){
                ans1 = top;
            }
        }
        System.out.println("Task 1: " + ans1);

        //observe the cycle manually
        
        //print(top_changes);
        
        //for sample input, each cycle contains 5 elements
        //for real input, each cycle has 1 element only
        
        //we also notice each cycle after the first deals with exactly 1755 blocks, causing the same increase of "top" (2768)
        
        //print(e_changes);

        long N2 = 1000000000000L;
        //compute top for the first cycle (which is an exception)
        N = N2 - e_changes.get(0);
        top = top_changes.get(0);

        //compute top for all full cycles
        top += (N / e_changes.get(1)) * top_changes.get(1);

        //deal with the remainder (leftover part for the last incomplete cycle)
        N %= e_changes.get(1);

        //simulate as if we are on the start of the 3rd cycle (so that we are far from the ill effects
        //  of the first cycle) and simulating the leftover part (from the start of the 3rd cycle "st" to "st + N")
        long st = e_changes.get(0) - 1 + e_changes.get(1), end = st + N;
        top += first_10000_top.get((int)end) - first_10000_top.get((int)st);
        System.out.println("Task 2: " + top);
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