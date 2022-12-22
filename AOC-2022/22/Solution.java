import java.util.*;
import java.io.*;
import java.lang.Math;
//Idea: (Painful) Simulation: use r, c, dir to record the current row, column, direction
//  Part I: If taking a step would bring you to a rock, stop and do the next instruction (guaranteed to be a turning instruction)
//      We use "re" and "ce" to record the indices of the two ends of each row and each column respectively (ranges with only '.' or '#' in them)
//      for example, if i-th row is "   .##. ", the left end of the range with '.' or '#' is column 3, right end is column 6.
//      re[i][0] is the column for left end of row i, re[i][1] is the column for right end of row i
//          For the example above, we would set re[i][0] = 3, re[i][1] = 6.
//      ce[j][0] is the row for top end of column j, ce[j][1] is the row for bottom end of column j
//
//  Part II: Create a cube by hand...
public class Solution {
    public static void solve(boolean part1){
        String[] input = read_all_String();
        String[] bo = subarray(input, 0, input.length - 2);
        //# of columns for the board is max length of each String representing a row in input[0..(input.length - 2)]
        int row = bo.length, col = 0;
        for(String S : bo){
            col = Math.max(col, S.length());
        }

        //create board, re and ce...
        char[][] board = new char[row][col];
        int[][] re = new int[row][2], ce = new int[col][2];
        for(int i = 0; i < row; ++i){
            char[] s = bo[i].toCharArray();
            Arrays.fill(re[i], -1);
            for(int j = 0; j < s.length; ++j){
                board[i][j] = s[j];
            }
            for(int j = s.length; j < col; ++j){
                board[i][j] = ' ';
            }
            for(int j = 0; j < col; ++j){
                if(board[i][j] != ' '){
                    if(re[i][0] == -1){
                        re[i][0] = j;
                    }
                    re[i][1] = j;
                }
            }
        }
        for(int j = 0; j < col; ++j){
            Arrays.fill(ce[j], -1);
            for(int i = 0; i < row; ++i){
                if(board[i][j] != ' '){
                    if(ce[j][0] == -1){
                        ce[j][0] = i;
                    }
                    ce[j][1] = i;
                }
            }
        }
        int dir = 3, r = 0, c = re[0][0];

        //turn the instructions to a list of individual instructions, move_ins has all instructions for walking, dir_ins has all those for turning
        String[] move_ins = split(input[input.length - 1], "[LR]"), dir_ins = split(input[input.length - 1], "[0123456789]");
        List<String> l = new ArrayList<>();
        for(int i = 0; i < dir_ins.length; ++i){
            l.add(move_ins[i]);
            l.add(dir_ins[i]);
        }
        //one walking instruction in the end... (edge case)
        if(move_ins.length > dir_ins.length){
            l.add(move_ins[move_ins.length - 1]);
        }
        for(String ins : l){
            //dir_card's direction is down, left, right, up, for i in [0..3] and change of r, c being dir_card[i, i + 1]
            //turning left is like (dir + 3) % 4, can prove by writing down all cases...
            if(ins.charAt(0) == 'L'){
                dir = (dir + 3) % 4;
            }
            //turning right is light (dir + 1) % 4
            else if(ins.charAt(0) == 'R'){
                dir = (dir + 1) % 4;
            }
            else{
                for(int i = 0; i < Integer.parseInt(ins); ++i){
                    //coordinates and direction for after executing the move...
                    int tr = r + dir_card[dir], tc = c + dir_card[dir + 1], dirc = dir;

                    //need to wrap around, either to the ends of row / column or around the cube
                    if(tr < 0 || tr >= row || tc < 0 || tc >= col || board[tr][tc] == ' '){
                        //part1: wrap around ends of row / column
                        if(part1){
                            //goes too low, need to reappear on the top end (of the range with only '.' and '#' in it) of the same column
                            if(tr == r + 1){
                                tr = ce[c][0];
                            }
                            //goes too high ... reappear on bottom
                            else if(tr == r - 1){
                                tr = ce[c][1];
                            }
                            //goes too far right ... reappear on left
                            else if(tc == c + 1){
                                tc = re[r][0];
                            }
                            //goes too far left ... reappear on right
                            else{
                                tc = re[r][1];
                            }
                        }
                        //part2: wrap around the cube
                        else{
                            if(tr == r + 1){
                                if(c < col / 3){
                                    tr = 0; tc = 100 + c; dirc = 0;
                                }
                                else if(c < 2 * col / 3){
                                    tr = 150 + c - 50; tc = 49; dirc = 1;
                                }
                                else{
                                    tr = 50 + (c - 100); tc = 99; dirc = 1;
                                }
                            }
                            else if(tr == r - 1){
                                if(c < col / 3){
                                    tr = c + 50; tc = 50; dirc = 3;
                                }
                                else if(c < 2 * col / 3){
                                    tr = 150 + c - 50; tc = 0; dirc = 3;
                                }
                                else{
                                    tr = 199; tc = c - 100; dirc = 2;
                                }
                            }
                            else if(tc == c + 1){
                                if(r < row / 4){
                                    tr = 100 + (49 - r); tc = 99; dirc = 1;
                                }
                                else if(r < row / 2){
                                    tr = 49; tc = 100 + r - 50; dirc = 2;
                                }
                                else if(r < row * 3 / 4){
                                    tr = (49 - (r - 100)); tc = 149; dirc = 1;
                                }
                                else{
                                    tr = 149; tc = (50 + r - 150); dirc = 2;
                                }
                            }
                            else{
                                if(r < row / 4){
                                    tr = 100 + (49 - r); tc = 0; dirc = 3;
                                }
                                else if(r < row / 2){
                                    tr = 100; tc = (r - 50); dirc = 0;
                                }
                                else if(r < 3 * row / 4){
                                    tr = (49 - (r - 100)); tc = 50; dirc = 3;
                                }
                                else{
                                    tr = 0; tc = (50 + r - 150); dirc = 0;
                                }
                            }
                        }
                    }
                    //cannot move, break, continue to the next instruction, which is guaranteed to be a turning instruction
                    if(board[tr][tc] == '#'){
                        break;
                    }

                    //update r, c, dir to tr, tc, dirc
                    r = tr; c = tc; dir = dirc;
                }
            }
        }
        long res = 1000 * (long)(r + 1) + 4 * (long)(c + 1) + (dir + 1) % 4;
        System.out.println("Task " + (part1 ? "1: " : "2: ") + res);
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //Part I
        solve(true);
        //Part II
        solve(false);
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