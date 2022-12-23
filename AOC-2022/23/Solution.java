import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    //idea: Part I & II: plain simulation, with coordinate compression (but need to be careful about using "%", since to decompress coordinate storing
    //  info for i and j (row and column) in the form coordinate = i * MAX + j, i and j have to be non-negative (otherwise % gives incorrect output, just
    //  how Java % works, it's safest to make i and j non-negative).
    //So initially when we parse the input, we add MAX / 2 to i and j for all Elves. So even if the Elves move left or up for many iterations, the 
    //  shifted coordinate would still have i and j non-negative, and if we shift all Elves by the same amount initially, it doesn't change their
    //  behavior in the simulation.

    //shift amount for 3 coordinates to check for each proposed direction of moving, in the order N, S, W, E.
    public static int[][][] dirs = {{{-1, -1}, {-1, 0}, {-1, 1}}, {{1, -1}, {1, 0}, {1, 1}}, {{-1, -1}, {0, -1}, {1, -1}}, {{-1, 1}, {0, 1}, {1, 1}}};
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //for computing runtime... can ignore
        time();

        //input board
        char[][] board = read_all_char_arr();

        //stores the compressed coordinates of Elves at each round of the simulation
        HashSet<Long> set = new HashSet<>();
        for(int i = 0; i < board.length; ++i){
            for(int j = 0; j < board[0].length; ++j){
                //shift i and j by MAX / 2 to ensure any Elves's position always have non-negative i and j in the simulation
                if(board[i][j] == '#'){
                    set.add((long)(i + MAX / 2) * MAX + (j + MAX / 2));
                }
            }
        }

        //epoch is just an upper bound of iterations to get the desired answer
        //dir_ind is between [0..3], act as the starting index of "dirs" to start checking in each round
        //  In round 1, dir_ind is 0, so we would check where to move each Elf in the order N, S, W, E.
        //  Next iteration we set dir_ind = (dir_ind + 1) % 4 = 1, so we would check ... in the order S, W, E, N. etc.
        int epoch = 100000, dir_ind = 0;
        for(int _e = 1; _e <= epoch; ++_e){

            //Used to store the compressed coordinate of Elves after moving in this round
            HashSet<Long> next = new HashSet<>();

            //(key, list(long)) stores the list of coordinates that proposed to move to "key" in this round
            HashMap<Long, HashSet<Long>> proposed = new HashMap<>();


            for(long ele : set){
                //initially let next be a copy of "set", later change position of certain Elves if they are
                //  allowed to move this round (the rest have the same position as in "set")
                next.add(ele);
                long i = ele / MAX, j = ele % MAX;

                //see if ele has any neighbor
                boolean no_neighbor = true;
                for(int s = 0; s < 8; ++s){
                    long ti = i + dir_all[s], tj = j + dir_all[s + 1];
                    if(set.contains(ti * MAX + tj)){
                        no_neighbor = false;
                        break;
                    }
                }
                if(no_neighbor)
                    continue;

                //propose ele to move to the first valid direction
                loop: for(int s = dir_ind; s < dir_ind + 4; ++s){
                    int[][] arr = dirs[s % 4];
                    for(int[] shift : arr){
                        long ti = i + shift[0], tj = j + shift[1];
                        //invalid move direction
                        if(set.contains(ti * MAX + tj)){
                            continue loop;
                        }
                    }

                    //valid move direction
                    long ti = i + arr[1][0], tj = j + arr[1][1], coord = ti * MAX + tj;
                    proposed.putIfAbsent(coord, new HashSet<>());
                    proposed.get(coord).add(ele);
                    break;
                }
            }

            //counts how many Elves move in this round
            int ct = 0;

            for(long potential_pos : proposed.keySet()){
                //only one Elf proposed to move to potential_pos, can move the Elf
                if(proposed.get(potential_pos).size() == 1){

                    //this for loop actually just runs once since there's only one element in proposed.get(potential_pos)
                    for(long prev : proposed.get(potential_pos)){
                        next.remove(prev);
                        next.add(potential_pos);
                        ++ct;
                    }
                }
            }

            //No Elves moved, end of simulation
            if(ct == 0){
                System.out.println("Task 2: " + _e);
                time();
                print_time();
                return ;
            }

            //update dir_ind, and update "set" be "next" (so now "set" holds the coordinates of Elves after this round of simulation, ready for
            //  simulation of the next round)
            dir_ind = (dir_ind + 1) % 4;
            set = next;

            //compute answer for part I
            if(_e == 10){
                //limit = {min_i, max_i, min_y, max_y}, which are boundaries of the smallest rectangle containing all coordinates of Elves.
                long[] limit = new long[4];
                limit[0] = Long.MAX_VALUE; limit[2] = Long.MAX_VALUE; limit[1] = Long.MIN_VALUE; limit[3] = Long.MIN_VALUE;
                for(long ele : set){
                    long i = ele / MAX, j = ele % MAX;
                    limit[0] = Math.min(i, limit[0]);
                    limit[1] = Math.max(i, limit[1]);
                    limit[2] = Math.min(j, limit[2]);
                    limit[3] = Math.max(j, limit[3]);
                }

                //empty positions of smallest rectangle containing all coordinates of Elves = area of such smallest rectangle - number of Elves.

                System.out.println("Task 1: " + ((limit[3] - limit[2] + 1) * (limit[1] - limit[0] + 1) - set.size()));
                time();
                print_time();
                time();
            }
        }
    }

    //template
    public static Scanner sc;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

    public static long MAX = (long)(1e7 + 0.1), MOD = 1000000007L,  _timer_ind = 0L;
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