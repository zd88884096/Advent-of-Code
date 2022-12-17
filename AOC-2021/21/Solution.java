import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static long[][][][][][] dp;

    //simulate player "turn"'s turn with two players at i (has i[0] and i[1]) with score j (with j[0] and j[1])
    //  while computing how many times p win
    public static long dfs(int[] i, int[] j, int p, int turn){
        /*System.out.println("i:");
        print(i);
        System.out.println("j:");
        print(j);*/

        //since we only simulate one player's turn per call to dfs
        //  at each call either j[0] or j[1] >= 21 but not both
        //  We determine if p won by checking if j[p] >= 21
        if(j[0] >= 21 || j[1] >= 21){
            return j[p] >= 21 ? 1L : 0L;
        }
        //memoization step
        if(dp[i[0]][i[1]][j[0]][j[1]][p][turn] >= 0){
            return dp[i[0]][i[1]][j[0]][j[1]][p][turn];
        }
        long ans = 0L;
        //iterate all possible scores
        for(int r1 = 1; r1 <= 3; ++r1){
            for(int r2 = 1; r2 <= 3; ++r2){
                for(int r3 = 1; r3 <= 3; ++r3){
                    int dice_sum = r1 + r2 + r3;
                    int[] i_new = {i[0], i[1]};
                    i_new[turn] = (i_new[turn] + dice_sum) % 10;
                    int[] j_new = {j[0], j[1]};
                    //need to add one to new score array since the range of points is 1-10, but we get 0-9 with % 10
                    j_new[turn] = j_new[turn] + i_new[turn] + 1;
                    // -1 * turn + 1 essentially reverses turn between 0 and 1
                    ans += dfs(i_new, j_new, p, -1 * turn + 1);
                }
            }
        }
        //System.out.println();
        dp[i[0]][i[1]][j[0]][j[1]][p][turn] = ans;
        return ans;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        String[] input = read_all_String();
        int[] cur = new int[2], score = new int[2];
        int dice = 1, rolled = 0;
        for(int i = 0; i < 2; ++i){
            String[] toks = split(input[i], " ");
            //falls in range 0-9..., easier to deal with when doing mod 10 later...
            cur[i] = Integer.parseInt(toks[toks.length - 1]) - 1;
        }

        //dp[i1][i2][j1][j2][p][turn] = how many universes player p wins by being at position i1 with score j1 while opponent is at 
        //  position i2 with score j2 and currently it's player "turn"'s turn
        dp = new long[10][10][21][21][2][2];
        for(int i1 = 0; i1 < 10; ++i1)
            for(int i2 = 0; i2 < 10; ++i2)
                for(int j1 = 0; j1 < 21; ++j1)
                    for(int j2 = 0; j2 < 21; ++j2)
                        for(int p = 0; p < 2; ++p)
                            Arrays.fill(dp[i1][i2][j1][j2][p], -1L);

        int[] i_arr_1 = {cur[0], cur[1]}, j_arr_1 = {0, 0}, i_arr_2 = {cur[0], cur[1]}, j_arr_2 = {0, 0};
        long p1 = dfs(i_arr_1, j_arr_1, 0, 0), p2 = dfs(i_arr_2, j_arr_2, 1, 0);
        System.out.println("Task 2: " + Math.max(p1, p2));
        /*Part I
        loop: while(true){
            for(int i = 0; i < 2; ++i){
                cur[i] += dice; dice = (dice + 1) % 100;
                cur[i] += dice; dice = (dice + 1) % 100;
                cur[i] += dice; dice = (dice + 1) % 100;
                rolled += 3;
                //falls in range 0-9...
                cur[i] %= 10;
                //need to add one to cur[i] since the range of points is 1-10...
                score[i] += (cur[i] + 1);
                if(score[i] >= 1000){
                    System.out.println("Task 1: " + (score[-1 * i + 1] * rolled));
                    break loop;
                }
            }
        }*/

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