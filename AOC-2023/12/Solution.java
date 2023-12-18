import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static class PP{
        String a;
        int b;
        public PP(String ap, int bp){
            a = ap;
            b = bp;
        }
    }
    static boolean all(char[] arr, int st, int end, boolean b){
        if(end == -1){
            return true;
        }
        //System.out.println(st + " " + end);
        char comp = b ? '#' : '.';
        for(int i = st; i <= end; ++i){
            if(arr[i] != comp && arr[i] != '?'){
                return false;
            }
        }
        return true;
    }
    static long solve(char[] arr, int[] nums){
        int n = arr.length, c = nums.length;
        long[][] dp = new long[n][c + 1];
        int[] rs = new int[c];
        rs[0] = nums[0];
        for(int i = 1; i < c; ++i){
            rs[i] = rs[i - 1] + nums[i];
        }
        for(int i = 0; i < n; ++i){
            if(i + 1 >= nums[0] && all(arr, i - nums[0] + 1, i, true) && all(arr, 0, i - nums[0], false)){
                ++dp[i][0];
            }
            for(int j = 1; j < c; ++j){
                if(rs[j] > i){
                    break;
                }
                int k = i - nums[j] + 1;
                if(!all(arr, k, i, true)){
                    continue;
                }
                for(int m = i - nums[j] + 1; m > 1; --m){
                    if(arr[m - 1] == '#'){
                        break;
                    }
                    dp[i][j] += dp[m - 2][j - 1];
                }
            }
        }
        //print(dp);
        long sum = dp[n - 1][c - 1];
        for(int i = n - 2; i >= 0; --i){
            if(arr[i + 1] == '#'){
                break;
            }
            sum += dp[i][c - 1];
        }
        System.out.println(sum);
        System.out.println();
        return sum;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        FILENAME = args[0];
        long res = 0L;
        loop: for(String S : read_all_String()){
            String[] toks = S.split(" ");
            char[] arr = toks[0].toCharArray();
            int[] nums = stoi(split(toks[1], ","));
            arr = dup(arr, 5, '?');
            nums = dup(nums, 5);
            res += solve(arr, nums);
        }
        System.out.println(res);
    }
    public static String FILENAME = "";
    //template
    public static Scanner sc;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

    public static char[] dup(char[] arr, int times, char c){
        int n = arr.length;
        char[] res = new char[n * times + times - 1];
        int ind = 0;
        for(int t = 0; t < times; ++t){
            for(int i = 0; i < n; ++i){
                res[ind] = arr[i];
                ++ind;
            }
            if(t < times - 1){
                res[ind] = c;
                ++ind;
            }
        }
        return res;
    }

    public static char[] dup(char[] arr, int times){
        int n = arr.length;
        char[] res = new char[n * times];
        for(int t = 0; t < times; ++t){
            for(int i = 0; i < n; ++i){
                res[t * n + i] = arr[i];
            }
        }
        return res;
    }

    public static int[] dup(int[] arr, int times){
        int n = arr.length;
        int[] res = new int[n * times];
        for(int t = 0; t < times; ++t){
            for(int i = 0; i < n; ++i){
                res[t * n + i] = arr[i];
            }
        }
        return res;
    }

    public static String[] trim(String[] toks){
        for(int i = 0; i < toks.length; ++i){
            toks[i] = trim(toks[i]);
        }
        return toks;
    }
    public static String trim(String S){
        char[] s = trim(S.toCharArray());
        StringBuilder sb = new StringBuilder();
        for(char c : s){
            sb.append(c);
        }
        return sb.toString();
    }

    public static char[] trim(char[] s){
        int st = 0, end = s.length - 1;
        while(st < s.length){
            if(s[st] != ' '){
                break;
            }
            ++st;
        }
        while(end >= 0){
            if(s[end] != ' '){
                break;
            }
            --end;
        }
        if(st > end){
            char[] res = {};
            return res;
        }
        char[] res = new char[end - st + 1];
        for(int i = 0; i < end - st + 1; ++i){
            res[i] = s[i + st];
        }
        return res;
    }
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

    public static char[][] copy(char[][] arr){
        char[][] res = new char[arr.length][arr[0].length];
        for(int i = 0; i < arr.length; ++i){
            for(int j = 0; j < arr[0].length; ++j){
                res[i][j] = arr[i][j];
            }
        }
        return res;
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

    public static char[][] rotate_90(char[][] arr){
        int row = arr.length, col = arr[0].length;
        char[][] res = new char[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[j][row - 1 - i] = arr[i][j];
            }
        }
        return res;
    }
    public static char[][] transpose(char[][] arr){
        int row = arr.length, col = arr[0].length;
        char[][] res = new char[col][row];
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
    public static HashSet<Integer> setify(int[] a){
        HashSet<Integer> set = new HashSet<>();
        for(int ele : a){
            set.add(ele);
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
    public static boolean equal(char[][] a, char[][] b){
        if(a.length != b.length || a[0].length != b[0].length){
            return false;
        }
        for(int i = 0; i < a.length; ++i){
            for(int j = 0; j < a[0].length; ++j){
                if(a[i][j] != b[i][j]){
                    return false;
                }
            }
        }
        return true;
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
            sc = new Scanner(new File(FILENAME));
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

    public static void print(long[][] aa){
        for(int i = 0; i < aa.length; ++i){
            print(aa[i]);
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