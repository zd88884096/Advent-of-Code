import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    //idea: Simulate, while recognizing the positions for '^' and 'v' reappear every (row - 2) steps (in step 0 and step (row - 2) the positions for 
    //  them are the same), same for '>' and '<' for every (col - 2) steps, thus, the positions for blizzards are the same for every lcm(row - 2, col - 2) steps
    //We can do BFS, with the visited array being 3 dimensional: visited[i][j][k] indicates whether you visited (j, k) at iteration i, where i in [0..lcm(row - 2, col - 2)]
    //  (Since if you visited (j, k) in the 0-th step, it doesn't help at all by being at (j, k) at step lcm(row - 2, col - 2) since it's the same as not moving
    //  throughout a whole cycle of blizzard moving, and assume the contrary, if being at (j, k) at step lcm(row - 2, col - 2) does produce a shortest path
    //  with total step s, then we can instead take the same path as the one between step lcm(row - 2, col - 2) and step s of this path but starting moving
    //  immediately at step 0, and since the blizzards' positions are going to be the same in each step of our new path, we successfully reach the goal in
    //  s - lcm(row - 2, col - 2) < s steps, contradicting that s is the shortest path. (i, j, k) shouldn't be visited.

    //Part I: record everything above and simulate directly

    //Part II: same thing, but recognizing that if shortest path between start and goal is t steps, it is optimal to take this path and start the BFS from goal to start at
    //  step t, since if the optimal path from goal to start (after one went from start to goal) start from u >= t steps, we can simply stay at goal until step
    //  u and then head back (and this strategy will be covered in BFS starting at step t, since we have an option of not moving at each step).

    //for computing lcm
    public static int gcd(int a, int b){
        return b == 0 ? a : gcd(b, a % b);
    }
    //for d in [0..4], (i + dirs[d], j + dirs[d + 1]) represent the possible move from (i, j) onto 4 cardinal directions along with not moving
    public static int[] dirs = {0, -1, 0, 1, 0, 0};
    //cycle is set to lcm(row - 2, col - 2)
    public static int cycle, row, col;
    //unavailable[i][j][k] means at step (i + cycle * some constant), (j, k) is an obstacle (either a wall or a blizzard)
    public static boolean[][][] unavailable;
    //board of the game
    public static char[][] board;
    //compute shortest distance from (st_i, st_j) to (end_i, end_j) starting from time "starting_step" using BFS
    //  returns the ending step of the shortest path starting from time "starting_step"
    public static int compute_dist(int starting_step, int st_i, int st_j, int end_i, int end_j){
        int step = starting_step;
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        boolean[][][] visited = new boolean[cycle][row][col];
        q.add(new Pair<>(st_i, st_j));
        visited[starting_step % cycle][st_i][st_j] = true;
        loop: while(!q.isEmpty()){
            int sz = q.size(), next_ind = (step + 1) % cycle;
            for(int _e = 0; _e < sz; ++_e){
                Pair<Integer, Integer> p = q.poll();
                //reached the destination...
                if(p.a == end_i && p.b == end_j){
                    break loop;
                }
                //simulate potential moves from (p.a, p.b) for the next step
                for(int s = 0; s < 5; ++s){
                    int ti = p.a + dirs[s], tj = p.b + dirs[s + 1];
                    if(ti >= 0 && ti < row && tj >= 0 && tj < col && (!visited[next_ind][ti][tj]) && (!unavailable[next_ind][ti][tj])){
                        visited[next_ind][ti][tj] = true;
                        q.add(new Pair<>(ti, tj));
                    }
                }
            }
            ++step;
        }
        return step;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        time();
        board = read_all_char_arr();
        row = board.length;
        col = board[0].length;
        //(key, value) has "value" being the corresponding index of "key" in "dirs"
        //for instance, '<' has index 0 in dirs, which means for a '<' at position (i, j) it becomes (i + dirs[0], j + dirs[1]) = (i, j - 1) every step
        //recognize '#' as a blizzard too, but one that doesn't move
        HashMap<Character, Integer> dir_map = new HashMap<>();
        dir_map.put('<', 0); dir_map.put('^', 1); dir_map.put('>', 2); dir_map.put('v', 3); dir_map.put('#', 4);
        List<Triple<Integer, Integer, Integer>> obstacles = new ArrayList<>();
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                if(board[i][j] != '.'){
                    obstacles.add(new Triple<>(i, j, dir_map.get(board[i][j])));
                }
            }
        }
        //cycle set to lcm(row - 2, col - 2)
        cycle = (row - 2) * (col - 2) / gcd(row - 2, col - 2);
        unavailable = new boolean[cycle][row][col];
        for(int _e = 0; _e < cycle; ++_e){
            for(Triple<Integer, Integer, Integer> t : obstacles){
                unavailable[_e][t.a][t.b] = true;
                t.a += dirs[t.c];
                t.b += dirs[t.c + 1];
                //if an obstacle is not a wall, it is not allowed to be at the boundary row / col of the board
                if(t.c != 4){
                    if(t.a == 0){
                        t.a = row - 2;
                    }
                    else if(t.a == row - 1){
                        t.a = 1;
                    }
                    else if(t.b == 0){
                        t.b = col - 2;
                    }
                    else if(t.b == col - 1){
                        t.b = 1;
                    }
                }
            }
        }
        int ans1 = compute_dist(0, 0, 1, row - 1, col - 2);
        System.out.println("Task 1: " + ans1);
        //start from time ans1, go back to start, then back to goal again
        //since the inner compute_dist returns the ending step of starting from ans1 and getting back at start, we can
        //  wrap the it inside the outer compute_dist as an input to the outer compute_dist
        int ans2 = compute_dist(compute_dist(ans1, row - 1, col - 2, 0, 1), 0, 1, row - 1, col - 2);
        System.out.println("Task 2: " + ans2);
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